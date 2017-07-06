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
        String path = "./res/test-trace/xml-security/";
        String type = "spearman";
        String version = "V2";

        Integer[] indices = new Integer[]{0, 5, 13, 17, 25, 36, 44, 58, 62, 80};

        //Integer[] indices = new Integer[]{0, 100, 200, 300, 400, 500, 600, 700, 800, 876};

        for (int index = 0; index < 83; index++) {
            System.out.print(index + ",");
            criteria.put(Variables.THRESHOLD_1, 1000000000l);
            //criteria.put(Variables.THRESHOLD_3, 1000000l);
            getCorrelationScoreForMatrices(criteria,
                    path + "V2/differenceMatrix-pos-w.csv",
                    version,
                    path,
                    path + "changeMatrix-pos-w.csv",
                    "Positional Weighted",
                    type,
                    index);
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
        Double totalCorr = 0.0;
        Double totalAPFD = 0.0;
        List<String> failiures = Util.getLinesFromFile("./res/test-trace/xml-security/V3-seeded/failed-tests.txt");
        Float[] bandValues = new Float[11];
        for (int i = 0; i < 11; i++) {
            bandValues[i] = new Float(0);
        }
        for (int i = 0; i < 5; i++) {
            LinkedList<String> order = Prioritizer.getExecutionOrder(changeMatrix, diffList, criteria, referenceResults.get(startIndex));
            totalCorr += Score.getCorrelationScore(referenceResults, order, type);
            totalAPFD += Score.getAPFDScore(order, failiures);

            //String change_file = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\change-status.txt";
            //String change_value = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\diff-changes.txt";

            //Validator validator = new Validator(change_file, change_value);


            //validator.printBandChangeSummary(order, diffMatrix, changeMatrix);
            /*ResultMatrix matrix = new ResultMatrix(diffMatrix);
            matrix.setReferenceResult(referenceResults);
            matrix.addResult(name, order);
            matrix.printOrderedResults();*/
            //System.out.println(name + " - " + order);
            //Score.printCorrelationScoreByBands(referenceResults, order, 0.10f, type);
            /*Float[] res = Score.getCorrelationScoreByBands(referenceResults, order, 0.10f, type);
            for(int j=0; j<11; j++) {
                bandValues[j] += res[j];
            }*/
            //List<String> defOrder = Util.getLinesFromFile("C:\\Users\\Sriram\\Desktop\\RA\\ant-def-order.txt");
            /*System.out.print("Score - " + Score.getCorrelationScore(referenceResults, order, type));
            System.out.print("," + Score.getAPFDScore(order, failiures));
            System.out.println();*/
        }
        System.out.println(totalCorr / 5.0 + "," + totalAPFD / 5.0);
        /*for(int j=0; j<11; j++) {
            System.out.print(String.format("%.5g",bandValues[j]/5.0));
            if(j != 10) {
                System.out.print(" & ");
            }
        }
        System.out.println();*/
    }


}
