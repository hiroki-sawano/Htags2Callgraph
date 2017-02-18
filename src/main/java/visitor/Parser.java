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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import system.Settings;
import translator.GraphvizTranslator;

public class Parser {

    private static Automaton automaton;
    private static Settings settings;
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        settings = Settings.getInstance();
        settings.init(args[0]);

        automaton = new Automaton();

        // read FILEMAP to create HashMap (class -> source page)
        FILEMAP filemap = null;
        try {
            filemap = new FILEMAP(settings.getHtagsDir() + "/FILEMAP");
        } catch (IOException ex) {
            logger.error("can't read:" + settings.getHtagsDir() + "/FILEMAP");
            return;
        }

        // add class names from FILEMAP to state list
        for (String entry : filemap.getFilemap().keySet()) {
            // if a retrieved class meets the search condition, add it, as a new state, into the automaton (no need for checking if it is new)
            if (FilenameUtils.getBaseName(entry).matches(settings.getRegex())) {
                State state = new State(FilenameUtils.getBaseName(entry), filemap.getFilemap().get(entry), "concerned");
                automaton.addState(state);
            }
        }
        
        // read a directory containing pages which have list of links to each class in order to create HashMap (callee class -> list of source pages of callers)
        Reference r = new Reference(settings.getHtagsDir() + "/R", settings.getRegex());
        Reference y = new Reference(settings.getHtagsDir() + "/Y", settings.getRegex());

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
                logger.info(key + " is not called.");
            }
        }

        for (State s : newstates) {
            automaton.addState(s);
        }

        GraphvizTranslator graphTran = new GraphvizTranslator(automaton);
        graphTran.printGraph(true);
    }

    private static void createEdges(Reference reference, State to, List<State> newstates) {
        String key = to.getId();
        logger.info(key + " is found in " + reference.getPath());
        for (String callerURL : reference.getTagMap().get(key)) {
            callerURL = StringUtils.strip(callerURL, "../");
            String caller = callerURL.split("#")[0];
            String number = callerURL.split("#")[1];

            File file = new File(settings.getHtagsDir() + "/" + caller);

            try {
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
            } catch (IOException ex) {
                logger.warn("can't read:" + settings.getHtagsDir() + "/" + caller);
            }
        }
    }
}
