import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.janusgraph.core.*;
import org.janusgraph.core.schema.JanusGraphManagement;

import java.util.HashMap;
import java.util.Map;


public class GraphBulkUpload {

    private static JanusGraph graph;
    private static GraphTraversalSource traversalSource;

    public static void main(String[] args) {
        create();
        System.exit(0);
    }


    static JanusGraph create() {
        JanusGraphFactory.Builder config = JanusGraphFactory.build();
        config.set("storage.backend", "cassandrathrift");
        config.set("storage.cassandra.keyspace", "wynk_graph");
        config.set("ids.block-size", "1000000000");
        config.set("ids.renew-percentage","0.3");
        config.set("storage.cassandra.frame-size-mb", "128");
        config.set("storage.cassandra.thrift.cpool.max-wait", -1);


        config.set("storage.buffer-size","60000");
        config.set("storage.batch-loading","true");


        config.set("storage.hostname", "10.70.1.167,10.70.0.18,10.70.0.141");

        //config.set("storage.hostname", "10.1.2.144");

        graph = config.open();
        System.out.println("Graph = "+graph);
        manage(graph);
        traversalSource = graph.traversal();
        System.out.println("traversalSource = "+traversalSource);
        load(graph);

        return graph;
    }


    static void load(final JanusGraph graph) {
        JanusGraphTransaction tx = graph.newTransaction();
        bulkUplaod();
        tx.commit();
    }

    private static void manage(JanusGraph graph) {
        JanusGraphManagement mgmt = graph.openManagement();
        if (mgmt.getGraphIndex("vertexId") != null) {
            System.out.println("Graph schema already defined");
            return;
        }
        mgmt.makeEdgeLabel("follow").multiplicity(Multiplicity.SIMPLE).make();

        PropertyKey item =
                mgmt.makePropertyKey("vid")
                        .dataType(String.class)
                        .cardinality(Cardinality.SINGLE)
                        .make();
        mgmt.buildIndex("vertexId", Vertex.class)
                .addKey(item)
                .unique()
                .buildCompositeIndex();
        mgmt.makePropertyKey("time").dataType(Long.class).make();
        mgmt.commit();
    }

    static void bulkUplaod(){
        TransactionBuilder builder = graph.buildTransaction();
        JanusGraphTransaction tx = builder.enableBatchLoading().consistencyChecks(false).start();
        Map<String, Object> edgeProperties = new HashMap<>();
        Map<String, Object> nodeProperties = new HashMap<>();
        edgeProperties.put("time", System.currentTimeMillis());

        nodeProperties.put("curated", true);
        for(int i =2511500 ; i<2511501; i++){
            String followId = "sn-uid-"+i;
            System.out.println("Adding new node : "+followId);
            Node from = new Node(EntityType.USER, "ikka");
            Node to = new Node(EntityType.ARTIST, followId, nodeProperties);
            Arrow arrow = new Arrow(from, to,EdgeLabel.FOLLOW, edgeProperties);
            try {
                addEdge(arrow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tx.commit();
        tx.close();
        graph.close();
    }

    public static void addEdge(Arrow arrow) throws Exception {
        GraphTraversal<Vertex, Vertex> gt;
        Vertex fromV, toV;
        try {
            gt = traversalSource.V().has(arrow.from.idKey, arrow.from.vid);
            fromV = gt.hasNext() ? gt.next() : addVertex(arrow.from);
            traversalSource.tx().commit();
            // ideally there should be a commit here.... as the V() method below will create a new txn
            gt = traversalSource.V().has(arrow.to.idKey, arrow.to.vid);
            toV = gt.hasNext() ? gt.next() : addVertex(arrow.to);
            traversalSource.tx().commit();
            // ideally there should be a commit here.... as the V() method below will create a new txn
//            if (!updateEdge(arrow)) {
                traversalSource.V(fromV).next().addEdge(arrow.label.label(), toV, convertProperties(arrow.properties));
                // commit here as well.
            //}
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public static Object[] convertProperties(Map<String, Object> properties) {
        int size = properties.size() << 1;
        Object[] propArray = new Object[size];
        int c = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            propArray[c++] = entry.getKey();
            propArray[c++] = entry.getValue();
        }
        return propArray;
    }

    public static Vertex addVertex(Node node) throws Exception{
        try {
            JanusGraphVertex janusGraphVertex = graph.addVertex(node.label.label());
            janusGraphVertex.property(node.idKey, node.vid);
            for (Map.Entry<String, Object> entry : node.properties.entrySet()) {
                janusGraphVertex.property(entry.getKey(), entry.getValue());
            }
            return janusGraphVertex;

        } catch (Exception e) {
            throw new Exception();
        }
    }
}
