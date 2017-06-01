package orderbuilder.evaluator;

import orderbuilder.model.ChangeMatrix;
import orderbuilder.model.differenceMatrix.TestTraceDifferenceMatrix;
import orderbuilder.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Sriram on 28-05-2017.
 */
public class Validator {

    HashMap<String, FileChange> changes = new HashMap<>();

    public Validator(String changeTypeFile, String changeValueFile) {
        List<String> lines = Util.getLinesFromFile(changeTypeFile);
        for (String line : lines) {
            String[] val = line.split(",");
            FileChange.Change_type type = null;
            String name = val[1].substring(val[1].lastIndexOf("/") + 1);
            if (val[0].equals("A")) {
                type = FileChange.Change_type.add;
            } else if (val[0].equals("D")) {
                type = FileChange.Change_type.delete;
            } else if (val[0].equals("M")) {
                type = FileChange.Change_type.modify;
            } else {
                type = FileChange.Change_type.rename;
                name = val[2].substring(val[2].lastIndexOf("/") + 1);
            }
            FileChange change = new FileChange(name, null, type);
            changes.put(name, change);
        }
        lines = Util.getLinesFromFile(changeValueFile);
        for (String line : lines) {
            String[] val = line.split(",");
            String name = val[0].substring(val[0].lastIndexOf("/") + 1);
            FileChange change = changes.get(name);
            if (change == null) {
                System.out.println();
            }
            change.setChange(Long.parseLong(val[1]));
        }
    }

    public void printBandChangeSummary(List<String> order, TestTraceDifferenceMatrix differenceMatrix, ChangeMatrix changeMatrix) {
        int i = 1;
        Set<String> fileName = changes.keySet();
        for (String test : order) {

            System.out.println(i++ + "," + test + "," + changeMatrix.getChangeByTest(test) + "," + differenceMatrix.getTestCaseByTest(test));
        }
    }

    public static void main(String[] args) {
        String change_file = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\change-status.txt";
        String change_value = "C:\\Users\\Sriram\\Desktop\\RA\\XML Sec compare\\diff-changes.txt";
        Validator validator = new Validator(change_file, change_value);
    }
}
