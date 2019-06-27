package gremlin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jEdge;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.tinkerpop.api.impl.Neo4jGraphAPIImpl;
import org.neo4j.tinkerpop.api.impl.Neo4jNodeImpl;
import org.neo4j.tinkerpop.api.impl.Neo4jRelationshipImpl;
import org.neo4j.tinkerpop.api.impl.PropertyConverter;

/**
 * @author mh
 * @since 08.04.16
 */
public class Gremlin {

    public static final Object[] NO_OBJECTS = new Object[0];
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    private static GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
    static {
        addFunctions(engine,
                "def label(s) { return org.neo4j.graphdb.DynamicLabel.label(s)}",
                "def type(s) { return org.neo4j.graphdb.DynamicRelationshipType.withName(s)}");

    }

    private GremlinGroovyScriptEngine getEngine() {
        return engine;
    }

    private static void addFunctions(ScriptEngine engine, String... script) {
        try {
            for (String s : script) {
                if (s==null) continue;
                engine.eval(s);
            }
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Procedure(mode = Mode.WRITE)
    @Description("gremlin.run(code) - runs gremlin script, with optional parameters")
    public Stream<Result> run(@Name("code") String code, @Name(value="params", defaultValue="null") Map<String, Object> params) throws ScriptException {
        ScriptEngine engine = getEngine();
        Bindings bindings = engine.createBindings();
        if (params != null) bindings.putAll(params);
        bindings.put("db", db);
        // todo cache
        Neo4jGraph neo4jGraph = Neo4jGraph.open(new Neo4jGraphAPIImpl(db, new PropertyConverter.None()));

        bindings.put("graph", neo4jGraph);
        bindings.put("g", neo4jGraph.traversal());
        bindings.put("log", log);
        Object value = engine.eval(code, bindings);
        return mapResults(value);
    }


    @Procedure(mode = Mode.WRITE)
    @Description("gremlin.runFile(file or url) - runs gremlin script from file, you can still pass parameters")
    public Stream<Result> runFile(@Name("fileName") String fileName, @Name(value="params", defaultValue="null") Map<String,Object> params) throws ScriptException {
        File file = new File(fileName);
        try {
            if (!file.exists() || !file.isFile() || !file.canRead())
           throw new IOException("Cannot open file "+fileName+" for reading.");
    
            final String code = FileUtils.readTextFile(file, Charset.defaultCharset());
            return run(code, params);
    
        } catch (IOException e) {
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
