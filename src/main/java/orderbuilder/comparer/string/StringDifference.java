package orderbuilder.comparer.string;

import orderbuilder.util.Util;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sriram on 09-03-2017.
 */
public class StringDifference {

    public static long basicLineDifference(List<String> text1, List<String> text2) {
        long difference = 0;
        HashMap<String, Integer> methodAndCount = new HashMap<String, Integer>();
        for (String line : text1) {
            line = line.trim();
            if (methodAndCount.containsKey(line)) {
                Integer count = methodAndCount.get(line);
                methodAndCount.put(line, ++count);
            } else {
                methodAndCount.put(line, 1);
            }
        }
        for (String line : text2) {
            line = line.trim();
            if (methodAndCount.containsKey(line)) {
                Integer count = methodAndCount.get(line);
                if (count == 1) {
                    methodAndCount.remove(line);
                } else {
                    methodAndCount.put(line, --count);
                }
            } else {
                difference++;
            }
        }
        for (String key : methodAndCount.keySet()) {
            difference += methodAndCount.get(key);
        }
        return difference;
    }

    public static long basicLineDifference(String file1, String file2) {
        return basicLineDifference(Util.getLinesFromFile(file1), Util.getLinesFromFile(file2));
    }
}
