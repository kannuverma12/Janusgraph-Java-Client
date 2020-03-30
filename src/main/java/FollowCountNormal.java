import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.janusgraph.core.*;

import java.util.Map;

public class FollowCountNormal {

    private static JanusGraph graph;
    private static GraphTraversalSource traversalSource;

    public static void main(String[] args) {
        create();
        System.exit(0);
    }

    public static JanusGraph create() {
        JanusGraphFactory.Builder config = JanusGraphFactory.build();
        config.set("storage.backend", "cassandrathrift");
        config.set("storage.cassandra.keyspace", "w_graph");
        config.set("storage.cassandra.read-consistency-level", "ONE");

        config.set("log.tx.key-consistent", "true");
        config.set("storage.cassandra.frame-size-mb", "128");
        config.set("storage.hostname", "10.10.10.167");
        config.set("connectionPool.keepAliveInterval", "360000");
        config.set("storage.cql.only-use-local-consistency-for-system-operations", true);

        graph = JanusGraphFactory.open("/Users/b0216282/Documents/backups/git/janusgraph/src/main/resources/jp.properties");

        System.out.println("Graph = " + graph);
        traversalSource = graph.traversal();
        System.out.println("traversalSource = " + traversalSource);
        getAllEdges();
        return graph;
    }

    static long getAllEdges() {
        try {
            GraphTraversal<Vertex, Vertex> allV = traversalSource.V();

            GraphTraversal<Vertex, Vertex> gt = allV.has("vid", "abc");
            GraphTraversal<Vertex, Long> c = gt.bothE()
                    //.limit(100000)
                    .count();
            long l = c.next();

            // find all vertex in graph
            long l1 = traversalSource.V().count().next();

            //find all edges
            long allEdges = traversalSource.E().count().next();

            //group edges by label
            GraphTraversal<Edge, Map<Object, Long>> l3 = traversalSource.E().groupCount().by("label");

            System.out.println("All edges = " + l);
            graph.tx().commit();
            return l;
        } catch (Exception e) {
            System.out.println("Error while fetching the edges for : ");
            e.printStackTrace();
        }
        return -1;
    }
}
