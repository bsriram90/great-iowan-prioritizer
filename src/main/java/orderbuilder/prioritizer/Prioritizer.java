package orderbuilder.prioritizer;

import orderbuilder.comparer.test.*;
import orderbuilder.evaluator.Score;
import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import orderbuilder.util.Util;
import orderbuilder.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sriram on 23-03-2017.
 */
public class Prioritizer {

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria, String startTest) throws Exception {
        // get the right orderbuilder.comparer
        TestComparer comparer = getComparer(criteria);
        return comparer.getExecutionOrder(change, diff, criteria, startTest);
    }

    private static TestComparer getComparer(HashMap<String, Object> criteria) {
        TestComparer comparer = null;
        if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE)) {
            comparer = new CTDTComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.FARTHEST_FROM_NO_CHANGE)) {
            comparer = new FarthestFromNoChange();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE_BACKTRACK)) {
            comparer = new CTDTBackTrackComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.CLOSEST_TO_CHANGE_TRACE_COMPARER)) {
            comparer = new CTDTTraceComparer();
        } else if (criteria.get(Variables.PRIORITIZER_SEARCH_METHOD).equals(Variables.TEST_TRACE)) {
            comparer = new TestTraceComparer();
        }
        return comparer;
    }

    public static void main(String[] args) throws Exception {

        HashMap<String, Object> criteria = Util.getDefaultPrioritizerCriteria();
        String path = "./res/test-trace/ant/";
        String type = "spearman";
        String version = "V7";

        //Integer[] indices = new Integer[] {0,5,13,17,25,36,44,58,62,80};

        Integer[] indices = new Integer[]{0, 100, 200, 300, 400, 500, 600, 700, 800, 876};

        for (long index = 0; index < 100000; index += 1000) {
            System.out.print(index + ",");
            //criteria.put(Variables.THRESHOLD_1, index);
            criteria.put(Variables.THRESHOLD_3, 1000000l);
            getCorrelationScoreForMatrices(criteria,
                    path + "V7/differenceMatrix-pos-w-1.csv",
                    version,
                    path,
                    path + "changeMatrix-pos-w-1.csv",
                    "Positional Weighted",
                    type,
                    0);
        }

        /*        getCorrelationScoreForMatrices(criteria,
                path + "V7/differenceMatrix-set-1.csv",
                version,
                path,
                path + "changeMatrix-set-1.csv",
                "Set",
                type);*/
    }

    private static void getCorrelationScoreForMatrices(HashMap<String, Object> criteria, String diffFileName, String version, String path, String changeFileName, String name, String type, Integer startIndex) throws Exception {
        TestTraceDifferenceMatrix<Long> diffMatrix = new TestTraceDifferenceMatrix<>(diffFileName, Long.class, version, path);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>(changeFileName, Long.class);
        List<String> referenceResults = changeMatrix.getTestsByChangeDesc();
        List<DifferenceMatrix> diffList = new ArrayList<>();
        diffList.add(diffMatrix);
        LinkedList<String> order = Prioritizer.getExecutionOrder(changeMatrix, diffList, criteria, referenceResults.get(startIndex));

        //String change_file = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\change-status.txt";
        //String change_value = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\diff-changes.txt";

        //Validator validator = new Validator(change_file, change_value);


        //validator.printBandChangeSummary(order, diffMatrix, changeMatrix);
        /*ResultMatrix matrix = new ResultMatrix(diffMatrix);
        matrix.setReferenceResult(referenceResults);
        matrix.addResult(name, order);
        matrix.printOrderedResults();*/
        //System.out.println(name + " - " + order);
        Score.printCorrelationScoreByBands(referenceResults, order, 1.0f, type);
        //List<String> defOrder = Util.getLinesFromFile("C:\\Users\\Sriram\\Desktop\\RA\\ant-def-order.txt");
        List<String> failiures = Util.getLinesFromFile("./res/test-trace/ant/V8/failed-tests.txt");
        System.out.print("," + Score.getAPFDScore(order, failiures));
        System.out.println();
    }


}
