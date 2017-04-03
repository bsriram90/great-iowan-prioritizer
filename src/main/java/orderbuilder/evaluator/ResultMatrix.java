package orderbuilder.evaluator;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

/**
 * Created by Sriram on 02-04-2017.
 */
public class ResultMatrix {
    private List<String> referenceResult;
    private List<Result> results;
    private HashMap<String, Integer> testAndIndex = new HashMap<>();

    public ResultMatrix() {
        results = new ArrayList<>();
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
        results.add(new Result(id, order));
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
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
        System.out.print("Reference :\t");
        for (String test : referenceResult) {
            System.out.print(test + "\t");
        }
        Collections.sort(results, new LevenstienComparator());
        System.out.print(System.lineSeparator());
        for (Result res : results) {
            System.out.print(res.getId() + ":\t");
            for (String test : res.getOrder()) {
                System.out.print(test + "\t");
            }
            System.out.print(System.lineSeparator());
        }
    }

    class LevenstienComparator implements Comparator<Result> {

        @Override
        public int compare(Result o1, Result o2) {
            String referenceString = getIndexConvertedString(referenceResult);
            String string1 = getIndexConvertedString(o1.getOrder());
            String string2 = getIndexConvertedString(o2.getOrder());
            LevenshteinDistance editDist = new LevenshteinDistance();
            Integer distance1 = editDist.apply(referenceString, string1);
            Integer distance2 = editDist.apply(referenceString, string2);
            int distDiff = distance1 - distance2;
            return distDiff;
        }
    }
}
