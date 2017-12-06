package datamatcher.solve.lcsjoin;

import datamatcher.algorithm.LCSString;
import datamatcher.algorithm.LevenshteinDistance;
import util.files.CSVTable;
import util.logging.FileOutput;
import util.logging.L;
import util.logging.LF;
import util.tool.TicTac2;
import util.tool.TicTacLF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LCSJoin {

    private static LCSInput input = LCSInput.get();
    private static final String outPath = input.folderPath + input.outputPath;
    private static final String similar = input.folderPath + input.similarPath;
    private static final String nomatch = input.folderPath + input.mismatchPath;

    private static final String sdcPath = input.folderPath + input.sdc.csvName;
    private static final String dsPath = input.folderPath + input.ds.csvName;
    private static final String SDC_COLUMN = input.sdc.keyColumn.toLowerCase();
    private static final String DS_COLUMN = input.ds.keyColumn.toLowerCase();

    // performance profiling
    private TicTac2 tt = new TicTac2();

    public void solve(String[] args) {
        LCSInput.logFile.getFile().delete();
        tt.reset();
        CSVTable sdcTable = readTable(sdcPath);

        CSVTable dsTable = readTable(dsPath);

        List<Map<String, String>> sdcMaps = sdcTable.data;
        List<Map<String, String>> dsMaps = dsTable.data;

        List<Map<String, String>> joinMaps = new ArrayList<>();
        final int size = sdcMaps.size();
        // The mapping: i -> mapping[i] = ds column #i
        int[] mappings = new int[size];
        // The map key: i -> keys[i]
        String[] keys = new String[size];
        // LCS Index : i -> (sdc's index, ds's index)
        int[][] lcsIndices = new int[size][2];
        int[][] levDist = new int[size][2];

        int perfect = 0;
        boolean logDetail = true;
        for (int i = 0; i < size; i++) {
            Map<String, String> map = sdcMaps.get(i);
            String src = normalizeSDC(map.get(SDC_COLUMN));
            String srcLcs = "";
            String dstGood = "";
            int max = 0;
            mappings[i] = -1;
            if (logDetail) {
                L.log("  find #%s for /%s/", i, src);
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
            } else {
                levDist[i][0] = LevenshteinDistance.get(srcLcs, src);
                levDist[i][1] = LevenshteinDistance.get(srcLcs, dstGood);
            }
            if (logDetail) {
                L.log("got at #%s as /%s/", mappings[i], srcLcs);
            }
        }
        LF lf = LCSInput.logFile;
        FileOutput outLog = LCSInput.logFile.getFile();
        outLog.open(true);

        lf.log("End with %s", Arrays.toString(mappings));
        lf.log("Perfect = %s -> %.3f", perfect, 1F * perfect / size);

        tt.tic();
        for (int i = 0; i < mappings.length; i++) {
            String pf = "x";
            if (lcsIndices[i][0] >= 0 && lcsIndices[i][1] >= 0) {
                pf = "o";
            }
            outLog.writeln(""); // New line
            outLog.writeln("  sdc >> %s", sdcMaps.get(i).get(CSVTable.LINE));
            outLog.writeln("   ds -> %s", dsMaps.get(mappings[i]).get(CSVTable.LINE));
            outLog.writeln("  lcs => %s", keys[i]);
            outLog.writeln("  perfect = %s, idx = %s & %s", pf, lcsIndices[i][0], lcsIndices[i][1]);
            outLog.writeln("  lev dist = %s & %s", levDist[i][0], levDist[i][1]);
        }
        outLog.close();
        tt.tac("Write log file OK");

        L.log("---------------");

        tt.tic();
        FileOutput out = new FileOutput(outPath);
        FileOutput sim = new FileOutput(similar);
        FileOutput mis = new FileOutput(nomatch);
        FileOutput pack;

        FileOutput[] all = {out, sim, mis};

        for (FileOutput op : all) {
            op.delete();
            op.open(true);
            op.writeln("%s,LCS Key,LevDis Key,LCS,LevDis Value,LCS Value,%s", sdcTable.header, dsTable.header);
        }

        for (int i = 0; i < mappings.length; i++) {
            int key = mappings[i];
            boolean same = lcsIndices[i][0] >= 0 && lcsIndices[i][1] >= 0;

            int max = 4, diff = 3;
            boolean simi = levDist[i][0] < max && levDist[i][1] < max
                    && Math.abs(levDist[i][0] - levDist[i][1]) < diff;
            if (same) {
                pack = out;
            } else if (simi) {
                pack = sim;
            } else {
                pack = mis;
            }
            pack.writeln("%s,%s,%s,%s,%s,%s,%s"
                    , sdcMaps.get(i).get(CSVTable.LINE)
                    , sdcMaps.get(i).get(SDC_COLUMN)
                    , levDist[i][0]
                    , keys[i]
                    , levDist[i][1]
                    , dsMaps.get(key).get(DS_COLUMN)
                    , dsMaps.get(key).get(CSVTable.LINE)
            );
        }
        for (FileOutput op : all) {
            op.close();
        }
        tt.tac("Output File OK");
        L.log("CSV File created: %s", outPath);
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
        LF f = LCSInput.logFile;
        TicTacLF tt = new TicTacLF(f);
        tt.tic();
        CSVTable table = CSVTable.readCSVFile(path, f);
        tt.tac("Read %s rows in %s", table.data.size(), path);
        return table;
    }
}
