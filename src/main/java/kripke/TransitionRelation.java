package kripke;

public class TransitionRelation {

    private State from;
    private State to;
    private Label label;

    public TransitionRelation(State from, Label label) {
        this.from = from;
        this.label = label;
    }

    public TransitionRelation(State from, State to, Label label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    public void setFrom(State from) {
        this.from = from;
    }

    public void setTo(State to) {
        this.to = to;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public State getFrom() {
        return this.from;
    }

    public State getTo() {
        return this.to;
    }

    public Label getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        if (getLabel() == null) {
            return getFrom() + "->" + getTo() + " [ label = \"\" ]";
        } else {
            return getFrom() + "->" + getTo() + " [ label = \"" + getLabel() + "\" ]";
        }
    }
}
