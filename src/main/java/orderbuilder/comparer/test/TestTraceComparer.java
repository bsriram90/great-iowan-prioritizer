package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import orderbuilder.util.Variables;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Sriram on 21-04-2017.
 */
public class TestTraceComparer extends TestComparer {

    Logger logger = Logger.getLogger(TestTraceComparer.class.toString());
    // HashMap<String, List<String>> differingTraces = new HashMap<>();

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria, String startTest) throws Exception {
        TestTraceDifferenceMatrix<Long> prev = (TestTraceDifferenceMatrix) diff.get(0);
        // TestTraceDifferenceMatrix<Long> current = (TestTraceDifferenceMatrix) diff.get(1);
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        Long differenceThreshold = (Long) criteria.get(Variables.THRESHOLD_1);
        Long backTrackThreshold = (Long) criteria.get(Variables.THRESHOLD_2);
        Long pathFitness = (Long) criteria.get(Variables.THRESHOLD_3);
        Object[] results = null;
        //TestComparerUtil.getOrderUntilDifferingTest(logger, change, null, differenceThreshold, debug, null);
        String differingTest = startTest;
        LinkedList<String> newOrder = new LinkedList<>();
        // if nothing changed enough, return here
        if (differingTest == null) {
            if (debug) {
                logger.info("No test changed significantly. Order is same as previous run.");
            }
            return newOrder;
        }
        List<String> testCasesCovered = new ArrayList<>();
        HashSet<String> coreTests = new HashSet<>();
        coreTests.add(differingTest);
        // differingTraces.put(differingTest, getTraceDifference(prev.getTraceForTest(differingTest), current.getTraceForTest(differingTest)));
        String currentTest = differingTest;
        String currentTestCase = prev.getTestCaseByTest(differingTest);
        testCasesCovered.add(currentTestCase);
        boolean testsDiffered = false;
        while (newOrder.size() < change.getSize()) {
            newOrder.add(currentTest);
            if ((Long) change.getChangeByTest(currentTest) >= pathFitness) {
                coreTests.add(currentTest);
                if (!testsDiffered) {
                    testsDiffered = true;
                    coreTests.clear();
                    coreTests.add(currentTest);
                }
                List<String> insideTestCaseOrder = prev.getOrderedClosestTestsInTestCase(currentTest, pathFitness, currentTestCase, newOrder, change);
                if (insideTestCaseOrder != null && insideTestCaseOrder.size() >= 1) {
                    //coreTests.add(currentTest);
                    //differingTraces.put(currentTest, getTraceDifference(prev.getTraceForTest(currentTest), current.getTraceForTest(currentTest)));
                    newOrder.addAll(insideTestCaseOrder);
                }
            }
            if (testCasesCovered.size() >= prev.getNumOfTestCases() && newOrder.size() < change.getSize()) {
                testCasesCovered = new ArrayList<>();
                coreTests = new HashSet<>();
                results = TestComparerUtil.getOrderUntilDifferingTest(logger, change, null, differenceThreshold, debug, newOrder);
                newOrder.addAll((LinkedList<String>) results[1]);
                currentTest = (String) results[0];
                if (currentTest != null) {
                    coreTests.add(currentTest);
                    //differingTraces.put(currentTest, getTraceDifference(prev.getTraceForTest(currentTest), current.getTraceForTest(currentTest)));
                }
            } else {
                currentTest = getNextTest(prev, testCasesCovered, coreTests, newOrder, testsDiffered);
                if (currentTest == null) {
                    testCasesCovered = new ArrayList<>();
                    coreTests = new HashSet<>();
                    results = TestComparerUtil.getOrderUntilDifferingTest(logger, change, null, differenceThreshold, debug, newOrder);
                    newOrder.addAll((LinkedList<String>) results[1]);
                    currentTest = (String) results[0];
                    if (currentTest != null) {
                        coreTests.add(currentTest);
                        //differingTraces.put(currentTest, getTraceDifference(prev.getTraceForTest(currentTest), current.getTraceForTest(currentTest)));
                    }
                }
            }
            currentTestCase = prev.getTestCaseByTest(currentTest);
            testCasesCovered.add(currentTestCase);
        }
        return newOrder;
    }

    private List<String> getTraceDifference(List<String> prevTrace, List<String> currTrace) {
        /*int maxLength = (prevTrace.size() > currTrace.size()) ? prevTrace.size() : currTrace.size();
        int differenceIndex = 0;
        for(int i = 0; i < maxLength; i++) {
            if(!currTrace.get(i).equals(prevTrace.get(i))) {
                differenceIndex = i;
                break;
            }
        }
        List<String> difference = new ArrayList<>();
        difference.addAll(currTrace.subList(differenceIndex, currTrace.size()));*/

        List<String> temp1 = new ArrayList<>(prevTrace);
        temp1.removeAll(currTrace);
        List<String> temp2 = new ArrayList<>(currTrace);
        temp2.removeAll(prevTrace);
        temp1.addAll(temp2);
        return temp1;
    }

    private String getNextTest(TestTraceDifferenceMatrix<Long> prev, List<String> excludeTestCase, Collection<String> coreTests, List<String> excludeTests, boolean testsDiffered) {
        Set<String> candidates = prev.getAllTests();
        if (excludeTests != null && excludeTests.size() > 1) {
            candidates.removeAll(excludeTests);
        }
        candidates.removeAll(coreTests);
        for (String testCase : excludeTestCase) {
            candidates.removeAll(prev.getAllTestsForTestCase(testCase));
        }
        String closest = null;
        Float dist = 0.0f;
        if (!testsDiffered) {
            dist = Float.MAX_VALUE;
        }
        // Float dist = 0l;
        for (String candidate : candidates) {
            Float testFitness = 0.0f;
            for (String coreTest : coreTests) {
                // testFitness += Util.jaccardSimilarity(prev.getTraceForTest(candidate), differingTraces.get(coreTest));
                testFitness += (Long) prev.getDifferenceBetweenTests(candidate, coreTest);
            }
            testFitness = testFitness / coreTests.size();
            if (testsDiffered) {
                if (testFitness <= dist) {
                    closest = candidate;
                    dist = testFitness;
                }
            } else {
                if (testFitness >= dist) {
                    closest = candidate;
                    dist = testFitness;
                }
            }
        }
        return closest;
    }
}
