package orderbuilder.model;

import java.util.*;

/**
 * Created by Sriram on 23-03-2017.
 */
public abstract class TestChangeMatrix<T> {
    protected T[][] matrix;
    protected Integer size;
    protected Map<String, Integer> testIndex = new HashMap<>();
    protected Map<Integer, String> indexTest = new HashMap<>();
    protected Class<T> typeOfT;
    protected String version;

    public TestChangeMatrix(String fileName, Class<T> type) throws Exception {
        this(fileName, type, null);
    }

    public TestChangeMatrix(String fileName, Class<T> type, String version) throws Exception {
        if (!(type == Long.class || type == Double.class)) {
            throw new Exception("Invalid datatype. Only long and double supported");
        }
        typeOfT = type;
        this.version = version;
    }

    public T[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(T[][] matrix) {
        this.matrix = matrix;
    }

    public Long[][] getLongMatrix() {
        int r = matrix.length;
        int c = matrix[0].length;
        Long[][] longMatrix = new Long[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (matrix[i][j] != null) {
                    longMatrix[i][j] = (Long) matrix[i][j];
                } else {
                    longMatrix[i][j] = 0l;
                }
            }
        }
        return longMatrix;
    }

    public Set<String> excludeTests(Collection<String> excludeList) {
        //TODO change this to linked hash set
        Set<String> tests = new HashSet<>(testIndex.keySet());
        for (String test : excludeList) {
            tests.remove(test);
        }
        return tests;
    }

    public Integer getIndexByTest(String test) {
        return (Integer) testIndex.get(test);
    }

    public String getTestByIndex(Integer index) {
        return (String) indexTest.get(index);
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
