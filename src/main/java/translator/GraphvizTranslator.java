package translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import graph.Automaton;
import graph.State;
import graph.TransitionRelation;

public class GraphvizTranslator {

    private Automaton automaton;
    private String graphCommand;
    private String outputDir;
    
    public GraphvizTranslator(Automaton automaton, String graphCommand, String outputDir){
        this.automaton = automaton;
        this.graphCommand = graphCommand;
        this.outputDir = outputDir;
    }
    
    public void printGraph(Boolean printImage) {
        String outputPath = outputDir + "/graph.dot";
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
                    writer.write("    " + state + " [fillcolor=\"#a0ffa0\"]\n");
                }else{
                    writer.write("    " + state + " [fillcolor=\"#ffa0a0\"]\n");
                }
            }
            for (TransitionRelation transitionRelation : automaton.getTransitionRelations()) {
                writer.write("    " + transitionRelation + "\n");
            }

            writer.write("}\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (printImage) {
            List<String> argv = new ArrayList<>();
            argv.add(graphCommand);
            argv.add("-Tgif");
            argv.add(outputDir + "/graph.dot");
            argv.add("-o");
            argv.add(outputDir + "/graph.gif");

            ProcessBuilder pb = new ProcessBuilder(argv);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
