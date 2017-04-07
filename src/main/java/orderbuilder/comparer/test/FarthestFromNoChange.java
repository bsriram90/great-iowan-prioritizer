package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.util.Util;
import orderbuilder.util.Variables;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Sriram on 25-03-2017.
 */
public class FarthestFromNoChange extends TestComparer {

    Logger logger = Logger.getLogger(FarthestFromNoChange.class.getName());

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff1, HashMap<String, Object> criteria) throws Exception {
        DifferenceMatrix diff = diff1.get(0);
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        if (debug) {
            logger.info("Building execution order based on tests farthest from tests with changes lesser than threshold - " + criteria.get(Variables.THRESHOLD_1));
        }
        LinkedHashSet<String> order = getTestsWithFewerChange(change, (Long) criteria.get(Variables.THRESHOLD_1), debug);
        // if nothing changed enough, return here
        if (order.size() == change.getSize()) {
            if (debug) {
                logger.info("No test changed significantly. Order is same as previous run.");
            }
            return new LinkedList<>(order);
        }
        while (order.size() < change.getSize()) {
            String test = getNextTest(diff, order, debug);
            order.add(test);
        }
        if (debug) {
            logger.info("All tests ordered.");
        }
        return new LinkedList<>(order);
    }

    private String getNextTest(DifferenceMatrix<Long> differenceMatrix, LinkedHashSet<String> order, boolean debug) {
        Set<String> candidates = differenceMatrix.excludeTests(order);
        if (debug) {
            logger.info("Looking for test farthest from tests - " + Util.getStringFromCollection(order) + " in candidate set - " + Util.getStringFromCollection(candidates));
        }
        if (candidates.size() == 0) {
            return null;
        } else if (candidates.size() == 1) {
            String test = candidates.iterator().next();
            if (debug) {
                logger.info("Only one test left - " + test);
            }
            return test;
        }
        String farthest = null;
        if (differenceMatrix.getTypeOfT() == Long.class) {
            Long score = Long.MIN_VALUE;
            Long[][] matrix = differenceMatrix.getLongMatrix();
            for (String candidate : candidates) {
                Long currentScore = 0l;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : order) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if (currentScore > score) {
                    score = currentScore;
                    farthest = candidate;
                }
            }
        } else if (differenceMatrix.getTypeOfT() == Double.class) {
            Double score = Double.MAX_VALUE;
            Double[][] matrix = (Double[][]) differenceMatrix.getMatrix();
            for (String candidate : candidates) {
                Double currentScore = 0.0;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : order) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if (currentScore > score) {
                    score = currentScore;
                    farthest = candidate;
                }
            }
        }
        if (debug) {
            logger.info("Found farthest test - " + farthest);
        }
        return farthest;
    }

    private LinkedHashSet<String> getTestsWithFewerChange(ChangeMatrix change, Long threshold, boolean debug) {
        if (debug) {
            logger.info("Looking for tests that have changed fewer than the threshold - " + threshold);
        }
        LinkedHashSet<String> unchangedTests = new LinkedHashSet<>();
        Long[][] matrix = change.getLongMatrix();
        for (int i = 0; i < change.getSize(); i++) {
            if (matrix[0][i] <= threshold) {
                unchangedTests.add(change.getTestByIndex(i));
            }
        }
        if (debug) {
            logger.info("Initial tests with no change - " + Util.getStringFromCollection(unchangedTests));
        }
        return unchangedTests;
    }


}
