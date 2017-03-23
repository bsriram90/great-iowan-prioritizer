package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 23-03-2017.
 */
public abstract class TestChangeMatrix<T> {
    protected T[][] matrix;
    protected Integer size;
    protected Map<String, Integer> testIndex = new HashMap<>();
    protected Map<Integer, String> indexTest = new HashMap<>();
    protected Class<T> typeOfT;

    public TestChangeMatrix() {

    }

    public TestChangeMatrix(String fileName, Class<T> type) throws Exception{
        if (!(type == Long.class || type == Double.class)) {
            throw new Exception("Invalid datatype. Only long and double supported");
        }
        typeOfT = type;
    }
}
