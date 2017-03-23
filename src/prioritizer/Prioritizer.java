package prioritizer;

import model.ChangeMatrix;
import model.DifferenceMatrix;
import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Sriram on 23-03-2017.
 */
public class Prioritizer {

    public static LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria, boolean debug) throws Exception {
        Object[] results = change.getOrderAndDifferingTest(null,1000l);
        String differingTest = (String) results[0];
        LinkedList<String> order = (LinkedList<String>) results[1];
        if(differingTest == null) return order;
        LinkedList<String> newOrder = new LinkedList<>();

        return order;
    }

    public static void main(String[] args) throws Exception{
        DifferenceMatrix<Long> diffMatrix = new DifferenceMatrix<>("./res/v2/differenceMatrix-v2.csv", Long.class);
        ChangeMatrix<Long> changeMatrix = new ChangeMatrix<>("./res/v2-v3.csv", Long.class);
        LinkedList<String> order = Prioritizer.getExecutionOrder(changeMatrix,diffMatrix, Util.getDefaultPrioritizerCriteria(),false);
        for(String test : order) {
            System.out.println(test);
        }
    }


}
