package orderbuilder.model.differenceMatrix;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.util.Util;

import java.io.File;
import java.util.*;

/**
 * Created by Sriram on 21-04-2017.
 */
public class TestTraceDifferenceMatrix<T> extends DifferenceMatrix {

    HashMap<String, HashMap<String, List<String>>> testcaseTraces = new HashMap<>();
    HashMap<String, String> testDirectory = new HashMap<>();

    public HashMap<String, HashMap<String, List<String>>> getTestcaseTraces() {
        return testcaseTraces;
    }

    public void setTestcaseTraces(HashMap<String, HashMap<String, List<String>>> testcaseTraces) {
        this.testcaseTraces = testcaseTraces;
    }

    public TestTraceDifferenceMatrix(String fileName, Class type) throws Exception {
        super(fileName, type);

    }

    public TestTraceDifferenceMatrix(String fileName, Class type, String version) throws Exception {
        this(fileName, type, version, "");
    }

    public Set<String> getAllTestsForTestCase(String testCase) {
        return testcaseTraces.get(testCase).keySet();
    }

    public TestTraceDifferenceMatrix(String fileName, Class type, String version, String path) throws Exception {
        super(fileName, type, version);
        String tracePath = path + version;
        File root = new File(tracePath);
        String[] dirs = Util.getAllDirectories(root);
        for (String dir : dirs) {
            File testCase = new File(tracePath + "/" + dir);
            String[] tests = Util.getAllFiles(testCase);
            HashMap<String, List<String>> testTraces = new HashMap<>();
            for (String test : tests) {
                List<String> trace = Util.getLinesFromFile(tracePath + "/" + dir + "/" + test);
                // TODO fold over recursion
                testTraces.put(test, trace);
                testDirectory.put(test, dir);
            }
            testcaseTraces.put(dir, testTraces);
        }
    }

    public String getTestCaseByTest(String test) {
        return testDirectory.get(test);
    }

    public static void main(String[] args) throws Exception {
        TestTraceDifferenceMatrix<Long> diff = new TestTraceDifferenceMatrix<Long>("res/test-trace/xml-security/v2/differenceMatrix-v2.csv", Long.class, "v2", "res/test-trace/xml-security/");
        diff.printMatrix();
    }

    public List<String> getOrderedClosestTestsInTestCase(String currentTest, Long threshold, String currentTestCase, Collection<String> excludeTests, ChangeMatrix<Long> change) {
        List<String> order = new LinkedList<>();
        HashMap<String, List<String>> traces = testcaseTraces.get(currentTestCase);
        List<String> currentTrace = traces.get(currentTest);
        traces.keySet().removeAll(excludeTests);
        traces.remove(currentTest);
        List<TestSimilarity> traceFitness = new ArrayList<>();
        for (String test : traces.keySet()) {
            TestSimilarity sim = new TestSimilarity(test, Util.jaccardSimilarity(traces.get(test), currentTrace));
            traceFitness.add(sim);
        }
        Collections.sort(traceFitness);
        Iterator<TestSimilarity> itr = traceFitness.iterator();
        boolean pathStillFit = true;
        while (pathStillFit) {
            String nextTest = itr.next().test;
            order.add(nextTest);
            if (change.getChangeByTest(nextTest) <= threshold) {
                pathStillFit = false;
            }
        }
        return order;
    }

    public class TestSimilarity implements Comparable<TestSimilarity> {
        String test;
        Float similarity;

        public TestSimilarity(String t, Float f) {
            test = t;
            similarity = f;
        }

        @Override
        public int compareTo(TestSimilarity o) {
            return o.similarity.compareTo(this.similarity);
        }
    }
}
