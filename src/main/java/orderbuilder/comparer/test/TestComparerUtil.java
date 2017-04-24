package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by Sriram on 02-04-2017.
 */
public class TestComparerUtil {

    public static Object[] getOrderUntilDifferingTest(Logger logger, ChangeMatrix<Long> changeMatrix, String startTest, Long threshold, boolean debug, Collection<String> excludeTests) throws Exception {
        Object[] results = new Object[2];
        Long[][] matrix = changeMatrix.getLongMatrix();
        LinkedList<String> order = new LinkedList<>();
        if (excludeTests == null) {
            excludeTests = new ArrayList<>();
        }
        if (changeMatrix.getTypeOfT() != Long.class) {
            throw new Exception("Invalid type. Matrix is not Long");
        }
        int start = -1;
        if (startTest != null) {
            start = changeMatrix.getIndexByTest(startTest);
        }
        if (debug) {
            logger.info("Looking for change in execution order starting from test with index - " + start + ", with a difference threshold of - " + threshold);
        }
        for (int i = start + 1; i < changeMatrix.getSize(); i++) {
            if (!excludeTests.contains(changeMatrix.getTestByIndex(i))) {
                if (matrix[0][i] >= threshold) {
                    if (debug) {
                        logger.info("Trace differs significantly at test - " + changeMatrix.getTestByIndex(i));
                    }
                    results[0] = changeMatrix.getTestByIndex(i);
                    break;
                }
                order.add(changeMatrix.getTestByIndex(i));
            }
        }
        results[1] = order;
        return results;
    }


}
