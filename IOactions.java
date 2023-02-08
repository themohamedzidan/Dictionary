package ex2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class IOactions {
    IOactions() {

    }

    Map<String, String> Import(String filePath) throws Error, IOException {
        Map<String, String> map = new HashMap<>();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ", 2);
            if (parts.length >= 2) {
                String key = String.format("%s",  parts[0]);
                String value = String.format("%s",parts[1]);
                if (!value.matches("[a-zA-Z]+")) {
                    throw new Error("imported file not supported");
                }
                map.put(key, value);
            } else {
                throw new Error("imported file not supported");
            }
        }
        reader.close();
        return map;
    }

    void Export(Map<String, String> map,String saveAs) throws IOException {
        FileWriter fstream;
        BufferedWriter out;
        fstream = new FileWriter(saveAs +".txt");
        out = new BufferedWriter(fstream);
        for (Map.Entry<String, String> pairs : map.entrySet()) {
            out.write(pairs.getKey() + " ");
            out.write(pairs.getValue() + "\n");
        }
        out.close();
    }
}
