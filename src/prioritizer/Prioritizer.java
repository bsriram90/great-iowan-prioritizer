package prioritizer;

import comparer.test.ClosestToDifferingTestsComparer;
import comparer.test.FarthestFromNoChange;
import comparer.test.TestComparer;
import model.ChangeMatrix;
import model.DifferenceMatrix;
import util.Util;
import util.Variables;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Sriram on 23-03-2017.
 */
public class Prioritizer {

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria) throws Exception {
        // get the right comparer
        TestComparer comparer = getComparer(criteria);
        return comparer.getExecutionOrder(change, diff, criteria);
    }

    private static TestComparer getComparer(HashMap<String, Object> criteria) {
        TestComparer comparer = null;
        if(criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE)) {
            comparer = new ClosestToDifferingTestsComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.FARTHEST_FROM_NO_CHANGE)) {
            comparer = new FarthestFromNoChange();
        }
        return comparer;
    }

    public static void main(String[] args) throws Exception{
        DifferenceMatrix<Long> diffMatrix = new DifferenceMatrix<>("./res/v2/differenceMatrix-v2.csv", Long.class);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>("./res/v2-v3.csv", Long.class);
        LinkedList<String> order = Prioritizer.getExecutionOrder(changeMatrix,diffMatrix, Util.getDefaultPrioritizerCriteria());
        Thread.sleep(2000);
        for(String test : order) {
            System.out.println(test);
        }
    }


}
