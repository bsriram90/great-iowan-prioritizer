package comparer.test;

import model.ChangeMatrix;
import model.DifferenceMatrix;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Sriram on 23-03-2017.
 */
public abstract class TestComparer {

    public LinkedList<String> getExecutionOrder(ChangeMatrix change, DifferenceMatrix diff, HashMap<String, Object> criteria) throws Exception {
        return null;
    }
}