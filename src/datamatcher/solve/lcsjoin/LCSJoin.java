package datamatcher.solve.lcsjoin;

import datamatcher.algorithm.LCSString;
import datamatcher.solve.Basics;
import datamatcher.solve.CSVTable;
import datamatcher.solve.OutputPack;
import datamatcher.util.TicTac;
import datamatcher.util.TicTac2;
import datamatcher.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LCSJoin extends Basics {
    private static final String COMMA = ",";
    private static final String LINE = "line";

    private static LCSInput input = LCSInput.get();
    private static final String outPath = input.folderPath + input.outputPath;
    private static final String nomatch = input.folderPath + input.mismatchPath;

    private static final String sdcPath = input.folderPath + input.sdc.csvName;
    private static final String dsPath = input.folderPath + input.ds.csvName;
    private static final String SDC_COLUMN = input.sdc.keyColumn.toLowerCase();
    private static final String DS_COLUMN = input.ds.keyColumn.toLowerCase();

    // performance profiling
    private TicTac2 tt = new TicTac2();

    public void solve(String[] args) {
        tt.reset();
        CSVTable sdcTable = readTable(sdcPath);

        CSVTable dsTable = readTable(dsPath);

        List<Map<String, String>> sdcMaps = sdcTable.data;
        List<Map<String, String>> dsMaps = dsTable.data;

        List<Map<String, String>> joinMaps = new ArrayList<>();
        // The mapping: i -> mapping[i] = ds column #i
        int[] mappings = new int[sdcMaps.size()];
        // The map key: i -> keys[i]
        String[] keys = new String[sdcMaps.size()];
        // LCS Index : i -> (sdc's index, ds's index)
        int[][] lcsIndices = new int[sdcMaps.size()][2];

        int perfect = 0;
        boolean logDetail = true;
        for (int i = 0; i < sdcMaps.size(); i++) {
            Map<String, String> map = sdcMaps.get(i);
            String src = normalizeSDC(map.get(SDC_COLUMN));
            String srcLcs = "";
            String dstGood = "";
            int max = 0;
            mappings[i] = -1;
            if (logDetail) {
                log("  find #%s for /%s/", i, src);
            }
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
                    dstGood = dst;
                }
            }

            lcsIndices[i][0] = src.indexOf(srcLcs);
            lcsIndices[i][1] = dstGood.indexOf(srcLcs);
            if (lcsIndices[i][0] >= 0 && lcsIndices[i][1] >= 0) {
                perfect++;
            }
            if (logDetail) {
                log("got at #%s as /%s/", mappings[i], srcLcs);
            }
        }
        log("End with %s", Arrays.toString(mappings));
        log("Perfect = %s -> %.3f", perfect, 1F * perfect / sdcMaps.size());

        tt.tic();
        openLogFile();
        logFile("");
        for (int i = 0; i < mappings.length; i++) {
            logFile("  sdc >> %s\n   ds -> %s\n  lcs => %s\n  : %s & %s\n"
                    , sdcMaps.get(i).get(LINE), dsMaps.get(mappings[i]).get(LINE)
                    , keys[i], lcsIndices[i][0], lcsIndices[i][1]
            );
        }
        closeLogFile();
        tt.tac("Writelog file OK");

        log("---------------");

        tt.tic();
        OutputPack out = new OutputPack(outPath);
        OutputPack mis = new OutputPack(nomatch);
        OutputPack pack;

        out.delete();
        out.open(true);
        out.writeln("%s,LCS Key,LCS,LCS Value,%s", sdcTable.header, dsTable.header);

        mis.delete();
        mis.open(true);
        mis.writeln("%s,LCS Key,LCS,LCS Value,%s", sdcTable.header, dsTable.header);
        for (int i = 0; i < mappings.length; i++) {
            int key = mappings[i];
            if (lcsIndices[i][0] >= 0 && lcsIndices[i][1] >= 0) {
                pack = out;
            } else {
                pack = mis;
            }
            pack.writeln("%s,%s,%s,%s,%s"
                    , sdcMaps.get(i).get(LINE)
                    , sdcMaps.get(i).get(SDC_COLUMN)
                    , keys[i]
                    , dsMaps.get(key).get(DS_COLUMN)
                    , dsMaps.get(key).get(LINE)
            );
        }
        out.close();
        tt.tac("Output File OK");
        log("CSV File created: %s", outPath);
    }

    private String normalizeSDC(String from) {
        int max = 15;
        String s = normalize(from, input.sdc.keywords);
        s = s.substring(0, Math.min(s.length(), max));
        return s;
    }

    private String normalizeDS(String from) {
        int max = 15;
        String s = normalize(from, input.ds.keywords);
        s = s.substring(0, Math.min(s.length(), max));
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

    private CSVTable readTable(String path) {
        saveLog = true;
        openLogFile();
        long tic = System.currentTimeMillis();
        TicTac.tic();

        CSVTable table = readFile(path);

        TicTac.tac("File OK, %s rows read <- %s", table.data.size(), path);
        long tac = System.currentTimeMillis();
        logFile("[%s] : File OK, %s rows read <- %s", tac - tic, table.data.size(), path);
        closeLogFile();
        saveLog = false;
        return table;
    }

    private CSVTable readFile(String path) {
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
                        m.put(columns[i].toLowerCase(), lines[i]);
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
