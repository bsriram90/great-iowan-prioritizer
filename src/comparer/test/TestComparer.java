package comparer.test;

import model.DifferenceMatrix;

import java.util.LinkedList;

/**
 * Created by Sriram on 23-03-2017.
 */
public interface TestComparer {

    public String getNextTest(String test, DifferenceMatrix differenceMatrix, boolean debug);

    public String getNextTest(LinkedList<String> tests, DifferenceMatrix differenceMatrix, boolean debug);
}
