package orderbuilder.evaluator;

import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import orderbuilder.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sriram on 13-05-2017.
 */
public class Score {

    public static Float lineDiffScore(List<String> order, List<String> referenceResult, TestTraceDifferenceMatrix diff) {
        Float score = 0.0f;
        for (int i = 0; i < order.size(); i++) {
            if (!order.get(i).equals(referenceResult.get(i))) {
                Object s = diff.getDifferenceBetweenTests(order.get(i), referenceResult.get(i));
                if (s instanceof Float) {
                    score += (Float) s;
                } else {
                    score += ((Long) s).floatValue();
                }

            }
        }
        return score;
    }

    public static void printCorrelationScoreByBands(List<String> referenceResult, List<String> order, Float band, String type) {
        ArrayList<String> orderArrayList = new ArrayList<>(order);
        ArrayList<Integer> reference = new ArrayList<>(order.size());
        int bandSize = Math.round(referenceResult.size() * band);
        for (int i = 0; i < referenceResult.size(); i++) {
            reference.add(i + 1);
        }
        StringBuilder header = new StringBuilder("");
        StringBuilder body = new StringBuilder("");
        int n = (int) Math.ceil((float) referenceResult.size() / bandSize);
        Float spearman_n = Float.valueOf((referenceResult.size() * ( referenceResult.size() * referenceResult.size() - 1)));
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> result = new ArrayList<>(order.size());
            int limit = (i * bandSize + bandSize > referenceResult.size()) ? referenceResult.size() : i * bandSize + bandSize;
            List<String> referenceSubset = referenceResult.subList(0, limit);
            for (String test : referenceSubset) {
                result.add(orderArrayList.indexOf(test) + 1);
            }
            header.append("Band-" + i + "(Size=" + result.size() + "),");
            Float score = null;
            if(type.equals("custom")) {
                score = Score.correlationScore(reference.subList(0, limit), result);
            } else {
                score = Score.spearmanCoefficient(reference.subList(0, limit), result, spearman_n);
            }
            body.append(score + " & ");
        }
        //System.out.println(header);
        System.out.print(body);
    }

    public static Float customCorrelationScore(List<String> referenceResult, List<String> order) {
        ArrayList<Integer> reference = new ArrayList<>(order.size());
        ArrayList<Integer> result = new ArrayList<>(order.size());
        for (int i = 0; i < order.size(); i++) {
            reference.add(i + 1);
        }
        for (String test : referenceResult) {
            result.add(order.indexOf(test) + 1);
        }
        return correlationScore(reference, result);
    }

    public static Float correlationScore(List<Integer> reference, List<Integer> result) {
        Long aij = 0l;
        for (int i = 0; i < reference.size(); i++) {
            for (int j = 0; j < reference.size(); j++) {
                if (i != j) {
                    int a = a_ij(reference.get(i), reference.get(j), result.get(i), result.get(j));
                    a *= a;
                    aij += a;
                }
            }
        }
        if (aij == 0) return 0.0f;
        return ((Double) (aij / Math.sqrt(2 * aij))).floatValue();
    }

    private static int a_ij(Integer r_i, Integer r_j, Integer r_prime_i, Integer r_prime_j) {
        boolean swapped = false;
        int a = 0;
        if(r_i > r_j) {
            swapped = true;

            int temp = r_i;
            r_i = r_j;
            r_j = temp;

            temp = r_prime_i;
            r_prime_i = r_prime_j;
            r_prime_j = temp;
        }

        if (r_prime_i < r_prime_j
                && r_prime_i <= r_i
                && r_prime_j <= r_j) {
            a = 0;
        } else if (r_prime_i <= r_i) {
            a = (r_prime_j - r_j);
        } else if (r_prime_j <= r_j) {
            a = (r_prime_i - r_i);
        } else {
            a = ((r_prime_j - r_j) + (r_prime_i - r_i));
        }
        if(swapped) { a = -a; }
        return a;
    }

    public static Float spearmanCoefficient(List<Integer> referenceResult, List<Integer> order, Float spearman_n) {
        Long d_i = 0l;
        for(int i=0; i<referenceResult.size(); i++) {
            int d = d_i(referenceResult.get(i), order.get(i));
            d_i += d * d;
        }
        Float correlation = 1.0f - ((6 * d_i)/(spearman_n));
        return correlation;
    }

    private static int d_i(Integer reference, Integer order) {
        if(order > reference) {
            return reference - order;
        } else {
            return 0;
        }
    }

    public static Double getAPFDScore(List<String> order, List<String> failedTests) {
        int n = order.size();
        int m = failedTests.size();
        Integer summation = 0;
        for (String test : failedTests) {
            summation += (order.indexOf(test) + 1);
        }
        Double apfd = 1.0 - (summation.doubleValue() / (m * n)) + 1.0 / (2 * n);
        return apfd;
    }

    public static void main(String[] args) {
        String[] ref = new String[]{"1", "2", "3", "4", "5"};
        String[] test1 = new String[]{"1", "2", "3", "4", "5"};
        String[] test2 = new String[]{"5", "4", "3", "2", "1"};
        String[] test3 = new String[]{"5", "1", "2", "3", "4"};
        String[] test4 = new String[]{"2", "1", "4", "5", "3"};
        String[] test5 = new String[]{"1", "2", "3", "5", "4"};
        String[] test6 = new String[]{"1", "2", "5", "4", "3"};
        String[] test7 = new String[]{"1", "4", "3", "2", "5"};
        System.out.println(Arrays.asList(test1));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test1),1.0f,"spearman");
        System.out.println(Arrays.asList(test2));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test2),1.0f,"spearman");
        System.out.println(Arrays.asList(test3));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test3),1.0f,"spearman");
        System.out.println(Arrays.asList(test4));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test4),1.0f,"spearman");
        System.out.println(Arrays.asList(test5));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test5),1.0f,"spearman");
        System.out.println(Arrays.asList(test6));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test6),1.0f,"spearman");
        System.out.println(Arrays.asList(test7));
        Score.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test7), 1.0f, "spearman");

        String[] apfdTest = new String[]{"1"};

        List<String> failiures = Util.getLinesFromFile("./res/test-trace/xml-security/V3-seeded/failed-tests.txt");
        List<String> order = Util.getLinesFromFile("./res/test-trace/xml-security/V3-seeded/test-order.txt");

        System.out.println(Score.getAPFDScore(order, failiures));
    }
}
