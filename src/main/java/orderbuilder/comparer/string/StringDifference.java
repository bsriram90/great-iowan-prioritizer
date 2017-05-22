package orderbuilder.comparer.string;

import orderbuilder.util.Util;

import java.util.HashMap;
import java.util.HashSet;
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

    public static long setDifference(List<String> text1, List<String> text2) {
        long difference = 0;
        HashSet<String> lines1 = new HashSet<>(text1);
        HashSet<String> temp = new HashSet<>(text1);
        HashSet<String> lines2 = new HashSet<>(text2);
        // A - B
        lines1.removeAll(lines2);
        // B - A
        lines2.removeAll(temp);
        return lines1.size() + lines2.size();
    }

    public static long positionalDissimilarityScore(List<String> text1, List<String> text2, boolean weighted) {
        Long score = 0l;
        List<String> longer = null;
        List<String> shorter = null;
        if(text1.size() > text2.size()) {
            longer = text1;
            shorter = text2;
        } else {
            longer = text2;
            shorter = text1;
        }
        for(int i=0; i<shorter.size(); i++) {
            if(!longer.get(i).equals(shorter.get(i))) {
                if(weighted) {
                    score += (longer.size() - i);
                } else {
                    score ++;
                }
            }
        }
        int n = longer.size() - shorter.size();
        if(weighted) {
            n = ((n * (n + 1))/ 2);
        } else {
            score += n;
        }
        return score;
    }

    public static long positionalDissimilarityScore(String text1, String text2, boolean weighted) {
        return positionalDissimilarityScore(Util.getLinesFromFile(text1),Util.getLinesFromFile(text2), weighted);
    }


    public static long basicLineDifference(String file1, String file2) {
        return basicLineDifference(Util.getLinesFromFile(file1), Util.getLinesFromFile(file2));
    }

    public static long setDifference(String file1, String file2) {
        return setDifference(Util.getLinesFromFile(file1), Util.getLinesFromFile(file2));
    }
}
