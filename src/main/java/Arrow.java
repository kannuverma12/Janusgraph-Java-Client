import java.util.HashMap;
import java.util.Map;

public class Arrow {
    public final Node from;
    public final Node to;
    public final EdgeLabel label;
    public final Map<String,Object> properties;

    public Arrow(Node from, Node to, EdgeLabel label) {
        this(from, to, label, new HashMap<String, Object>());
    }

    public Arrow(Node from, Node to, EdgeLabel label, Map<String,Object> properties) {
        this.from = from;
        this.to = to;
        this.label = label;
        this.properties = properties;
    }

    @Override
    public String toString(){
        return " "+from.vid + "==>" + to.vid+" ";
    }
}