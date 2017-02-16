package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import graph.Automaton;
import graph.State;
import graph.TransitionRelation;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import translator.GraphvizTranslator;

public class Parser {

    private static String workDir = null;
    private static String regex = null;
    private static String graphCommand = null;
    private static String outputDir = null;

    private static Automaton automaton;

    public static void main(String[] args) throws IOException {
        String configFile = args[0];

        FileInputStream fis = new FileInputStream(configFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        Properties prop = new Properties();
        prop.load(br);

        // the HTML directory created by htags (e.g. ~/myproj/src/HTML)
        workDir = prop.getProperty("htags.html.dir");
        // regular expression which expects to be matched with classes you want to focus on
        regex = prop.getProperty("regex");
        // a command to generate a graph such as dot, sfdp
        graphCommand = prop.getProperty("graphviz.command");
        // an output directory
        outputDir = prop.getProperty("output.dir");

        automaton = new Automaton();

        // read FILEMAP to create HashMap (class -> source page)
        FILEMAP filemap = new FILEMAP(workDir + "/FILEMAP");

        // add class names from FILEMAP to state list
        for (String entry : filemap.getFilemap().keySet()) {
            // if a retrieved class meets the search condition, add it, as a new state, into the automaton (no need for checking if it is new)
            if (FilenameUtils.getBaseName(entry).matches(regex)) {
                State state = new State(FilenameUtils.getBaseName(entry), filemap.getFilemap().get(entry), "concerned");
                automaton.addState(state);
            }
        }

        // read a directory containing pages which have list of links to each class in order to create HashMap (callee class -> list of source pages of callers)
        Reference r = new Reference(workDir + "/R", regex);
        Reference y = new Reference(workDir + "/Y", regex);

        // parse html files having links to added states above so as to collect edges between callers and callees
        // when unknown states appear during the process, they will be in List of State below and merged into the automaton later so that it can avoid ConcurrentModificationException
        List<State> newstates = new ArrayList<>();
        for (State to : automaton.getStates()) {
            String key = to.getId();
            if (r.getTagMap().containsKey(key)) {
                createEdges(r, to, newstates);
            } else if (y.getTagMap().containsKey(key)) {
                createEdges(y, to, newstates);
            } else {
                System.out.println(key + " is not called.");
            }
        }

        for (State s : newstates) {
            automaton.addState(s);
        }

        GraphvizTranslator graphTran = new GraphvizTranslator(automaton, graphCommand, outputDir);
        graphTran.printGraph(true);
    }

    private static void createEdges(Reference reference, State to, List<State> newstates) throws IOException {
        String key = to.getId();
        System.out.println(key + " is found in " + reference.getPath());
        for (String callerURL : reference.getTagMap().get(key)) {
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
    }
}
