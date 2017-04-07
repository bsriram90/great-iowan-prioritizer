package orderbuilder.comparer.test;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.DifferenceMatrix;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sriram on 23-03-2017.
 */
public abstract class TestComparer {

    public LinkedList<String> getExecutionOrder(ChangeMatrix change, List<DifferenceMatrix> diff, HashMap<String, Object> criteria) throws Exception {
        return null;
    }
}
