import java.util.HashMap;
import java.util.Map;

public class Node {
    public final EntityType label;
    public final String idKey;
    public final Object vid;
    public final Map<String, Object> properties;

    public Node(String id) {
        this(EntityType.UNKNOWN, id);
    }

    public Node(EntityType label, Object vidValue) {
        this(label, vidValue, new HashMap<String, Object>());
    }

    public Node(EntityType label, Object vidValue, Map<String, Object> properties) {
        this(label, "vid", vidValue, properties);
    }

    public Node(EntityType label, String vidKey, Object vid, Map<String, Object> properties) {
        this.label = label;
        this.idKey = vidKey;
        this.vid = vid;
        this.properties = properties;
    }

}
