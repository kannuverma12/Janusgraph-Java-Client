public enum EdgeLabel {
    FOLLOW("follow");

    private final String label;

    EdgeLabel(String l) {
        this.label = l;
    }

    public String label(){
        return this.label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}