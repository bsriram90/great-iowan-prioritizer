package orderbuilder.evaluator;

import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sriram on 13-05-2017.
 */
public class CorrelationScore {

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

    public static void printCorrelationScoreByBands(List<String> referenceResult, List<String> order, Float band) {
        ArrayList<String> orderArrayList = new ArrayList<>(order);
        ArrayList<Integer> reference = new ArrayList<>(order.size());
        int bandSize = Math.round(referenceResult.size() * band);
        for (int i = 0; i < referenceResult.size(); i++) {
            reference.add(i + 1);
        }
        StringBuilder header = new StringBuilder("");
        StringBuilder body = new StringBuilder("");
        int n = (int) Math.ceil((float) referenceResult.size() / bandSize);
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> result = new ArrayList<>(order.size());
            int limit = (i * bandSize + bandSize > referenceResult.size()) ? referenceResult.size() : i * bandSize + bandSize;
            List<String> referenceSubset = referenceResult.subList(0, limit);
            for (String test : referenceSubset) {
                result.add(orderArrayList.indexOf(test) + 1);
            }
            header.append("Band-" + i + "(Size=" + result.size() + "),");
            body.append(CorrelationScore.correlationScore(reference.subList(0, limit), result) + ",");
        }
        System.out.println(header);
        System.out.println(body);
    }

    public static Float customCorrelationScore(List<String> referenceResult, List<String> order) {
        ArrayList<Integer> reference = new ArrayList<>(order.size());
        ArrayList<Integer> result = new ArrayList<>(order.size());
        for (int i = 0; i < order.size(); i++) {
            reference.add(i + 1);
        }
        for (String test : order) {
            result.add(referenceResult.indexOf(test) + 1);
        }
        return correlationScore(reference, result);
    }

    public static Float correlationScore(List<Integer> reference, List<Integer> result) {
        Long numerator = 0l;
        Long denominator = 0l;
        for (int i = 0; i < reference.size(); i++) {
            for (int j = 0; j < reference.size(); j++) {
                if (i != j) {
                    int a = a_ij(reference.get(i), reference.get(j), result.get(i), result.get(j));
                    numerator += a;
                    denominator += a * a;
                }
            }
        }
        if (numerator == 0) return 0.0f;
        return ((Double) (numerator / Math.sqrt(denominator))).floatValue();
    }

    private static int a_ij(int r_i, int r_j, int r_prime_i, int r_prime_j) {
        if (r_i <= r_prime_i && r_j <= r_prime_j) {
            return 0;
        } else if (r_i <= r_prime_i) {
            return (r_j - r_prime_j);
        } else if (r_j <= r_prime_j) {
            return (r_i - r_prime_i);
        } else {
            return ((r_j - r_prime_j) + (r_i - r_prime_i));
        }
    }

    public static void main(String[] args) {
        String[] ref = new String[]{"1", "2", "3", "4", "5"};
        String[] test1 = new String[]{"1", "2", "3", "4", "5"};
        String[] test2 = new String[]{"5", "4", "3", "2", "1"};
        String[] test3 = new String[]{"5", "1", "2", "3", "4"};
        String[] test4 = new String[]{"2", "1", "4", "5", "3"};
        String[] test5 = new String[]{"1", "2", "3", "5", "4"};
        /*System.out.println(Arrays.asList(test1) + " - " + CorrelationScore.customCorrelationScore(Arrays.asList(ref), Arrays.asList(test1)));
        System.out.println(Arrays.asList(test2) + " - " + CorrelationScore.customCorrelationScore(Arrays.asList(ref), Arrays.asList(test2)));
        System.out.println(Arrays.asList(test3) + " - " + CorrelationScore.customCorrelationScore(Arrays.asList(ref), Arrays.asList(test3)));
        System.out.println(Arrays.asList(test4) + " - " + CorrelationScore.customCorrelationScore(Arrays.asList(ref), Arrays.asList(test4)));
        System.out.println(Arrays.asList(test5) + " - " + CorrelationScore.customCorrelationScore(Arrays.asList(ref), Arrays.asList(test5)));*/
        CorrelationScore.printCorrelationScoreByBands(Arrays.asList(ref), Arrays.asList(test2), 0.20f);
    }
}
