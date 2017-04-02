package orderbuilder.evaluator;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sriram on 02-04-2017.
 */
public class Results {
    private List<String> referenceResult;
    private List<List<String>> results;
    private HashMap<String, String> resultsId;
    private HashMap<String, Integer> testAndIndex = new HashMap<>();

    public Results() {
        results = new ArrayList<>();
        resultsId = new HashMap<>();
    }

    public List<String> getReferenceResult() {
        return referenceResult;
    }

    public void setReferenceResult(List<String> referenceResult) {
        this.referenceResult = referenceResult;
        for (int i = 0; i < referenceResult.size(); i++) {
            testAndIndex.put(referenceResult.get(i), i);
        }
    }

    public void addResult(String id, List<String> order) {
        resultsId.put(getIndexConvertedString(order), id);
        results.add(order);
    }

    public List<List<String>> getResults() {
        return results;
    }

    public void setResults(List<List<String>> results) {
        this.results = results;
    }

    public String getIndexConvertedString(List<String> order) {
        StringBuilder builder = new StringBuilder("");
        for (String test : order) {
            builder.append(testAndIndex.get(test));
        }
        return builder.toString();
    }

    public void printOrderedResults() {
        System.out.print("Reference Order :\t");
        for (String test : referenceResult) {
            System.out.print(test + "\t");
        }
        System.out.print(System.lineSeparator());
        for (List<String> res : results) {
            System.out.print(resultsId.get(getIndexConvertedString(res) + ":\t"));
            for (String test : res) {
                System.out.print(test + "\t");
            }
            System.out.print(System.lineSeparator());
        }
    }

    class LevenstienComparator implements Comparator<List<String>> {

        @Override
        public int compare(List<String> o1, List<String> o2) {
            String referenceString = getIndexConvertedString(referenceResult);
            String string1 = getIndexConvertedString(o1);
            String string2 = getIndexConvertedString(o2);
            LevenshteinDistance editDist = new LevenshteinDistance();
            Integer distance1 = editDist.apply(referenceString, string1);
            Integer distance2 = editDist.apply(referenceString, string2);
            int distDiff = distance1 - distance2;
            return distDiff;
        }
    }
}
