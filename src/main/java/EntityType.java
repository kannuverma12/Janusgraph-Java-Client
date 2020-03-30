
public enum EntityType {
    USER("user"),
    ARTIST("artist"),
    PLAYLIST("playlist"),
    UNKNOWN("unknown");

    private final String label;

    EntityType(String l) {
        this.label = l;
    }

    public static EntityType getEntityType(String entity){
        for(EntityType entityType : EntityType.values()){
            if(entityType.label.equalsIgnoreCase(entity)){
                return entityType;
            }
        }
        return EntityType.UNKNOWN;
    }

    public String label() {
        return this.label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
