package model;

import util.Util;

import java.util.HashSet;
import java.util.LinkedList;
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
            indexTest.put(i,headers[i + 1]);
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

    private String getClosestTest(String test) {
        int index = (int) testIndex.get(test);
        int closest = 0;
        if(typeOfT == Long.class) {
            Long diff = (Long)matrix[index][0] - (Long)matrix[index][closest];
            for(int i = 1; i < size; i++) {
                if ((Long)matrix[index][i] - (Long)matrix[index][closest] < diff) {
                    closest = i;
                    diff = (Long)matrix[index][i] - (Long)matrix[index][closest];
                }
            }
        } else if (typeOfT == Double.class) {
            Double diff = (Double)matrix[index][0] - (Double)matrix[index][closest];
            for(int i = 1; i < size; i++) {
                if ((Double)matrix[index][i] - (Double)matrix[index][closest] < diff) {
                    closest = i;
                    diff = (Double)matrix[index][i] - (Double)matrix[index][closest];
                }
            }
        }
        return (String) indexTest.get(closest);
    }

    public String getClosestTest(LinkedList<String> tests) {
        if(tests.size() == 0) {
            return null;
        } else if (tests.size() == 1) {
            return getClosestTest(tests.getFirst());
        }
        String currentMin = null;

        Set<String> candidates = getRemainingTests(tests);
        if(typeOfT == Long.class) {
            Long score = 0l;
            for( String candidate : candidates ) {
                
            }
        } else if (typeOfT == Double.class) {

        }
        return currentMin;
    }

    private String getFarthestTest(String test) {
        int index = (int) testIndex.get(test);
        int farthest = 0;
        if(typeOfT == Long.class) {
            Long diff = (Long)matrix[index][0] - (Long)matrix[index][farthest];
            for(int i = 1; i < size; i++) {
                if ((Long)matrix[index][i] - (Long)matrix[index][farthest] > diff) {
                    farthest = i;
                    diff = (Long)matrix[index][i] - (Long)matrix[index][farthest];
                }
            }
        } else if (typeOfT == Double.class) {
            Double diff = (Double)matrix[index][0] - (Double)matrix[index][farthest];
            for(int i = 1; i < size; i++) {
                if ((Double)matrix[index][i] - (Double)matrix[index][farthest] > diff) {
                    farthest = i;
                    diff = (Double)matrix[index][i] - (Double)matrix[index][farthest];
                }
            }
        }
        return (String) indexTest.get(farthest);
    }

    private Set<String> getRemainingTests(List<String> excludeList) {
        Set<String> tests = testIndex.keySet();
        for(String test:excludeList) {
            tests.remove(test);
        }
        return tests;
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
