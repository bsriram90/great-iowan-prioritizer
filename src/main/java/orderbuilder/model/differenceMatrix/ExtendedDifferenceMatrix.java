package orderbuilder.model.differenceMatrix;

import orderbuilder.util.Util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by Sriram on 06-04-2017.
 */
public class ExtendedDifferenceMatrix<T> extends DifferenceMatrix {
    Map<String, List<String>> trace;

    public ExtendedDifferenceMatrix(String fileName, Class type, String version) throws Exception {
        super(fileName, type, version);
        trace = new HashMap<>();
        String tracePath = "res/xml-security/" + version;
        File dir = new File(tracePath);
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile() && name.contains(".txt");
            }
        });
        int i=0;
        for(String file : files) {
            List<String> testTrace = Util.getLinesFromFile(tracePath + "/" + file);
            Iterator<String> itr = testTrace.iterator();
            while (itr.hasNext()) {
                String line = itr.next();
                if (line.contains("init")) {
                    itr.remove();
                }
            }
            trace.put(file, testTrace);
        }
    }

    public Map<String, List<String>> getTrace() {
        return trace;
    }

    public void setTrace(Map<String, List<String>> trace) {
        this.trace = trace;
    }

    public List<String> getTraceForTest(String test) {
        return trace.get(test);
    }

    public String getTestWithClosestTrace(List<String> trace, Collection<String> excludeTests) {
        Set<String> candidates = excludeTests(excludeTests);
        Iterator<String> itr = candidates.iterator();
        if(candidates.size() == 0) {
            return null;
        }
        float similarity = -Float.MIN_VALUE;
        String closestTest = null;
        while (itr.hasNext()) {
            String test = itr.next();
            float jacSim = Util.jaccardSimilarity(trace, getTraceForTest(test));
            if (jacSim >= similarity) {
                similarity = jacSim;
                closestTest = test;
            }
        }
        return closestTest;
    }


}
