/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author macbookair
 */
public class R {

    private String path;
    private HashMap<String, List<String>> rMap = new HashMap<>();

    R(String path) throws IOException {
        this.path = path;
        
        final File folder = new File(path);

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                
                File file = new File(folder.getAbsolutePath() + "/" + fileEntry.getName());
                Document document = Jsoup.parse(file, "UTF-8");
                Elements elements = document.select("span.curline > a");
                List<String> hrefs = new ArrayList<>();

                for (Element element : elements) {
                    hrefs.add(element.attr("href"));
                }

                rMap.put(document.title(), hrefs);
            }
        }
    }
    
    public HashMap<String, List<String>> getRMap(){
        return rMap;
    }
    
    public void print(){
        System.out.println(path + ":");
        for (String s : rMap.keySet()) {
            System.out.println(s);
            for (String e : rMap.get(s)) {
                System.out.println("> " + e);
            }
        }
    }
}
