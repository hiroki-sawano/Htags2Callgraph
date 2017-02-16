package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Reference {

    private String path;
    private HashMap<String, List<String>> tagMap = new HashMap<>();

    Reference(String path, String regex) throws IOException {
        this.path = path;
        
        final File folder = new File(path);

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                
                File file = new File(folder.getAbsolutePath() + "/" + fileEntry.getName());
                Document document = Jsoup.parse(file, "UTF-8");
                
                System.out.println(document.title());
                if(FilenameUtils.getBaseName(document.title()).matches(regex)){
                    Elements elements = document.select("span.curline > a");
                    List<String> hrefs = new ArrayList<>();

                    for (Element element : elements) {
                        hrefs.add(element.attr("href"));
                    }
                    tagMap.put(document.title(), hrefs);
                }
            }
        }
    }
    
    public String getPath(){
        return this.path;
    }
    
    public HashMap<String, List<String>> getTagMap(){
        return tagMap;
    }
    
    public void print(){
        System.out.println(path + ":");
        for (String s : tagMap.keySet()) {
            System.out.println(s);
            for (String e : tagMap.get(s)) {
                System.out.println("> " + e);
            }
        }
    }
}
