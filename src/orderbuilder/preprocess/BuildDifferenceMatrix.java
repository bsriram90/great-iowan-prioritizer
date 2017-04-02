package orderbuilder.preprocess;

import orderbuilder.comparer.string.StringDifference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sriram on 09-03-2017.
 */
public class BuildDifferenceMatrix {
    private static String TRACE_FILES_DIRECTORY = null;
    private List<String> files = null;

    public BuildDifferenceMatrix(String path) throws Exception {
        TRACE_FILES_DIRECTORY = path;
        files = new ArrayList<String>();
        File root = new File(TRACE_FILES_DIRECTORY);
        String[] allDirectories = root.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        if(allDirectories.length < 1) {
            throw (new Exception("No trace information"));
        }
        for(int i=0; i<allDirectories.length; i++) {
            String directory = allDirectories[i];
            File dir = new File(TRACE_FILES_DIRECTORY + directory);
            String[] files = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isFile() && name.contains(".txt");
                }
            });
            Long[][] differenceMatrix = new Long[files.length][files.length];
            for(int k = 0; k<files.length ; k++) {
                for (int l = 0; l < files.length; l++) {
                    differenceMatrix[k][l] = new Long(0);
                }
            }
            for(int k = 0; k<files.length ; k++) {
                for(int l = k+1; l<files.length ; l++) {
                    if (k != l) {
                        differenceMatrix[k][l] = StringDifference.basicLineDifference(TRACE_FILES_DIRECTORY + directory + "\\" + files[k],TRACE_FILES_DIRECTORY + directory + "\\" + files[l]);
                    }
                }
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(TRACE_FILES_DIRECTORY + directory + "\\" + "differenceMatrix.csv", false));
                StringBuilder row = new StringBuilder("Files,");
                for(String file : files) {
                    row.append(file + ",");
                }
                writer.append(row.toString());
                writer.newLine();
                for(int k = 0; k<files.length ; k++) {
                    row = new StringBuilder(files[k] + ",");
                    for (int l = 0; l < files.length; l++) {
                        row.append(differenceMatrix[k][l] + ",");
                    }
                    writer.append(row.toString());
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
            Long[] versionDifference = new Long[files.length];
            for(int k = 0; k<files.length ; k++) {
                versionDifference[k] = new Long(-1);
            }
            for (int j = i + 1; j < allDirectories.length; j++) {
                for(int k = 0; k<files.length ; k++) {
                    String outerFile = files[k];
                    String[] filesToCompare = dir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File current, String name) {
                            return new File(current, name).isFile() && name.contains(".txt");
                        }
                    });
                    for (String file : filesToCompare) {
                        if (outerFile.equals(file)) {
                            String file1 = TRACE_FILES_DIRECTORY + allDirectories[i] + "\\" + files[k];
                            String file2 = TRACE_FILES_DIRECTORY + allDirectories[j] + "\\"  + file;
                            versionDifference[k] = StringDifference.basicLineDifference(file1, file2);
                        }
                    }
                }
                try {
                    String fileName = allDirectories[i] + "-" + allDirectories[j] + ".csv";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(TRACE_FILES_DIRECTORY + "\\" + fileName, false));
                    StringBuilder row = new StringBuilder("");
                    for(String file : files) {
                        row.append(file + ",");
                    }
                    writer.write(row.toString());
                    writer.newLine();
                    for(int f=0; f<files.length; f++) {
                        writer.append(versionDifference[f] + ",");
                    }
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        BuildDifferenceMatrix matrix = new BuildDifferenceMatrix("C:\\Users\\Sriram\\Desktop\\RA\\call_traces\\");
    }
}
