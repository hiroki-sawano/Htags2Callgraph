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
import system.Settings;

public class GraphvizTranslator {

    private Settings settings;
    private Automaton automaton;
    
    public GraphvizTranslator(Automaton automaton){
        settings = Settings.getInstance();
        this.automaton = automaton;
    }
    
    public void printGraph(Boolean printImage) {
        GraphvizType graphviz = settings.getGraphviz();
        String outputPath = settings.getOutputDir() + "/graph.dot";
        
        BufferedWriter writer;

        File file = new File(outputPath);
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("digraph system {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node  [style=\"rounded, filled, bold\", shape=box, fixedsize=true, width=1.8, fontname=\"Arial\"];\n");
            writer.write("    edge  [style=bold, fontname=\"Arial\", weight=2];\n");
            
            for (State state : automaton.getStates()){
                // should change the shape depending on whether it is concerned or not
                // fillcolor should be customizable, for instance, changing color based on naming convention might work well
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
                        writer.write("    " + state + " [fillcolor=\"#ff00ff\"]\n");
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
                        writer.write("    " + state + " [shape=hexagon, fillcolor=\"#ff00ff\"]\n");
                    }
                }
            }
            for (TransitionRelation transitionRelation : automaton.getTransitionRelations()) {
                writer.write("    " + transitionRelation + "\n");
            }

            writer.write("}\n");
            writer.close();
        } catch (IOException e) {
            // error : cannot open outputPath
            
            
            
            
            
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
                    // warning : command failed
                    
                    
                    
                    
                }
            }
        }
    }
}
