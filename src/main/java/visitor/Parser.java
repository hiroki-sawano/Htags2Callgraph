package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kripke.Automaton;
import kripke.Label;
import kripke.State;
import kripke.TransitionRelation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import translator.GraphvizTranslator;

public class Parser {

    public static void main(String[] args) throws IOException {
        Automaton automaton = new Automaton();

        // for example /Users/macbookair/Documents/workspace/PHPASTAnalyzer/src/HTML
        String workDir = args[0];        
        // search condition
        String regex = args[1];

        // read filemap to create HashMap (class -> html path)
        FILEMAP filemap = new FILEMAP(workDir + "/FILEMAP");
        
        // read R directory to create HashMap (callee class -> list of html whose classes are caller)
        R r = new R(workDir + "/R", regex);

        // get class names from tha map
        for (String entry : filemap.getFilemap().keySet()) {
            // if each class meet the search condition
            if (FilenameUtils.getBaseName(entry).matches(regex)) {
                // put the state into the state list when it is new
                State state = new State(FilenameUtils.getBaseName(entry), filemap.getFilemap().get(entry), "concerned");
                automaton.addState(state);
            }
        }

        List<State> newstates = new ArrayList<>();
        
        // parse the html file corresponding to the class to collect relations from caller states
        for (State to : automaton.getStates()) {
            // find the class in R directory
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
                    // only if caller meets the search condition, regard it as the same type of state,
                    // otherwise, give a different attribute to the found state so as to distinguish the states meeting condition and the others
                    if (automaton.isNewState(caller)) {
                        from = new State(caller, callerURL, "notconcerned");
                        newstates.add(from);
                    } else {
                        from = automaton.getState(caller);
                    }
                    TransitionRelation transition = new TransitionRelation(from, to, new Label("", ""));
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
