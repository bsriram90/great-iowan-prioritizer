package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sriram on 09-03-2017.
 */
public class Util {

    public static List<String> getLinesFromFile(String filename) {
        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while(line != null && !line.equals("")) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static HashMap<String,Object> getDefaultPrioritizerCriteria() {
        HashMap<String,Object> criteria = new HashMap<>();
        criteria.put(Variables.PRIORITIZER_SEARCH_METHOD,Variables.CLOSEST);
        return criteria;
    }
}
