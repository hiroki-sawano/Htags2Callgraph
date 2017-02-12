package kripke;

public class State {

    private String id;
    private String value;
    private String attr;

    public State(String id) {
        this.id = id;
    }

    public State(String id, String value) {
        this.id = id;
        this.value = value;
    }
    
    public State(String id, String value, String attr) {
        this.id = id;
        this.value = value;
        this.attr = attr;
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }
    
    public String getAttr(){
        return this.attr;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
