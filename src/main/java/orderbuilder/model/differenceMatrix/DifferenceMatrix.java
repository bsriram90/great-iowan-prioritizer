package orderbuilder.model.differenceMatrix;

import orderbuilder.model.Test;
import orderbuilder.model.TestChangeMatrix;
import orderbuilder.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Sriram on 21-03-2017.
 */
public class DifferenceMatrix<T> extends TestChangeMatrix {

    HashMap<String, Test<T>> tests = new HashMap<>();

    public DifferenceMatrix(String fileName, Class<T> type) throws Exception {
        this(fileName, type, null);
    }

    public DifferenceMatrix(String fileName, Class<T> type, String version) throws Exception {
        super(fileName, type, version);
        List<String> lines = Util.getLinesFromFile(fileName);
        String[] headers = lines.get(0).split(",");
        this.size = headers.length - 1;
        matrix = (T[][]) new Object[size][size];
        for (int i = 0; i < size; i++) {
            testIndex.put(headers[i + 1], i);
            indexTest.put(i, headers[i + 1]);
        }
        for (int i = 1; i < lines.size(); i++) {
            Test test = new Test(getTestByIndex(i - 1), typeOfT);
            String line = lines.get(i);
            String[] entries = line.split(",");
            for (int j = 1 + i; j < entries.length; j++) {
                Object val = null;
                if (typeOfT == Long.class) {
                    val = Long.parseLong(entries[j]);
                } else if (typeOfT == Double.class) {
                    val = Double.parseDouble(entries[j]);
                }
                matrix[i - 1][j - 1] = (T) val;
                matrix[j - 1][i - 1] = (T) val;
            }
            tests.put((String) indexTest.get(i - 1), test);
        }
        for (int i = 0; i < size; i++) {
            Test t1 = tests.get(getTestByIndex(i));
            for (int j = i + 1; j < size; j++) {
                if (j != i) {
                    Test t2 = tests.get(getTestByIndex(j));
                    t1.addTestDifference(t2, matrix[i][j]);
                    t2.addTestDifference(t1, matrix[j][i]);
                }
            }
        }
        setMatrix();
    }

    public Test getTestByName(String name) {
        return tests.get(name);
    }

    public T getDifferenceBetweenTests(String test1, String test2) {
        Test t = tests.get(test1);
        return (T) t.getDifferenceFromTest(test2);
    }

    public List<String> getTestsOrderedByClosenessToTest(String test) {
        Test t = getTestByName(test);
        return t.getTestsByDifferenceOrder();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        DifferenceMatrix<Long> matrix = new DifferenceMatrix<>("./res/test-trace/xml-security/v2/differenceMatrix-v2.csv", Long.class);
        matrix.printMatrix();
    }

    public void printMatrix() {
        for (String key : (Set<String>) testIndex.keySet()) {
            System.out.println(key + " - " + testIndex.get(key));
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print(System.lineSeparator());
        }
    }
}
