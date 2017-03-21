package model;

import util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sriram on 21-03-2017.
 */
public class DifferenceMatrix<T> {
    private T[][] matrix;
    private int size;
    private Map<String, Integer> testIndex;

    public DifferenceMatrix(String fileName) {
        testIndex = new HashMap<>();
        List<String> lines = Util.getLinesFromFile(fileName);
        String[] headers = lines.get(0).split(",");
        this.size = headers.length - 1;
        matrix = (T[][]) new Object[size][size];
        for (int i = 0; i < size; i++) {
            
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
        }
    }


}
