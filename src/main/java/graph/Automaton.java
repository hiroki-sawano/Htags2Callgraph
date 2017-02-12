package graph;

import java.util.ArrayList;
import java.util.List;

public class Automaton{

    private State initialState;
    private List<State> states = new ArrayList<>();
    private List<TransitionRelation> transitionRelations = new ArrayList<>();
    
    public void setInitialState(State s) {
        //State s = new State(id, "S0000", "");
        initialState = s;
    }

    public void addState(State s){
        //int stateNum = states.size() + 1;
        //s.setValue(String.format("S%04d", stateNum));
        states.add(s);
    }

    synchronized public void addTransitionRelation(TransitionRelation t) {
        transitionRelations.add(t);   
    }

    public State getInitialState() {
        return initialState;
    }
    
    public State getState(String id){
        for(State s : states){
            if(id.equals(s.getId())){
                return s;
            }
        }
        return null;
    }
    
    public List<State> getStates() {
        return states;
    }

    public List<TransitionRelation> getTransitionRelations() {
        return transitionRelations;
    }

    public boolean isNewState(String id) {
        for (State s : states) {
            if (id.equals(s.getId())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isNewState(State state) {
        for (State s : states) {
            if (state.getId().equals(s.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean isInitialState(State state) {
        return state.getId().equals(initialState.getId());
    }
    
    public boolean isNewTransition(TransitionRelation transition) {
        for (TransitionRelation t : transitionRelations) {
            if (transition.toString().equals(t.toString())) {
                return false;
            }
        }
        return true;
    }
}
