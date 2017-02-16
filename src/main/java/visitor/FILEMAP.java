package visitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FILEMAP {

    private String path;
    private HashMap<String, String> fileMap = new HashMap<>();

    FILEMAP(String path) throws IOException {
        this.path = path;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String string = reader.readLine();
            while (string != null) {
                System.out.println(string);
                String[] strs = string.split("\\s+");
                fileMap.put(strs[0], strs[1]);

                string = reader.readLine();
            }
        }
    }
    
    public HashMap<String, String> getFilemap(){
        return fileMap;
    }
    
    public void print(){
        System.out.println(path + ":");
        for (String s : fileMap.keySet()) {
            System.out.println(s + " -> " + fileMap.get(s));
        }
    }
    
}
