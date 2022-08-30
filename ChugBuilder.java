import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
public class ChugBuilder {

    // NOTE: FileLineIterator class is REMOVED from git repo due to fact that the code is similar to a homework assignment, which would violate my school's academic honor policy
    public static void main(String[] args) {
        getKidList("files/week 3 and grade.csv");
        ArrayList<String[]> signUpGenius = readCSV("files/SignUpGenius.csv");
        ArrayList<String> wholeCamp1 = readTxt("files/shalomCampers.txt");
        ArrayList<String> wholeCamp2 = readTxt("files/shalomCampers.txt");
        ArrayList<String> notInOGList1 = new ArrayList<>();
        ArrayList<String> notInOGList2 = new ArrayList<>();
        ArrayList<TreeMap<String, ArrayList<String>>> maps = initMaps(signUpGenius);
        int idx = 0;
        for (TreeMap<String, ArrayList<String>> content : maps) {
            if (idx == 0) {
                for (Map.Entry<String, ArrayList<String>> entry : content.entrySet()) {
                    ArrayList<String> value = entry.getValue();
                    for (String name : value) {
                        if (wholeCamp1.contains(name)) {
                            wholeCamp1.remove(name);
                        } else {
                            notInOGList1.add(name);
                        }
                    }
                }
            } else if (idx == 1) {
                for (Map.Entry<String, ArrayList<String>> entry : content.entrySet()) {
                    ArrayList<String> value = entry.getValue();
                    for (String name : value) {
                        if (wholeCamp2.contains(name)) {
                            wholeCamp2.remove(name);
                        } else {
                            notInOGList2.add(name);
                        }
                    }
                }
            }
            idx++;
        }
        boolean firstTime1 = true;
        for (Map.Entry<String, ArrayList<String>> entry : maps.get(0).entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            if (firstTime1) {
                writeCSV("files/output1.csv", false, value, key);
                firstTime1 = false;
            } else {
                writeCSV("files/output1.csv", true, value, key);
            }
        }

        boolean firstTime2 = true;
        for (Map.Entry<String, ArrayList<String>> entry : maps.get(1).entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            if (firstTime2) {
                writeCSV("files/output2.csv", false, value, key);
                firstTime2 = false;
            } else {
                writeCSV("files/output2.csv", true, value, key);
            }
        }

        writeCSV("files/output1.csv", true, wholeCamp1, "Campers Not Assigned");
        writeCSV("files/output1.csv", true, notInOGList1, "Campers Assigned But Not in Camp List");
        writeCSV("files/output2.csv", true, wholeCamp2, "Campers Not Assigned");
        writeCSV("files/output2.csv", true, notInOGList2, "Campers Assigned But Not in Camp List");
    }

    // Given the data from the CSV and 1st or 2nd chug slot, this function creates a treeMap where the key
    // is the chug and the value is the list of kids
    public static ArrayList<TreeMap<String, ArrayList<String>>> initMaps(ArrayList<String[]> textData) {
        TreeMap<String, ArrayList<String>> tgt1 = new TreeMap<>();
        TreeMap<String, ArrayList<String>> tgt2 = new TreeMap<>();
        ArrayList<String> dates = new ArrayList<>();
        for (String[] contents : textData) {
            // may need to change 9 and the index values depending on shemesh sheet
            if (contents.length >= 9) {
                if (!dates.contains(contents[0])) {
                    dates.add(contents[0]);
                }
            }
        }

        for (String[] contents : textData) {
            // may need to change 9 and the index values depending on shemesh sheet
            if (contents.length >= 9) {
                if (contents[0].equals(dates.get(0))) {
                    tgt1.put(contents[3], null);
                } else {
                    tgt2.put(contents[3], null);
                }
            }
        }
        for (Map.Entry<String, ArrayList<String>> entry : tgt1.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> tgtValue = new ArrayList<>();
            for (String[] contents : textData) {
                if (contents.length >= 9 && contents[3].equals(key) && contents[0].equals(dates.get(0))) {
                    if (contents[7] != "") {
                        tgtValue.add(contents[7]);
                    }
                }
            }
            tgt1.put(key, tgtValue);
        }

        for (Map.Entry<String, ArrayList<String>> entry : tgt2.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> tgtValue = new ArrayList<>();
            for (String[] contents : textData) {
                if (contents.length >= 9 && contents[3].equals(key) && contents[0].equals(dates.get(1))) {
                    if (contents[7] != "") {
                        tgtValue.add(contents[7]);
                    }
                }
            }
            tgt2.put(key, tgtValue);
        }
        ArrayList<TreeMap<String, ArrayList<String>>> tgt = new ArrayList<>();
        tgt.add(tgt1);
        tgt.add(tgt2);
        return tgt;
    }

    // Given a path of the CSV file, this will read the CSV
    // an arraylist, where each string array is the kid and elective
    public static ArrayList<String[]> readCSV(String path) {
        BufferedReader br;
        File file = Paths.get(path).toFile();
        ArrayList<String[]> tgt = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);
            FileLineIterator iterator = new FileLineIterator(br);
            iterator.next();
            while (iterator.hasNext()) {
                String tgtStringArr[] = iterator.next().split(",");
                
                for (int i = 0; i < tgtStringArr.length; i++) {
                    tgtStringArr[i] = tgtStringArr[i].replaceAll("\"", "");
                    tgtStringArr[i] = tgtStringArr[i].replaceAll("\"", "");
                }
                tgt.add(tgtStringArr);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tgt;
        
    }

    // Given a path of the CSV file, this will create text files of
    // Ruach and Shalom
    public static void getKidList(String path) {
        BufferedReader br;
        File file = Paths.get(path).toFile();
        File shalomFile = Paths.get("files/shalomCampers.txt").toFile();
        File ruachFile = Paths.get("files/ruachCampers.txt").toFile();
        BufferedWriter bwShalom;
        BufferedWriter bwRuach;
        try {
            FileWriter fwShalom = new FileWriter(shalomFile, false);
            bwShalom = new BufferedWriter(fwShalom);
            FileWriter fwRuach = new FileWriter(ruachFile, false);
            bwRuach = new BufferedWriter(fwRuach);

            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);
            FileLineIterator iterator = new FileLineIterator(br);
            iterator.next();
            while (iterator.hasNext()) {
                String tgtStringArr[] = iterator.next().split(",");
                
                for (int i = 0; i < tgtStringArr.length; i++) {
                    tgtStringArr[i] = tgtStringArr[i].replaceAll("\"", "");
                    tgtStringArr[i] = tgtStringArr[i].replaceAll("\"", "");
                }
                if (tgtStringArr.length >= 3) {
                    if (tgtStringArr[2].equals("2nd") || tgtStringArr[2].equals("3rd") || tgtStringArr[2].equals("4th") || tgtStringArr[2].equals("5th")) {
                        String stringToWrite = tgtStringArr[1] + " " + tgtStringArr[0];

                        // save to Shalom
                        bwShalom.write(stringToWrite, 0, stringToWrite.length());
                        bwShalom.newLine();
                    } else if (tgtStringArr[2].equals("6th") || tgtStringArr[2].equals("7th") || tgtStringArr[2].equals("8th")) {
                        String stringToWrite = tgtStringArr[1] + " " + tgtStringArr[0];

                        // save to Ruach
                        bwRuach.write(stringToWrite, 0, stringToWrite.length());
                        bwRuach.newLine();
                    }
                }
                
            }
            br.close();
            bwShalom.close();
            bwRuach.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Given the path of a text file, this creates an ArrayList of campers
    public static ArrayList<String> readTxt(String path) {
        BufferedReader br;
        File file = Paths.get(path).toFile();
        ArrayList<String> tgt = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);
            FileLineIterator iterator = new FileLineIterator(br);
            while (iterator.hasNext()) {
                tgt.add(iterator.next());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tgt;
    }

    public static void writeCSV(String path, boolean append, ArrayList<String> data, String header) {
        // File file = Paths.get(path).toFile();
        // BufferedWriter bw;
        // BufferedReader br;
        // try {
        //     FileReader fr = new FileReader(file);
        //     br = new BufferedReader(fr);
        //     FileWriter fw = new FileWriter(file, append);
        //     bw = new BufferedWriter(fw);

        //     if (!append) {
        //         bw.write(header, 0, header.length());
        //         bw.newLine();
        //         for (String string : data) {
        //             bw.write(string, 0, string.length());
        //             bw.newLine();
        //         }
        //         bw.newLine();
        //         bw.newLine();
        //         bw.close();
        //     } else {
        //         FileLineIterator iterator = new FileLineIterator(br);
        //         int count = 0;
        //         while (iterator.hasNext()) {
        //             String stringArr[] = iterator.next().split(",");
        //             String tgtStringArr[] = new String[stringArr.length + 1];
        //             for (int i = 0; i < tgtStringArr.length; i++) {
        //                 if (count == 0 && i == tgtStringArr.length - 1) {
        //                     tgtStringArr[i] = header;
        //                 } else if (i == tgtStringArr.length - 1) {
        //                     tgtStringArr[i] = data.remove(0);
        //                 } else {
        //                     tgtStringArr[i] = stringArr[i];
        //                 }
        //             }
        //             for (int i = 0; i < tgtStringArr.length; i++) {
        //                 bw.write(tgtStringArr[i] + ",", 0, tgtStringArr[i].length());
        //             }
        //             bw.newLine();
        //             count++;
        //          }
        //          br.close();
        //          bw.close();
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        File file = Paths.get(path).toFile();
        BufferedWriter bw;
        try {
            FileWriter fw = new FileWriter(file, append);
            bw = new BufferedWriter(fw);
            bw.write(header, 0, header.length());
            bw.newLine();
            for (String string : data) {
                bw.write(string, 0, string.length());
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}