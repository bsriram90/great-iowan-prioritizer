package orderbuilder.evaluator;

import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

/**
 * Created by Sriram on 02-04-2017.
 */
public class ResultMatrix {
    private TestTraceDifferenceMatrix diff;
    private List<String> referenceResult;
    private List<Result> results;
    private HashMap<String, Integer> testAndIndex = new HashMap<>();

    public ResultMatrix(TestTraceDifferenceMatrix change) {
        results = new ArrayList<>();
        this.diff = change;
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
        results.add(new Result(id, order, referenceResult, diff));
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
        Collections.sort(results);
        System.out.print(System.lineSeparator());
        for (Result res : results) {
            System.out.print(res.getId() + ":\t" + res.getScore() + " -\n");
            for (String test : res.getOrder()) {
                System.out.print(testAndIndex.get(test) + 1 + " - " + test + " - " + diff.getTestCaseByTest(test) + "\n");
            }
            System.out.print(System.lineSeparator());
        }
    }

    public Integer getLevenstienDistance(Result res) {
        LevenshteinDistance editDist = new LevenshteinDistance();
        String string1 = getIndexConvertedString(res.getOrder());
        String referenceString = getIndexConvertedString(referenceResult);
        return editDist.apply(referenceString, string1);
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

    class PositionalWeightedDistance implements Comparator<Result> {

        @Override
        public int compare(Result o1, Result o2) {
            return 0;
        }
    }
}
