package orderbuilder.preprocess;

import orderbuilder.comparer.string.StringDifference;
import orderbuilder.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Sriram on 19-04-2017.
 */
public class BuildTestDifferenceMatrix {

    private String TRACE_FILES_DIRECTORY = null;
    private LinkedHashSet<String> files = new LinkedHashSet<>();
    private List<HashMap<String, String>> filePaths = new ArrayList<>();

    public BuildTestDifferenceMatrix(String path) {
        TRACE_FILES_DIRECTORY = path;
        if ((TRACE_FILES_DIRECTORY.charAt(TRACE_FILES_DIRECTORY.length() - 1) != '\\') &&
                (TRACE_FILES_DIRECTORY.charAt(TRACE_FILES_DIRECTORY.length() - 1) != '/')) {
            TRACE_FILES_DIRECTORY = TRACE_FILES_DIRECTORY + "/";
        }
    }

    public void buildMatrix() throws Exception {
        File root = new File(TRACE_FILES_DIRECTORY);
        String[] allDirectories = Util.getAllDirectories(root);
        if (allDirectories.length < 1) {
            throw (new Exception("No trace information"));
        }
        for (int i = 0; i < allDirectories.length; i++) {
            List<String> testsInVersion = new ArrayList<>();
            HashMap<String, String> paths = new HashMap<>();
            String dir = allDirectories[i];
            String versionPath = TRACE_FILES_DIRECTORY + dir;
            File currentDir = new File(versionPath);
            String[] testDirs = Util.getAllDirectories(currentDir);
            for (String testDir : testDirs) {
                String filePath = versionPath + "/" + testDir;
                File filesDir = new File(filePath);
                String[] fileNames = Util.getAllFiles(filesDir);
                for (String file : fileNames) {
                    testsInVersion.add(testDir + "-" + file);
                    files.add(testDir + "-" + file);
                    paths.put(testDir + "-" + file, filePath + "/" + file);
                }
            }

            Long[][] difference = new Long[testsInVersion.size()][testsInVersion.size()];
            for(int j=0; j<testsInVersion.size(); j++) {
                for (int k = 0; k < testsInVersion.size(); k++) {
                    difference[j][k] = new Long(0);
                }
            }
            for(int j=0; j<testsInVersion.size(); j++) {
                for(int k=j+1; k<testsInVersion.size(); k++) {
                    if(j != k) {
                        //System.out.println("difference[" + j + "][" + k + "]");
                        difference[j][k] = StringDifference.positionalDissimilarityScore(paths.get(testsInVersion.get(j)), paths.get(testsInVersion.get(k)), true);
                    }
                }
            }
            filePaths.add(paths);
            writeDifferenceMatrixToFile(versionPath, difference, testsInVersion);

        }
        StringBuilder header = new StringBuilder("");
        StringBuilder body = new StringBuilder("");
        Iterator<String> itr = files.iterator();
        while (itr.hasNext()) {
            String test = itr.next();
            header.append(test + ",");
            String path1 = filePaths.get(0).get(test);
            String path2 = filePaths.get(1).get(test);
            if (path1 == null || path1.trim().equals("") || path2 == null || path2.trim().equals("")) {
                body.append("NA,");
            } else {
                body.append(StringDifference.positionalDissimilarityScore(path1, path2, true) + ",");
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(TRACE_FILES_DIRECTORY + "changeMatrix-pos-w.csv", false));
            writer.append(header.toString().substring(0, header.length() - 1));
            writer.newLine();
            writer.append(body.toString().substring(0, body.length() - 1));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDifferenceMatrixToFile(String versionPath, Long[][] difference, List<String> testsInVersion) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(versionPath + "/" + "differenceMatrix-pos-w.csv", false));
            StringBuilder row = new StringBuilder("Files");
            for (String file : testsInVersion) {
                row.append("," + file);
            }
            writer.append(row.toString());
            writer.newLine();
            for (int k = 0; k < testsInVersion.size(); k++) {
                row = new StringBuilder(testsInVersion.get(k));
                for (int l = 0; l < testsInVersion.size(); l++) {
                    row.append("," + difference[k][l]);
                }
                writer.append(row.toString());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        BuildTestDifferenceMatrix matrixBuilder = new BuildTestDifferenceMatrix("res/test-trace/xml-security/");
        matrixBuilder.buildMatrix();
    }
}
