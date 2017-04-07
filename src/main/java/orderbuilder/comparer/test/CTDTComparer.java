package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.util.Util;
import orderbuilder.util.Variables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Sriram on 23-03-2017.
 */
public class CTDTComparer extends TestComparer {

    Logger logger = Logger.getLogger(CTDTComparer.class.getName());

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff1, HashMap<String, Object> criteria) throws Exception {
        DifferenceMatrix diff = diff1.get(0);
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        if (debug) {
            logger.info("Building execution order based on tests closest to differing tests with threshold - " + criteria.get(Variables.THRESHOLD_1));
        }
        // find the point in which the test starts to differ significantly from previous version
        Object[] results = TestComparerUtil.getOrderUntilDifferingTest(logger, change, null, (Long) criteria.get(Variables.THRESHOLD_1), debug);
        String differingTest = (String) results[0];
        LinkedList<String> order = (LinkedList<String>) results[1];
        // if nothing changed enough, return here
        if (differingTest == null) {
            if (debug) {
                logger.info("No test changed significantly. Order is same as previous run.");
            }
            return order;
        }
        // find first closest test
        String nextTest = this.getNextTest(differingTest, diff, debug);
        LinkedList<String> newOrder = new LinkedList<>(order);
        newOrder.add(differingTest);
        newOrder.add(nextTest);
        while (newOrder.size() < diff.getSize()) {
            // find test closest to all tests found since differing test
            nextTest = this.getNextTest(newOrder, null, diff, debug);
            newOrder.add(nextTest);
        }
        if (debug) {
            logger.info("All tests ordered.");
        }
        return newOrder;
    }

    private String getNextTest(String test, DifferenceMatrix differenceMatrix, boolean debug) {
        if (debug) {
            logger.info("Looking for tests closest to test - " + test);
        }
        int index = differenceMatrix.getIndexByTest(test);
        int closest = 0;
        if (index == 0) closest = 1;
        if (differenceMatrix.getTypeOfT() == Long.class) {
            Long[][] matrix = differenceMatrix.getLongMatrix();
            Long diff = Long.MAX_VALUE;
            for (int i = 0; i < differenceMatrix.getSize(); i++) {
                if (index != i) {
                    if (matrix[index][i] - matrix[index][closest] < diff) {
                        closest = i;
                        diff = matrix[index][i] - matrix[index][closest];
                    }
                }
            }
        } else if (differenceMatrix.getTypeOfT() == Double.class) {
            Double[][] matrix = (Double[][]) differenceMatrix.getMatrix();
            Double diff = matrix[index][0] - matrix[index][closest];
            for (int i = 1; i < differenceMatrix.getSize(); i++) {
                if (matrix[index][i] - matrix[index][closest] < diff) {
                    closest = i;
                    diff = matrix[index][i] - matrix[index][closest];
                }
            }
        }
        if (debug) {
            logger.info("Closest test identified as - " + differenceMatrix.getTestByIndex(closest));
        }
        return differenceMatrix.getTestByIndex(closest);
    }

    protected String getNextTest(LinkedList<String> tests, LinkedList<String> excludesFromCandidates, DifferenceMatrix differenceMatrix, boolean debug) {
        String currentMin = null;
        Set<String> candidates;
        if (excludesFromCandidates == null) {
            candidates = differenceMatrix.excludeTests(tests);
        } else {
            candidates = differenceMatrix.excludeTests(excludesFromCandidates);
        }
        if (debug) {
            String picked = Util.getStringFromCollection(tests);
            String cand = Util.getStringFromCollection(candidates);
            logger.info("Looking for test closest to - (" + picked + ") in candidate set - (" + cand + ")");
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
        if (differenceMatrix.getTypeOfT() == Long.class) {
            Long score = Long.MAX_VALUE;
            Long[][] matrix = differenceMatrix.getLongMatrix();
            for (String candidate : candidates) {
                Long currentScore = 0l;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : tests) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if (currentScore <= score) {
                    score = currentScore;
                    currentMin = candidate;
                }
            }
        } else if (differenceMatrix.getTypeOfT() == Double.class) {
            Double score = Double.MAX_VALUE;
            Double[][] matrix = (Double[][]) differenceMatrix.getMatrix();
            for (String candidate : candidates) {
                Double currentScore = 0.0;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : tests) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if (currentScore <= score) {
                    score = currentScore;
                    currentMin = candidate;
                }
            }
        }
        if (debug) {
            logger.info("Found closest test - " + currentMin);
        }
        return currentMin;
    }

    protected Long getDistanceOfTestFromTests(DifferenceMatrix diff, LinkedList<String> path, String nextTest) {
        Long distanceFromPath = 0l;
        Integer currentIndex = diff.getIndexByTest(nextTest);
        Long[][] diffMatrix = diff.getLongMatrix();
        for (String test : path) {
            Integer compareIndex = diff.getIndexByTest(test);
            distanceFromPath += diffMatrix[compareIndex][currentIndex];
        }
        return distanceFromPath;
    }

}
