package gremlin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * @author mh
 * @since 08.04.16
 */
public class GremlinTest {

    private GraphDatabaseService db;
    @Before
    public void setUp() throws Exception {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        registerProcedure(db,Gremlin.class);
    }
    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test public void testRunBasic() throws Exception {
        testResult(db, "CALL gremlin.run('1',null)",null,
                (result) -> {
                    Object value = result.next().get("value");
                    assertEquals(1, value);
                });
    }

    @Test public void testRunGetNode() throws Exception {
        db.execute("CREATE (n:Person {name:'Peter'})");
        String script = "g.V().hasLabel('Person').has('name',name).values('name')";
        Map<String, Object> params = map("script", script, "params", map("name", "Peter"));
        testResult(db, "CALL gremlin.run({script},{params})", params,
                (result) -> {
                    Object value = result.next().get("value");
                    assertEquals("Peter", value);
                });
    }

    static Map<String,Object> map(Object ... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i+=2) {
            map.put(values[i].toString(),values[i+1]);
        }
        return map;
    }

    public static void testResult(GraphDatabaseService db, String call, Map<String,Object> params, Consumer<Result> resultConsumer) {
        try (Transaction tx = db.beginTx()) {
            Map<String, Object> p = (params == null) ? Collections.<String, Object>emptyMap() : params;
            resultConsumer.accept(db.execute(call, p));
            tx.success();
        }
    }

    public static void registerProcedure(GraphDatabaseService db, Class<?> procedure) throws KernelException {
        ((GraphDatabaseAPI)db).getDependencyResolver().resolveDependency(Procedures.class).register(procedure);
    }

}
