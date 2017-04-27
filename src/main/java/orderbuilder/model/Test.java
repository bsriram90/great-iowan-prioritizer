package orderbuilder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Sriram on 26-04-2017.
 */
public class Test<T> {

    private TestCase testCase;
    private String testName;
    private String closestTest;
    private HashMap<String, T> testDifference = new HashMap<>();
    private TreeMap<T, String> differenceTest = new TreeMap<>();
    private Class<T> typeOfT;

    public Test(String testName, Class<T> type) {
        this.testName = testName;
        this.typeOfT = type;
    }

    public Test(TestCase testCase, String testName, Class<T> type) {
        this(testName, type);
        this.testCase = testCase;
    }

    public void addTestDifference(Test test, T difference) throws Exception {
        addTestDifference(test.getTestName(), difference);
    }

    public void addTestDifference(String test, T difference) throws Exception {
        testDifference.put(test, difference);
        if (!differenceTest.containsKey(difference)) {
            differenceTest.put(difference, test);
        } else {
            if (typeOfT.equals(Double.class)) {
                Double diff = (Double) difference;
                while (!differenceTest.containsKey(diff)) {
                    diff = diff + 0.1;
                }
                differenceTest.put((T) diff, test);
            } else {
                Long diff = (Long) difference;
                while (!differenceTest.containsKey(diff)) {
                    diff = diff++;
                }
                differenceTest.put((T) diff, test);
            }

        }
        if (closestTest == null || closestTest.trim().equals("")) {
            closestTest = test;
        } else {
            Double closest;
            Double diff;
            if (typeOfT.equals(Double.class)) {
                closest = (Double) testDifference.get(closestTest);
                diff = (Double) difference;
            } else {
                closest = ((Long) testDifference.get(closestTest)).doubleValue();
                diff = ((Long) difference).doubleValue();
            }
            if (diff < closest) {
                closestTest = test;
            }
        }
    }

    public T getDifferenceFromTest(String test) {
        return testDifference.get(test);
    }

    public T getDistanceOfClosestTest() {
        return testDifference.get(closestTest);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getClosestTest() {
        return closestTest;
    }

    public void setClosestTest(String closestTest) {
        this.closestTest = closestTest;
    }

    public HashMap<String, T> getTestDifference() {
        return testDifference;
    }

    public void setTestDifference(HashMap<String, T> testDifference) {
        this.testDifference = testDifference;
    }

    public Class<T> getTypeOfT() {
        return typeOfT;
    }

    public void setTypeOfT(Class<T> typeOfT) {
        this.typeOfT = typeOfT;
    }

    public List<String> getTestsByDifferenceOrder() {
        List<String> orderedValues = new ArrayList<>(differenceTest.size());
        for (T diff : differenceTest.keySet()) {
            orderedValues.add(differenceTest.get(diff));
        }
        return orderedValues;
    }
}
