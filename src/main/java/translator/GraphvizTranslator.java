package translator;

import generated.GraphvizType;
import generated.NodeType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import graph.Automaton;
import graph.State;
import graph.TransitionRelation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import system.Settings;

public class GraphvizTranslator {

    private Settings settings;
    private Automaton automaton;
    private static Logger logger = LogManager.getLogger();
    
    public GraphvizTranslator(Automaton automaton){
        settings = Settings.getInstance();
        this.automaton = automaton;
    }
    
    public void printGraph(Boolean printImage) {
        GraphvizType graphviz = settings.getGraphviz();
        String outputPath = settings.getOutputDir() + "/graph.dot";
        
        File file = new File(outputPath);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("digraph system {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node  [style=\"rounded, filled, bold\", shape=box, fixedsize=true, width=1.8, fontname=\"Arial\"];\n");
            writer.write("    edge  [style=bold, fontname=\"Arial\", weight=2];\n");
            
            for (State state : automaton.getStates()){
                if(state.getAttr().equals("concerned")){
                    boolean match = false;
                    for(NodeType node : graphviz.getNodes().getSpecified().getNode()){
                        if(state.toString().matches(node.getValue())){
                            writer.write("    " + state + " [shape=" + node.getShape() + ", fillcolor=\"" + node.getFillcolor() + "\"]\n");
                            match = true;
                            break;
                        }
                    }
                    if(!match){
                        writer.write("    " + state + " [shape=" + graphviz.getNodes().getSpecified().getShape() + ", fillcolor=\"" + graphviz.getNodes().getSpecified().getFillcolor() + "\"]\n");
                    }
                }else{
                    boolean match = false;
                    for(NodeType node : graphviz.getNodes().getUnspecified().getNode()){
                        if(state.toString().matches(node.getValue())){
                            writer.write("    " + state + " [shape=" + node.getShape() + ", fillcolor=\"" + node.getFillcolor() + "\"]\n");
                            match = true;
                            break;
                        }
                    }
                    if(!match){
                        writer.write("    " + state + " [shape=" + graphviz.getNodes().getUnspecified().getShape() + ", fillcolor=\"" + graphviz.getNodes().getUnspecified().getFillcolor() + "\"]\n");
                    }
                }
            }
            for (TransitionRelation transitionRelation : automaton.getTransitionRelations()) {
                writer.write("    " + transitionRelation + "\n");
            }

            writer.write("}\n");
            writer.close();
        } catch (IOException e) {
            logger.error("can't open:" + outputPath);
            return;
        }

        if (printImage) {
            for(String command : graphviz.getCommand()){
                List<String> argv = new ArrayList<>();
                argv.add(command);
                argv.add("-Tgif");
                argv.add(outputPath);
                argv.add("-o");
                argv.add(settings.getOutputDir() + "/" + FilenameUtils.getBaseName(command) + ".gif");

                ProcessBuilder pb = new ProcessBuilder(argv);
                try {
                    Process p = pb.start();
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    logger.error(command + " failed");
                    return;
                }
            }
        }
    }
}
