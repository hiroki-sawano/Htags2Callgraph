package graph;

public class Label {

    private String action;
    private String condition;

    public Label(String action, String condition) {
        this.action = action;
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        if (condition != null) {
            return action + " [" + condition + "]";
        } else {
            return action;
        }
    }
}
