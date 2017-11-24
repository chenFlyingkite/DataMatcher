package datamatcher.solve.lcsjoin;

import datamatcher.algorithm.LCSString;
import datamatcher.solve.Basics;
import datamatcher.solve.CSVTable;
import datamatcher.solve.OutputPack;
import datamatcher.util.TicTac;
import datamatcher.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LCSJoin extends Basics {
    private static final String COMMA = ",";
    private static final String LINE = "line";

    private static LCSInput input = LCSInput.get();
    private static final String outPath = input.folderPath + input.outFile;

    private static final String sdcPath = input.folderPath + input.sdcFile;
    private static final String dsPath = input.folderPath + input.dsFile;
    private static final String SDC_COLUMN = input.SDC_COLUMN;
    private static final String DS_COLUMN = input.DS_COLUMN;

    public void solve(String[] args) {
        CSVTable sdcTable = readTable(sdcPath);

        CSVTable dsTable = readTable(dsPath);

        List<Map<String, String>> sdcMaps = sdcTable.data;
        List<Map<String, String>> dsMaps = dsTable.data;

        List<Map<String, String>> joinMaps = new ArrayList<>();
        // The mapping: i -> mapping[i] = ds column #i
        int[] mappings = new int[sdcMaps.size()];
        // The map key: i -> keys[i]
        String[] keys = new String[sdcMaps.size()];
        for (int i = 0; i < sdcMaps.size(); i++) {
            Map<String, String> map = sdcMaps.get(i);
            String src = normalizeSDC(map.get(SDC_COLUMN));
            String srcLcs = "";
            int max = 0;
            mappings[i] = -1;
            log("  find #%s for /%s/", i, src);
            for (int j = 0; j < dsMaps.size(); j++) {
                Map<String, String> dsMap = dsMaps.get(j);
                String dst = normalizeDS(dsMap.get(DS_COLUMN));
                String s = "";
                if (dst.length() >= max) {
                    //TicTac.tic();
                    s = LCSString.LCSAlgorithm(src, dst);
                    //TicTac.tac("LCS -> %s", s);
                }

                if (s.length() > max) {
                    //log("  found: max = %s, %s = %s -> %s", max, s, src, dst);
                    srcLcs = s;
                    max = s.length();
                    mappings[i] = j;
                    keys[i] = s;
                }
            }
            log("got at #%s as /%s/", mappings[i], srcLcs);
        }
        log("End with %s", Arrays.toString(mappings));
        openLogFile();
        for (int i = 0; i < mappings.length; i++) {
            logFile("%s\n      -> %s\n  => %s\n"
                    , sdcMaps.get(i).get(LINE), dsMaps.get(mappings[i]).get(LINE)
                    , keys[i]
            );
        }
        closeLogFile();

        log("---------------");

        OutputPack out = new OutputPack(outPath);
        out.open(true);
        out.writeln("%s,LCS Key,LCS,LCS Value,%s", sdcTable.header, dsTable.header);
        for (int i = 0; i < mappings.length; i++) {
            int key = mappings[i];
            out.writeln("%s,%s,%s,%s,%s"
                    , sdcMaps.get(i).get(LINE)
                    , sdcMaps.get(i).get(SDC_COLUMN)
                    , keys[i]
                    , dsMaps.get(key).get(DS_COLUMN)
                    , dsMaps.get(key).get(LINE)
            );
        }
        out.close();
        log("CSV File created: %s", outPath);
    }

    private String normalizeSDC(String from) {
        String s = normalize(from, input.sdcErase.keywords);
        s = s.substring(0, Math.min(s.length(), 10));
        return s;
    }

    private String normalizeDS(String from) {
        String s = normalize(from, input.dsErase.keywords);
        s = s.substring(0, Math.min(s.length(), 10));
        return s;
    }

    private String normalize(String from, String[] list) {
        String result = from.toLowerCase();

        if (list != null) {
            for (String x : list) {
                result = result.replaceAll(x, "");
            }
        }

        return result;
    }

    private static CSVTable readTable(String path) {
        TicTac.tic();
        CSVTable table = readFile(path);
        TicTac.tac("File OK, %s rows read <- %s", table.data.size(), path);
        return table;
    }

    private static CSVTable readFile(String path) {
        CSVTable table = new CSVTable();
        List<Map<String, String>> data = new ArrayList<>();
        if (missingFile(path)) {
            log("File not found: %s", path);
            return table;
        }

        File f = new File(path);
        Scanner fis = null;
        try {
            fis = new Scanner(f);
            String line;
            String[] lines;

            String[] columns = null;
            int col = 0;
            int ln = 0;
            // Read header as columns
            if (fis.hasNextLine()) {
                ln++;
                line = fis.nextLine();
                lines = line.split(COMMA);
                if (lines.length == 0) {
                    log("Missing header columns, omit file");
                    return table;
                }
                columns = lines;
                col = columns.length;
                table.header = line;
            }

            while (fis.hasNextLine()) {
                ln++;
                line = fis.nextLine();
                lines = line.split(COMMA);
                if (lines.length > 0) {
                    Map<String, String> m = new HashMap<>();
                    m.put(LINE, line);
                    if (lines.length != col) {
                        log("Bad data at line #%s, missing columns\n  %s columns expected:\n  %s", ln, col, line);
                        table.data = data;
                        return table;
                    }

                    // Put the parsed columns
                    for (int i = 0; i < col; i++) {
                        m.put(columns[i], lines[i]);
                    }
                    data.add(m);
                }
            }
            table.data = data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            Util.closeIt(fis);
        }

        return table;
    }
}
