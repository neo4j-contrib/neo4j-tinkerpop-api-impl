package gremlin;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jEdge;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.api.security.AccessMode;
import org.neo4j.kernel.impl.core.GraphProperties;
import org.neo4j.kernel.impl.core.GraphPropertiesProxy;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.PerformsWrites;
import org.neo4j.procedure.Procedure;
import org.neo4j.tinkerpop.api.impl.Neo4jGraphAPIImpl;
import org.neo4j.tinkerpop.api.impl.Neo4jNodeImpl;
import org.neo4j.tinkerpop.api.impl.Neo4jRelationshipImpl;

import javax.script.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author mh
 * @since 08.04.16
 */
public class Gremlin {

    public static final Object[] NO_OBJECTS = new Object[0];
    @Context
    public GraphDatabaseAPI db;

    @Context
    public KernelTransaction ktx;

    @Context
    public Log log;

    private static final GraphPropertiesProxy NO_GRAPH_PROPERTIES = new GraphPropertiesProxy(null);
    private static GraphProperties graphProperties = NO_GRAPH_PROPERTIES;

    private static ThreadLocal<ScriptEngine> engine = new ThreadLocal<>();

    private GraphProperties graphProperties() {
        if (graphProperties == NO_GRAPH_PROPERTIES)
            graphProperties = db.getDependencyResolver().resolveDependency(NodeManager.class).newGraphProperties();
        return graphProperties;
    }
    private ScriptEngine getEngine() {
        if (engine.get() == null) {
            ScriptEngine gremlin = new GremlinGroovyScriptEngine();
            //new ScriptEngineManager().getEngineByName("gremlin-groovy");
            addFunctions(gremlin,
                    "def label(s) { return org.neo4j.graphdb.DynamicLabel.label(s)}",
                    "def type(s) { return org.neo4j.graphdb.DynamicRelationshipType.withName(s)}");
            engine.set(gremlin);
        }
        return engine.get();
    }

    private void addFunctions(ScriptEngine engine, String...script) {
        try {
            for (String s : script) {
                if (s==null) continue;
                engine.eval(s);
            }
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Procedure
    @PerformsWrites
    public Stream<Result> runFunction(@Name("name") String name, @Name("params") List params) {
        try {
            ScriptEngine engine = getEngine();
            Object function = engine.get(name);
            Bindings bindings = engine.createBindings();
            Neo4jGraph neo4jGraph = Neo4jGraph.open(new Neo4jGraphAPIImpl(db));
            GraphTraversalSource traversal = neo4jGraph.traversal();
            bindings.put("db", db);
            bindings.put("graph", neo4jGraph);
            bindings.put("g", traversal);
            bindings.put("log", log);
            if (function == null) {
                String code = (String) graphProperties().getProperty(name, null);
                if (code == null)
                    throw new RuntimeException("Function " + name + " not defined, use CALL function('name','code') ");
                else {
                    engine.eval(code);
                }
            }
            Object value = ((Invocable) engine).invokeFunction(name, params == null ? NO_OBJECTS : params.toArray());
            return mapResults(value);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Procedure
    @PerformsWrites
    public Stream<Result> run(@Name("code") String code, @Name("params") Map<String,Object> params) {
        try {
            ScriptEngine engine = getEngine();
            Bindings bindings = engine.createBindings();
            if (params != null) bindings.putAll(params);
            bindings.put("db", db);
            // todo cache
            Neo4jGraph neo4jGraph = Neo4jGraph.open(new Neo4jGraphAPIImpl(db));

            bindings.put("graph", neo4jGraph);
            bindings.put("g", neo4jGraph.traversal());
            bindings.put("log", log);
            Object value = engine.eval(code,bindings);
            return mapResults(value);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    protected Stream<Result> mapResults(Object value) {
        if (value instanceof Traversal) {
            return ((Traversal)value).toStream().map(Gremlin::toResult);
        }
        if (value instanceof Object[]) {
            return Stream.of((Object[]) value).map(Gremlin::toResult);
        }
        if (value instanceof Iterable) {
            return StreamSupport.stream(((Iterable<?>)value).spliterator(),false).map(Gremlin::toResult);
        }
        return Stream.of(toResult(value));
    }

    static Result toResult(Object value) {
        return new Result(convert(value));
    }

    private static Object convert(Object value) {
        if (value instanceof Neo4jVertex) {
            return ((Neo4jNodeImpl)((Neo4jVertex)value).getBaseVertex()).getEntity();
        }
        if (value instanceof Neo4jEdge) {
            return ((Neo4jRelationshipImpl)((Neo4jEdge)value).getBaseEdge()).getEntity();
        }
        if (value instanceof Set) {
            Set set = (Set) value;
            return convert(new LinkedHashSet<>(set.size()), set.iterator());
        }
        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            return convert(new ArrayList<>(coll.size()), coll.iterator());
        }
        if (value instanceof Iterable) {
            Iterable iter = (Iterable) value;
            return convert(new ArrayList(100), iter.iterator());
        }
        if (value instanceof Iterator) {
            Iterator iter = (Iterator) value;
            return convert(new ArrayList(100), iter);
        }
        return value;
    }

    private static <T extends Iterator,V extends Collection> V convert(V result, T input) {
        while (input.hasNext()) {
            result.add(convert(input.next()));
        }
        return result;
    }

    @Procedure
    @PerformsWrites
    public Stream<Operation> function(@Name("name") String name, @Name("code") String code) {
        try (KernelTransaction.Revertable access = ktx.restrict(AccessMode.Static.FULL)) {
            ScriptEngine js = getEngine();

            js.eval(code);
            GraphProperties props = graphProperties();
            boolean replaced = props.hasProperty(name);
            props.setProperty(name, code);
            return Stream.of(new Operation(name,replaced ? "Updated" : "Added"));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Procedure
    public Stream<Result> list() {
        return StreamSupport.stream(graphProperties.getPropertyKeys().spliterator(), false).map(Result::new);
    }

    public static class Operation {
        public String function;
        public String operation;

        public Operation(String function, String operation) {
            this.function = function;
            this.operation = operation;
        }


    }
    public static class Result {
        public Object value;

        public Result(Object value) {
            this.value = value;
        }

    }
}
