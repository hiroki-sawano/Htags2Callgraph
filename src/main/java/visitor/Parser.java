package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import graph.Automaton;
import graph.State;
import graph.TransitionRelation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import translator.GraphvizTranslator;

public class Parser {

    public static void main(String[] args) throws IOException {
        Automaton automaton = new Automaton();

        // HTML directory created by htags (e.g. ~/myproj/src/HTML)
        String workDir = args[0];        
        // regular expression which expects to be matched with classes you want to focus on
        String regex = args[1];

        // read FILEMAP to create HashMap (class -> source page)
        FILEMAP filemap = new FILEMAP(workDir + "/FILEMAP");
        
        // read a directory containing pages which have list of links to each class in order to create HashMap (callee class -> list of source pages of callers)
        R r = new R(workDir + "/R", regex);

        // add class names from FILEMAP to state list
        for (String entry : filemap.getFilemap().keySet()) {
            // if a retrieved class meets the search condition, add it, as a new state, into the automaton (no need for checking if it is new)
            if (FilenameUtils.getBaseName(entry).matches(regex)) {
                State state = new State(FilenameUtils.getBaseName(entry), filemap.getFilemap().get(entry), "concerned");
                automaton.addState(state);
            }
        }

        // parse html files having links to added states above so as to collect edges between callers and callees
        // when unknown states appear during the process, they will be in List of State below and merged into the automaton later so that it can avoid ConcurrentModificationException
        List<State> newstates = new ArrayList<>();  
        for (State to : automaton.getStates()) {
            String key = to.getId();
            if (r.getRMap().containsKey(key)) {
                System.out.println(key + " is found in " + r.getPath());
                for (String callerURL : r.getRMap().get(key)) {
                    callerURL = StringUtils.strip(callerURL, "../");
                    String caller = callerURL.split("#")[0];
                    String number = callerURL.split("#")[1];

                    File file = new File(workDir + "/" + caller);
                    Document document = Jsoup.parse(file, "UTF-8");
                    caller = FilenameUtils.getBaseName(document.title());

                    State from;
                    if (automaton.isNewState(caller)) {
                        // give a different attribute to a state so as to distinguish states especially you want to look at from the others
                        from = new State(caller, callerURL, "notconcerned");
                        newstates.add(from);
                    } else {
                        from = automaton.getState(caller);
                    }
                    // haven't given a label for now, but putting step number in it might be useful
                    TransitionRelation transition = new TransitionRelation(from, to, null);
                    if (automaton.isNewTransition(transition)) {
                        automaton.addTransitionRelation(transition);
                    }
                }
            } else {
                System.out.println(key + " is not called.");
            }
        }
        
        for(State s : newstates){
            automaton.addState(s);
        }

        GraphvizTranslator graphTran = new GraphvizTranslator(automaton, "/opt/local/bin/dot", "/Users/macbookair/Desktop");
        graphTran.printGraph(true);
    }
}
