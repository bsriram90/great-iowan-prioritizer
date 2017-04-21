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

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria) throws Exception {
        TestTraceDifferenceMatrix<Long> prev = (TestTraceDifferenceMatrix) diff.get(0);
        TestTraceDifferenceMatrix<Long> current = (TestTraceDifferenceMatrix) diff.get(1);
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        Long differenceThreshold = (Long) criteria.get(Variables.THRESHOLD_1);
        Long backTrackThreshold = (Long) criteria.get(Variables.THRESHOLD_2);
        Long pathFitness = (Long) criteria.get(Variables.THRESHOLD_3);
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
        List<String> testCasesCovered = new ArrayList<>();
        List<String> coreTests = new ArrayList<>();
        coreTests.add(differingTest);
        String currentTest = differingTest;
        String currentTestCase = prev.getTestCaseByTest(differingTest);
        while (newOrder.size() < change.getSize()) {
            List<String> insideTestCaseOrder = prev.getOrderedClosestTestsInTestCase(currentTest, pathFitness, currentTestCase, newOrder, change);
            if (insideTestCaseOrder != null && insideTestCaseOrder.size() > 1) {
                coreTests.add(currentTest);
                newOrder.addAll(insideTestCaseOrder);
            }
            currentTest = getNextTest(prev, change, testCasesCovered, coreTests, pathFitness);
            testCasesCovered.add(prev.getTestCaseByTest(currentTest));
        }
        return newOrder;
    }

    private String getNextTest(TestTraceDifferenceMatrix<Long> prev, ChangeMatrix<Long> change, List<String> excludeTestCase, List<String> coreTests, Long threshold) {
        List<String> orderedByChange = change.getTestsByChangeDesc();

        Set<String> excludeSet = new HashSet<>();
        for (String testCase : excludeTestCase) {
            excludeSet.addAll(prev.getAllTestsForTestCase(testCase));
        }
        orderedByChange.removeAll(excludeSet);
        while (orderedByChange.size() > 0) {
            String test = orderedByChange.get(0);
            if (change.getChangeByTest(test) <= threshold) {
                String badTestCase = prev.getTestCaseByTest(test);
                Set<String> badTests = prev.getAllTestsForTestCase(badTestCase);
                orderedByChange.removeAll(badTests);
            }
            return test;
        }
        return null;
    }
}
