package model;

import util.Util;

import java.util.List;
import java.util.Set;

/**
 * Created by Sriram on 21-03-2017.
 */
public class DifferenceMatrix<T> extends TestChangeMatrix{


    public DifferenceMatrix(String fileName, Class<T> type) throws Exception {
        super(fileName,type);
        List<String> lines = Util.getLinesFromFile(fileName);
        String[] headers = lines.get(0).split(",");
        this.size = headers.length - 1;
        matrix = (T[][]) new Object[size][size];
        for (int i = 0; i < size; i++) {
            testIndex.put(headers[i + 1], i);
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] entries = line.split(",");
            int index = (int) testIndex.get(entries[0]);
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
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        DifferenceMatrix<Long> matrix = new DifferenceMatrix<>("./res/v2/differenceMatrix-v2.csv", Long.class);
        matrix.printMatrix();
    }

    public void printMatrix() {
        for(String key : (Set<String>)testIndex.keySet()) {
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
