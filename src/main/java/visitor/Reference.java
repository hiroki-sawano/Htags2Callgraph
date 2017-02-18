package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Reference {

    private String path;
    private HashMap<String, List<String>> tagMap = new HashMap<>();
    private static Logger logger = LogManager.getLogger();

    Reference(String path, String regex) {
        this.path = path;

        final File folder = new File(path);

        if (folder.exists()) {
            for (final File fileEntry : folder.listFiles()) {
                if (!fileEntry.isDirectory()) {
                    try {
                        File file = new File(folder.getAbsolutePath() + "/" + fileEntry.getName());
                        logger.info(folder.getAbsolutePath() + "/" + fileEntry.getName());
                        
                        Document document;
                        document = Jsoup.parse(file, "UTF-8");
                        if (FilenameUtils.getBaseName(document.title()).matches(regex)) {
                            Elements elements = document.select("span.curline > a");
                            List<String> hrefs = new ArrayList<>();

                            for (Element element : elements) {
                                hrefs.add(element.attr("href"));
                            }
                            tagMap.put(document.title(), hrefs);
                        }
                    } catch (IOException ex) {
                        logger.warn("can't read:" + folder.getAbsolutePath() + "/" + fileEntry.getName());
                    }
                }else{
                    logger.warn("ignore nested directory:" + folder.getAbsolutePath() + "/" + fileEntry.getName());
                }
            }
        } else {
            logger.warn("can't find:" + path);
        }
    }

    public String getPath() {
        return this.path;
    }

    public HashMap<String, List<String>> getTagMap() {
        return tagMap;
    }

    public void print() {
        System.out.println(path + ":");
        for (String s : tagMap.keySet()) {
            System.out.println(s);
            for (String e : tagMap.get(s)) {
                System.out.println("> " + e);
            }
        }
    }
}
