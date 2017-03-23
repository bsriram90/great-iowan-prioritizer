package comparer.test;

import model.DifferenceMatrix;

import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Sriram on 23-03-2017.
 */
public class ClosestToFailiureComparer implements TestComparer{

    @Override
    public String getNextTest(String test, DifferenceMatrix differenceMatrix, boolean debug) {
        int index = differenceMatrix.getIndexByTest(test);
        int closest = 0;
        if(differenceMatrix.getTypeOfT() == Long.class) {
            Long[][] matrix = (Long[][]) differenceMatrix.getMatrix();
            Long diff = matrix[index][0] - matrix[index][closest];
            for(int i = 1; i < differenceMatrix.getSize(); i++) {
                if (matrix[index][i] - matrix[index][closest] < diff) {
                    closest = i;
                    diff = matrix[index][i] - matrix[index][closest];
                }
            }
        } else if (differenceMatrix.getTypeOfT() == Double.class) {
            Double[][] matrix = (Double[][]) differenceMatrix.getMatrix();
            Double diff = matrix[index][0] - matrix[index][closest];
            for(int i = 1; i < differenceMatrix.getSize(); i++) {
                if (matrix[index][i] - matrix[index][closest] < diff) {
                    closest = i;
                    diff = matrix[index][i] - matrix[index][closest];
                }
            }
        }
        return differenceMatrix.getTestByIndex(closest);
    }

    @Override
    public String getNextTest(LinkedList<String> tests, DifferenceMatrix differenceMatrix, boolean debug) {
        String currentMin = null;
        Set<String> candidates = differenceMatrix.excludeTests(tests);
        if(differenceMatrix.getTypeOfT() == Long.class) {
            Long score = Long.MAX_VALUE;
            Long[][] matrix = (Long[][]) differenceMatrix.getMatrix();
            for( String candidate : candidates ) {
                Long currentScore = 0l;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : tests) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if(currentScore < score) {
                    score = currentScore;
                    currentMin = candidate;
                }
            }
        } else if (differenceMatrix.getTypeOfT() == Double.class) {
            Double score = Double.MAX_VALUE;
            Double[][] matrix = (Double[][]) differenceMatrix.getMatrix();
            for( String candidate : candidates ) {
                Double currentScore = 0.0;
                int candidateIndex = differenceMatrix.getIndexByTest(candidate);
                for (String test : tests) {
                    int testIndex = differenceMatrix.getIndexByTest(test);
                    currentScore += matrix[candidateIndex][testIndex];
                }
                if(currentScore < score) {
                    score = currentScore;
                    currentMin = candidate;
                }
            }
        }
        return currentMin;
    }
}
