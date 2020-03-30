
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.TransactionBuilder;
import org.janusgraph.core.JanusGraphFactory.Builder;
import org.janusgraph.core.schema.JanusGraphManagement;

public class GraphUploadStaging {
    private static JanusGraph graph;
    private static GraphTraversalSource traversalSource;

    public GraphUploadStaging() {
    }

    public static void main(String[] args) {
        create();
    }

    public static JanusGraph create() {
        Builder config = JanusGraphFactory.build();
        //config.set("storage.backend", "cassandrathrift");
        //config.set("storage.cassandra.keyspace", "wynk_graph");

        config.set("storage.backend", "cql");
        config.set("storage.cql.keyspace", "wynk_graph");
        config.set("storage.cql.read-consistency-level", "ONE");
        config.set("storage.cql.write-consistency-level", "ONE");

        config.set("ids.block-size", "1000000000");
        config.set("ids.renew-percentage", "0.3");
        config.set("storage.hostname", "10.1.2.144");
        graph = config.open();
        System.out.println("Graph = " + graph);
        manage(graph);
        traversalSource = graph.traversal();
        System.out.println("traversalSource = " + traversalSource);
        load(graph);
        return graph;
    }

    public static void load(JanusGraph graph) {
        JanusGraphTransaction tx = graph.newTransaction();
        bulkUplaod();
    }

    private static void manage(JanusGraph graph) {
        JanusGraphManagement mgmt = graph.openManagement();
        if (mgmt.getGraphIndex("vertexId") != null) {
            System.out.println("Graph schema already defined");
        } else {
            mgmt.makeEdgeLabel("follow").multiplicity(Multiplicity.SIMPLE).make();
            PropertyKey item = mgmt.makePropertyKey("vid").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            mgmt.buildIndex("vertexId", Vertex.class).addKey(item).unique().buildCompositeIndex();
            mgmt.makePropertyKey("time").dataType(Long.class).make();
            mgmt.commit();
        }
    }

    public static void bulkUplaod() {
        TransactionBuilder builder = graph.buildTransaction();
        JanusGraphTransaction tx = builder.enableBatchLoading().consistencyChecks(false).start();
        Map<String, Object> edgeProperties = new HashMap();
        Map<String, Object> nodeProperties = new HashMap();
        edgeProperties.put("time", System.currentTimeMillis());
        nodeProperties.put("curated", true);

        for(int i = 3611501; i < 3612600; ++i) {
            String followId = "sn-uid-" + i;
            System.out.println("Adding new edge : " + followId);
            Node from = new Node(EntityType.USER, "arijit-singh");
            Node to = new Node(EntityType.ARTIST, followId, nodeProperties);
            Arrow arrow = new Arrow(from, to, EdgeLabel.FOLLOW, edgeProperties);

            try {
                addEdge(arrow);
            } catch (Exception var10) {
                var10.printStackTrace();
            }
            System.out.println("Edge addition completed : " + followId);
        }

        tx.commit();
        tx.close();
        graph.close();
    }

    public static void addEdge(Arrow arrow) throws Exception {
        try {
            GraphTraversal<Vertex, Vertex> gt = traversalSource.V(new Object[0]).has(arrow.from.idKey, arrow.from.vid);
            Vertex fromV = gt.hasNext() ? (Vertex)gt.next() : addVertex(arrow.from);
            traversalSource.tx().commit();
            gt = traversalSource.V(new Object[0]).has(arrow.to.idKey, arrow.to.vid);
            Vertex toV = gt.hasNext() ? (Vertex)gt.next() : addVertex(arrow.to);
            traversalSource.tx().commit();
            ((Vertex)traversalSource.V(new Object[]{fromV}).next()).addEdge(arrow.label.label(), toV, convertProperties(arrow.properties));
        } catch (Exception var5) {
            var5.printStackTrace();
            throw new Exception();
        }
    }

    public static Object[] convertProperties(Map<String, Object> properties) {
        int size = properties.size() << 1;
        Object[] propArray = new Object[size];
        int c = 0;

        Entry entry;
        for(Iterator var4 = properties.entrySet().iterator(); var4.hasNext(); propArray[c++] = entry.getValue()) {
            entry = (Entry)var4.next();
            propArray[c++] = entry.getKey();
        }

        return propArray;
    }

    public static Vertex addVertex(Node node) throws Exception {
        try {
            JanusGraphVertex janusGraphVertex = graph.addVertex(node.label.label());
            janusGraphVertex.property(node.idKey, node.vid);
            Iterator var2 = node.properties.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, Object> entry = (Entry)var2.next();
                janusGraphVertex.property((String)entry.getKey(), entry.getValue());
            }

            return janusGraphVertex;
        } catch (Exception var4) {
            throw new Exception();
        }
    }

    public static long getEdges() {
        try {
            Builder config = JanusGraphFactory.build();
            config.set("storage.backend", "cassandrathrift");
            config.set("storage.cassandra.keyspace", "graph_wynk1");
            config.set("storage.hostname", "127.0.0.1");
            graph = config.open();
            System.out.println("Graph = " + graph);
            traversalSource = graph.traversal();
            GraphTraversal<Vertex, Vertex> gt = traversalSource.V(new Object[0]).has("vid", "supernode");
            long l = (Long)gt.count().next();
            graph.tx().commit();
            return l;
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0L;
        }
    }
}
