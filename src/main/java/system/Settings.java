package system;

import generated.ConfigurationType;
import generated.GraphvizType;
import java.io.File;
import javax.xml.bind.JAXB;

public class Settings {

    private static Settings instance = null;
    
    private String htagsDir = null;
    private String outputDir = null;
    private String regex = null;
    private GraphvizType graphviz = null;

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public void init(String configFile){
        ConfigurationType config = JAXB.unmarshal(new File(configFile), ConfigurationType.class);
        // the HTML directory created by htags (e.g. ~/myproj/src/HTML)
        htagsDir = config.getHtagsDir();
        // an output directory
        outputDir = config.getOutputDir();
        // regular expression which expects to be matched with classes you want to focus on
        regex = config.getRegex();
        // graphviz settings
        graphviz = config.getGraphviz();
    }

    public String getHtagsDir() {
        return htagsDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getRegex() {
        return regex;
    }

    public GraphvizType getGraphviz() {
        return graphviz;
    }
    
}
