import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
//import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

public class FollowCountSpark {

    private static Graph hgraph;
    private static GraphTraversalSource traversalSource;

    public static void main(String[] args) {
        createHGraph();
        System.exit(0);
    }

    private static void createHGraph() {
        hgraph = GraphFactory.open("/Users/b0216282/Documents/backups/git/janusgraph/src/main/resources/jp_spark.properties");
        System.out.println("Graph = "+hgraph);
        //traversalSource = hgraph.traversal().withComputer(SparkGraphComputer.class);
        System.out.println("traversalSource = "+traversalSource);
        getAllEdgesFromHGraph();
    }

    static long getAllEdgesFromHGraph(){
        try{
            GraphTraversal<Vertex, Vertex> allV = traversalSource.V();
            GraphTraversal<Vertex, Vertex> gt = allV.has("vid", "supernode");
            GraphTraversal<Vertex, Long> c = gt.outE()
//                    .limit(600000)
                    .count();
            long l = c.next();
            System.out.println("All edges = "+l);
            return l;
        }catch (Exception e) {
            System.out.println("Error while fetching the edges for : ");
            e.printStackTrace();
        }
        return -1;
    }
}
