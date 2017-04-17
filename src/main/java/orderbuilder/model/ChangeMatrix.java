package orderbuilder.model;

import orderbuilder.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Created by Sriram on 23-03-2017.
 */
public class ChangeMatrix<T> extends TestChangeMatrix {

    private static final Logger logger = Logger.getLogger(ChangeMatrix.class.getName());

    public ChangeMatrix(String fileName, Class<T> type) throws Exception {
        super(fileName, type);
        List<String> lines = Util.getLinesFromFile(fileName);
        String[] headers = lines.get(0).split(",");
        this.size = headers.length;
        matrix = (T[][]) new Object[1][size];
        for (int i = 0; i < size; i++) {
            testIndex.put(headers[i], i);
            indexTest.put(i, headers[i]);
        }
        String[] entries = lines.get(1).split(",");
        for (int j = 0; j < entries.length; j++) {
            Object val = null;
            if (typeOfT == Long.class) {
                val = Long.parseLong(entries[j]);
            } else if (typeOfT == Double.class) {
                val = Double.parseDouble(entries[j]);
            }
            matrix[0][j] = (T) val;
        }
    }

    public T getChangeByTest(String test) {
        return (T) matrix[0][getIndexByTest(test)];
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        ChangeMatrix<Long> matrix = new ChangeMatrix<>("./res/xml-security/v2-v3.csv", Long.class);
        matrix.printMatrix();
        List<String> sorted = matrix.getTestsByChangeAsc();
        for (String s : sorted) {
            System.out.println(s);
        }
    }

    public void printMatrix() {
        for (String key : (Set<String>) testIndex.keySet()) {
            System.out.println(key + " - " + testIndex.get(key));
        }
        for (int j = 0; j < size; j++) {
            System.out.print(matrix[0][j] + "\t");
        }
        System.out.print(System.lineSeparator());
    }

    public List<String> getTestsByChangeAsc() {
        List<String> order = new ArrayList<>();
        TreeMap<Long, String> sorter = new TreeMap<>();
        for (Object test : testIndex.keySet()) {
            String testName = (String) test;
            Long change = (Long) getChangeByTest(testName);
            while (sorter.containsKey(change)) {
                change++;
            }
            sorter.put(change, testName);
        }
        Set<Long> keys = sorter.keySet();
        for (Long key : keys) {
            order.add(sorter.get(key));
        }
        return order;
    }

}
