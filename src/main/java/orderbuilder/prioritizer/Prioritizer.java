package orderbuilder.prioritizer;

import orderbuilder.comparer.test.*;
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

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria) throws Exception {
        // get the right orderbuilder.comparer
        TestComparer comparer = getComparer(criteria);
        return comparer.getExecutionOrder(change, diff, criteria);
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
        List<DifferenceMatrix> diffList = new ArrayList<>();
        // DifferenceMatrix<Long> diffMatrix = new DifferenceMatrix<>("./res/v2/differenceMatrix-v2.csv", Long.class);
        TestTraceDifferenceMatrix<Long> extDiff1 = new TestTraceDifferenceMatrix<>("./res/test-trace/xml-security/v2/differenceMatrix-v2.csv", Long.class, "v2", "./res/test-trace/xml-security/");
        TestTraceDifferenceMatrix<Long> extDiff2 = new TestTraceDifferenceMatrix<>("./res/test-trace/xml-security/v3/differenceMatrix-v3.csv", Long.class, "v3", "./res/test-trace/xml-security/");
        diffList.add(extDiff1);
        diffList.add(extDiff2);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>("./res/test-trace/xml-security/changeMatrix.csv", Long.class);
        HashMap<String, Object> criteria = Util.getDefaultPrioritizerCriteria();
        ResultMatrix resultMatrix = new ResultMatrix();
        List<String> referenceResults = changeMatrix.getTestsByChangeDesc();
        resultMatrix.setReferenceResult(referenceResults);
        LinkedList<String> order = null;
        order = Prioritizer.getExecutionOrder(changeMatrix, diffList, criteria);
        String id = "TraceComparer";
        resultMatrix.addResult(id, order);
        /*for (long i = 0; i < changeMatrix.getSize(); i += 1) {
            criteria.put(Variables.THRESHOLD_2, i);
            for (long j = 0; j < 1000; j += 100) {
                criteria.put(Variables.THRESHOLD_3, j);

            }
        }*/
        resultMatrix.printOrderedResults();
    }


}
