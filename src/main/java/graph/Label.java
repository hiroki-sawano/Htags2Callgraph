package graph;

public class Label {

    private String action;
    private String condition;

    public Label(String action, String condition) {
        this.action = action;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return action + " [" + condition + "]";
    }
}
