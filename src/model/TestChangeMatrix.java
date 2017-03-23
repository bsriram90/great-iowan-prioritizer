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

    public TestChangeMatrix(String fileName, Class<T> type) throws Exception{
        if (!(type == Long.class || type == Double.class)) {
            throw new Exception("Invalid datatype. Only long and double supported");
        }
        typeOfT = type;
    }

    public T[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(T[][] matrix) {
        this.matrix = matrix;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Map<String, Integer> getTestIndex() {
        return testIndex;
    }

    public void setTestIndex(Map<String, Integer> testIndex) {
        this.testIndex = testIndex;
    }

    public Map<Integer, String> getIndexTest() {
        return indexTest;
    }

    public void setIndexTest(Map<Integer, String> indexTest) {
        this.indexTest = indexTest;
    }

    public Class<T> getTypeOfT() {
        return typeOfT;
    }

    public void setTypeOfT(Class<T> typeOfT) {
        this.typeOfT = typeOfT;
    }
}
