package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.DifferenceMatrix;
import orderbuilder.util.Util;
import orderbuilder.util.Variables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Sriram on 23-03-2017.
 */
public class ClosestToDifferingTestsComparer extends TestComparer {

    Logger logger = Logger.getLogger(ClosestToDifferingTestsComparer.class.getName());

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria) throws Exception {
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        if (debug) {
            logger.info("Building execution order based on tests closest to differing tests with threshold - " + criteria.get(Variables.THRESHOLD));
        }
        // find the point in which the test starts to differ significantly from previous version
        Object[] results = getOrderUntilDifferingTest(change, null, (Long) criteria.get(Variables.THRESHOLD), debug);
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
        LinkedList<String> newOrder = new LinkedList<>();
        newOrder.add(differingTest);
        newOrder.add(nextTest);
        while ((newOrder.size() + order.size()) < diff.getSize()) {
            // find test closest to all tests found since differing test
            nextTest = this.getNextTest(newOrder, diff, debug);
            newOrder.add(nextTest);
        }
        if (debug) {
            logger.info("All tests ordered.");
        }
        // merge unchanged and changed tests
        order.addAll(newOrder);
        if (debug) {
            logger.info("Existing order and new order merged.");
        }
        return order;
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

    private String getNextTest(LinkedList<String> tests, DifferenceMatrix differenceMatrix, boolean debug) {
        String currentMin = null;
        Set<String> candidates = differenceMatrix.excludeTests(tests);
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
                if (currentScore < score) {
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
                if (currentScore < score) {
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

    private Object[] getOrderUntilDifferingTest(ChangeMatrix<Long> changeMatrix, String startTest, Long threshold, boolean debug) throws Exception {
        Object[] results = new Object[2];
        Long[][] matrix = changeMatrix.getLongMatrix();
        LinkedList<String> order = new LinkedList<>();
        if (changeMatrix.getTypeOfT() != Long.class) {
            throw new Exception("Invalid type. Matrix is not Long");
        }
        int start = -1;
        if (startTest != null) {
            start = changeMatrix.getIndexByTest(startTest);
        }
        if (debug) {
            logger.info("Looking for change in execution order starting from test with index - " + start + ", with a difference threshold of - " + threshold);
        }
        for (int i = start + 1; i < changeMatrix.getSize(); i++) {
            if (matrix[0][i] >= threshold) {
                if (debug) {
                    logger.info("Trace differs significantly at test - " + changeMatrix.getTestByIndex(i));
                }
                results[0] = changeMatrix.getTestByIndex(i);
                break;
            }
            order.add(changeMatrix.getTestByIndex(i));
        }
        results[1] = order;
        return results;
    }
}
