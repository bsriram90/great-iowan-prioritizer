package model;

import util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Sriram on 23-03-2017.
 */
public class ChangeMatrix<T> extends TestChangeMatrix {

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

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        ChangeMatrix<Long> matrix = new ChangeMatrix<>("./res/v2-v3.csv", Long.class);
        matrix.printMatrix();
    }

    public Object[] getOrderAndDifferingTest(String startTest, Long threshold) throws Exception {
        Object[] results = new Object[2];
        LinkedList<String> order = new LinkedList<>();
        if (this.typeOfT != Long.class) {
            throw new Exception("Invalid type. Matrix is not Long");
        }
        int start = -1;
        if (startTest != null) {
            start = (int) testIndex.get(startTest);
        }
        for (int i = start + 1; i < size; i++) {
            order.add((String) indexTest.get(i));
            if ((Long) matrix[0][i] >= threshold) {
                results[0] = indexTest.get(i);
                break;
            }
        }
        results[1] = order;
        return results;
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

}
