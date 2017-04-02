package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.DifferenceMatrix;
import orderbuilder.util.Variables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by Sriram on 02-04-2017.
 * <p>
 * Threshold 1 - similarity threshold to find differing tests
 * Threshold 2 - backtrack threshold
 * Threshold 3 - similarity threshold to determine if path is still viable
 */
public class CTDTBackTrackComparer extends CTDTComparer {

    Logger logger = Logger.getLogger(TestComparer.class.getName());

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria) throws Exception {
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        Long differenceThreshold = (Long) criteria.get(Variables.THRESHOLD_1);
        Long backTrackThreshold = (Long) criteria.get(Variables.THRESHOLD_2);
        Long pathFitness = (Long) criteria.get(Variables.THRESHOLD_3);
        // TODO implement debug logging
        // find the point in which the test starts to differ significantly from previous version
        Object[] results = TestComparerUtil.getOrderUntilDifferingTest(logger, change, null, differenceThreshold, debug);
        String differingTest = (String) results[0];
        LinkedList<String> newOrder = (LinkedList<String>) results[1];
        // if nothing changed enough, return here
        if (differingTest == null) {
            if (debug) {
                logger.info("No test changed significantly. Order is same as previous run.");
            }
            return newOrder;
        }
        LinkedList<String> path = new LinkedList<>();
        path.add(differingTest);
        newOrder.add(differingTest);
        String nextTest;
        int backTrackCount = 0;
        while (newOrder.size() < diff.getSize()) {
            nextTest = this.getNextTest(path, newOrder, diff, debug);
            newOrder.add(nextTest);
            // TODO handle double data type here
            if ((Long) change.getChangeByTest(nextTest) < pathFitness) {
                // time to backtrack
                if (path.size() >= 2) {
                    // continue backtracking until threshold
                    if (backTrackCount++ <= backTrackThreshold) {
                        path.removeLast();
                    } else {
                        // reset backtrack count and start from root
                        backTrackCount = 0;
                        String root = path.getFirst();
                        path = new LinkedList<>();
                        path.add(root);
                    }
                }
            } else {
                path.add(nextTest);
            }
        }
        return newOrder;
    }
}
