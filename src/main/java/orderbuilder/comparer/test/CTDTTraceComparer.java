package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;
import orderbuilder.model.differenceMatrix.ExtendedDifferenceMatrix;
import orderbuilder.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Sriram on 06-04-2017.
 */
public class CTDTTraceComparer extends TestComparer {

    Logger logger = Logger.getLogger(CTDTTraceComparer.class.getName());

    @Override
    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff1, HashMap<String, Object> criteria) throws Exception {
        DifferenceMatrix t1 = diff1.get(0);
        DifferenceMatrix t2 = diff1.get(1);
        boolean debug = (boolean) criteria.get(Variables.DEBUG);
        ExtendedDifferenceMatrix current;
        ExtendedDifferenceMatrix prev;
        if(t1 instanceof ExtendedDifferenceMatrix) {
            current = (ExtendedDifferenceMatrix) t2;
            prev = (ExtendedDifferenceMatrix) t1;
        } else {
            throw new Exception("TraceComparer required ExtendedDifferenceMatrix");
        }
        Object[] results = TestComparerUtil.getOrderUntilDifferingTest(logger,change,null,(Long)criteria.get(Variables.THRESHOLD_1),debug);
        String differingTest = (String) results[0];
        LinkedList<String> order = (LinkedList<String>) results[1];
        // if nothing changed enough, return here
        if (differingTest == null) {
            if (debug) {
                logger.info("No test changed significantly. Order is same as previous run.");
            }
            return order;
        }
        order.add(differingTest);
        List<String> differingTrace = getNewTraceStartingFromDifference(prev.getTraceForTest(differingTest), current.getTraceForTest(differingTest));
        while (order.size() < change.getSize()) {
            String nextTest = prev.getTestWithClosestTrace(differingTrace, order);
            order.add(nextTest);
        }
        return order;
    }

    private List<String> getNewTraceStartingFromDifference(List<String> prevTrace, List<String> currTrace) {
        int maxLength = (prevTrace.size() > currTrace.size()) ? prevTrace.size() : currTrace.size();
        int differenceIndex = 0;
        for(int i = 0; i < maxLength; i++) {
            if(!currTrace.get(i).equals(prevTrace.get(i))) {
                differenceIndex = i;
                break;
            }
        }
        List<String> difference = new ArrayList<>();
        difference.addAll(currTrace.subList(0, differenceIndex));
        List<String> temp1 = new ArrayList<>(prevTrace);
        temp1.removeAll(currTrace);
        List<String> temp2 = new ArrayList<>(currTrace);
        temp2.removeAll(prevTrace);
        temp1.addAll(temp2);
        return temp1;
    }



}
