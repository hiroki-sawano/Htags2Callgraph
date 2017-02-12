package translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kripke.Automaton;
import kripke.State;
import kripke.TransitionRelation;

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
            writer.write("    node  [style=\"rounded, filled, bold\", shape=box, fixedsize=true, width=1.7, fontname=\"Arial\"];\n");
            writer.write("    edge  [style=bold, fontname=\"Arial\", weight=2];\n");
            
            //writer.write("    Layouts_" + Config.layout + " [shape = box];\n");
            //writer.write("    S [shape = circle, style = filled, color = black, width = 0.3, height = 0.3]\n");
            //writer.write("    ext [peripheries = 2, shape = circle, style = filled, color = black, width = 0.3, height = 0.3]\n");

            //writer.write("    S->" + automaton.getInitialState() + " [ label = \"\" ]\n");

            for (State state : automaton.getStates()){
                // should change the shape depending on whether it is concerned or not
                // fillcolor should be decided on only the names of classes
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (printImage) {
            List<String> argv = new ArrayList<String>();
            /*argv.add("cmd");
            argv.add("/c");
            argv.add("cd");
            argv.add(sysConf.getGraphvizDir());
            argv.add("&&");*/

            //argv.add("/opt/local/bin/dot");
            argv.add(graphCommand);
            //argv.add("-Gdpi=300");
            argv.add("-Tgif");
            argv.add(outputDir + "/graph.dot");
            argv.add("-o");
            argv.add(outputDir + "/graph.gif");

            // run
            ProcessBuilder pb = new ProcessBuilder(argv);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }
    }
}
