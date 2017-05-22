package orderbuilder.util;

import java.io.*;
import java.util.*;

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
            while (line != null && !line.equals("")) {
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

    public static String getStringFromCollection(Collection c) {
        StringBuilder builder = new StringBuilder("");
        Iterator itr = c.iterator();
        while (itr.hasNext()) {
            builder.append(itr.next() + ",");
        }
        return builder.substring(0, builder.length() - 1);
    }

    public static HashMap<String, Object> getDefaultPrioritizerCriteria() {
        HashMap<String, Object> criteria = new HashMap<>();
        criteria.put(Variables.PRIORITIZER_SEARCH_METHOD, Variables.TEST_TRACE);
        criteria.put(Variables.THRESHOLD_1, 1000l);
        criteria.put(Variables.THRESHOLD_2, 2l);
        criteria.put(Variables.THRESHOLD_3, 1000l);
        criteria.put(Variables.SIMILARITY_THRESHOLD, 0.25);
        criteria.put(Variables.DEBUG, false);
        return criteria;
    }

    public static String[] getAllDirectories(File root) {
        return root.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
    }

    public static String[] getAllFiles(File dir) {
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile();
            }
        });
    }

    public static float jaccardSimilarity(Collection<String> a, Collection<String> b) {
        Set<String> union = new HashSet<String>();
        union.addAll(a);
        union.addAll(b);
        Set<String> intersection = new HashSet<String>();
        for (String edge : a) {
            if (b.contains(edge)) {
                intersection.add(edge);
            }
        }
        return (float) intersection.size() / (float) union.size();
    }
}
