package orderbuilder.prioritizer;

import orderbuilder.comparer.test.CTDTBackTrackComparer;
import orderbuilder.comparer.test.CTDTComparer;
import orderbuilder.comparer.test.FarthestFromNoChange;
import orderbuilder.comparer.test.TestComparer;
import orderbuilder.evaluator.Results;
import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.DifferenceMatrix;
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

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria) throws Exception {
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
        }
        return comparer;
    }

    public static void main(String[] args) throws Exception {
        DifferenceMatrix<Long> diffMatrix = new DifferenceMatrix<>("./res/v2/differenceMatrix-v2.csv", Long.class);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>("./res/v2-v3.csv", Long.class);
        HashMap<String, Object> criteria = Util.getDefaultPrioritizerCriteria();
        Results results = new Results();
        List<String> referenceResults = new ArrayList<>();
        referenceResults.add("TestDifficultSituations.txt");
        referenceResults.add("TestTextAcess.txt");
        referenceResults.add("TestEmbeddedTokenizer.txt");
        referenceResults.add("TestTokenizerProperties.txt");
        referenceResults.add("TestExceptions.txt");
        referenceResults.add("TestTokenProperties.txt");
        referenceResults.add("TestExceptionsList.txt");
        referenceResults.add("TestJavaTokeninzing.txt");
        results.setReferenceResult(referenceResults);
        LinkedList<String> order = null;
        for (long i = 0; i < changeMatrix.getSize(); i += 1) {
            criteria.put(Variables.THRESHOLD_2, i);
            for (long j = 0; j < 1000; j += 100) {
                criteria.put(Variables.THRESHOLD_3, j);
                order = Prioritizer.getExecutionOrder(changeMatrix, diffMatrix, criteria);
                String id = "T2-" + i + "-T3-" + j;
                results.addResult(id, order);
            }
        }
        results.printOrderedResults();
    }


}
