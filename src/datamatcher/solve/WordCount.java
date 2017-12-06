package datamatcher.solve;

import util.files.CSVTable;
import util.logging.FileOutput;
import util.logging.LF;
import util.tool.TextUtil;

import java.io.File;
import java.util.*;

public class WordCount {
//    private Input input = new Input(
//        "D:\\YSData\\20171203\\HK2\\", "SDC_HK.csv", "ISSUER"
//    );
//    private LF logF = new LF(input.folder);
//
//    private FileOutput output = new FileOutput(new File(input.folder, "SDC_name.csv"));

    private Input input;
    private LF logF;
    private FileOutput output;

    public static final String OUTPUT_COLUMN_NAME = "name";
    public static final String OUTPUT_COLUMN_N = "n";

    public static void solve() {
        solve("D:\\YSData\\20171203\\HK2\\", "SDC_HK.csv", "ISSUER", "SDC_name.csv");
    }

    public static void solve(String folder, String csvNameToCount, String column, String csvOutputName) {
        WordCount wc = new WordCount();
        wc.input = new Input(folder, csvNameToCount, column);
        wc.logF = new LF(folder);
        wc.output = new FileOutput(new File(folder, csvOutputName));
        wc.solve(null);
    }

    private void solve(String[] args) {
        CSVTable table = CSVTable.readCSVFile_logPerformance(input.csvFile, logF);

        Map<String, Integer> words = new HashMap<>();
        List<Map<String, String>> data = table.data;
        for (Map<String, String> m : data) {
            String row = m.get(input.column);
            String noParan = row.replaceAll("\\x28", " "); // 0x28 = '('
            noParan = noParan.replaceAll("\\x29", " "); // 0x29 = ')'
            String[] lines = noParan.split(" ");

            for (String line : lines) {
                String nocase = line.toLowerCase();
                if (!TextUtil.isEmpty(nocase)) {
                    if (words.containsKey(nocase)) {
                        words.put(nocase, words.get(nocase) + 1);
                    } else {
                        words.put(nocase, 1);
                    }
                }
            }
        }
        List<Map.Entry<String, Integer>> allWords = new ArrayList<>();
        for (Map.Entry<String, Integer> e : words.entrySet()) {
            allWords.add(e);
        }
        allWords.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int v1 = o1.getValue();
                int v2 = o2.getValue();
                if (v1 == v2) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return Integer.compare(v2, v1);
                }
            }
        });

        // Generating the output csv file
        output.delete();
        output.open();
        output.writeln("%s,%s", OUTPUT_COLUMN_NAME, OUTPUT_COLUMN_N);
        for (Map.Entry<String, Integer> word : allWords) {
            output.writeln("%s,%s", word.getKey(), word.getValue());
        }
        output.close();
    }

    private static class Input {
        private String filename;
        private String folder;
        private String csvFile;
        private String column;
        Input(String csvFolder, String csvFileName, String columnName) {
            folder = csvFolder;
            filename = csvFileName;
            csvFile = folder + File.separator + filename;
            column = columnName.toLowerCase();
        }
    }
}
