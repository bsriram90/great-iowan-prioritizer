package orderbuilder.prioritizer;

import orderbuilder.comparer.test.*;
import orderbuilder.evaluator.CorrelationScore;
import orderbuilder.evaluator.ResultMatrix;
import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import orderbuilder.util.Util;
import orderbuilder.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sriram on 23-03-2017.
 */
public class Prioritizer {

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria, String startTest) throws Exception {
        // get the right orderbuilder.comparer
        TestComparer comparer = getComparer(criteria);
        return comparer.getExecutionOrder(change, diff, criteria, startTest);
    }

    private static TestComparer getComparer(HashMap<String, Object> criteria) {
        TestComparer comparer = null;
        if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE)) {
            comparer = new CTDTComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.FARTHEST_FROM_NO_CHANGE)) {
            comparer = new FarthestFromNoChange();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE_BACKTRACK)) {
            comparer = new CTDTBackTrackComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE_TRACE_COMPARER)) {
            comparer = new CTDTTraceComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.TEST_TRACE)) {
            comparer = new TestTraceComparer();
        }
        return comparer;
    }

    public static void main(String[] args) throws Exception {

        HashMap<String, Object> criteria = Util.getDefaultPrioritizerCriteria();
        String path = "./res/test-trace/xml-security/";
        String type = "spearman";

        getCorrelationScoreForMatrices(criteria,
                path + "v2/pos-w-differenceMatrix.csv",
                "v2",
                path,
                path + "pos-w-changeMatrix.csv",
                "Positional Weighted",
                type);

        getCorrelationScoreForMatrices(criteria,
                path + "v2/pos-uw-differenceMatrix.csv",
                "v2",
                path,
                path + "pos-uw-changeMatrix.csv",
                "Positional Unweighted",
                type);

        getCorrelationScoreForMatrices(criteria,
                path + "v2/differenceMatrix-v2.csv",
                "v2",
                path,
                path + "changeMatrix.csv",
                "Multiset",
                type);

        getCorrelationScoreForMatrices(criteria,
                path + "v2/differenceMatrix-1.csv",
                "v2",
                path,
                path + "changeMatrix-1.csv",
                "Set",
                type);
    }

    private static void getCorrelationScoreForMatrices(HashMap<String, Object> criteria, String diffFileName, String version, String path, String changeFileName, String name, String type) throws Exception {
        TestTraceDifferenceMatrix<Long> diffMatrix = new TestTraceDifferenceMatrix<>(diffFileName, Long.class, version, path);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>(changeFileName, Long.class);
        List<String> referenceResults = changeMatrix.getTestsByChangeDesc();
        List<DifferenceMatrix> diffList = new ArrayList<>();
        diffList.add(diffMatrix);
        LinkedList<String> order = null;
        order = Prioritizer.getExecutionOrder(changeMatrix, diffList, criteria, referenceResults.get(0));
        System.out.println(name + " - " + order);
        CorrelationScore.printCorrelationScoreByBands(referenceResults, order, 0.05f, type);
    }


}
