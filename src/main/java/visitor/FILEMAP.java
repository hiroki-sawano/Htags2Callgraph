/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author macbookair
 */
public class FILEMAP {

    private String path;
    private HashMap<String, String> fileMap = new HashMap<>();

    FILEMAP(String path) throws IOException {
        this.path = path;
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String string = reader.readLine();
            while (string != null) {
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
