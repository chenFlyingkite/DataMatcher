package datamatcher.solve;

import datamatcher.algorithm.LCSString;
import datamatcher.util.TicTac;
import datamatcher.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LCSJoin extends Basics {
    private static final String COMMA = ",";
    private static final String LINE = "line";
    private static final String sdcPath = "D:\\YSData\\data\\SDC.csv";
    private static final String dsPath = "D:\\YSData\\data\\DataStream.csv";
    private static final String SDC_COLUMN = "Issuer";
    private static final String DS_COLUMN = "Full Name";

    public void solve(String[] args) {
        TicTac.tic();
        List<Map<String, String>> sdcMaps = readFile(sdcPath);
        TicTac.tac("File read OK, %s rows", sdcMaps.size());

        TicTac.tic();
        List<Map<String, String>> dsMaps = readFile(dsPath);
        TicTac.tac("File read OK, %s rows", dsMaps.size());

        List<Map<String, String>> joinMaps = new ArrayList<>();
        int[] mappings = new int[sdcMaps.size()];
        String[] keys = new String[sdcMaps.size()];
        for (int i = 0; i < sdcMaps.size(); i++) {
            Map<String, String> map = sdcMaps.get(i);
            String src = normalize(map.get(SDC_COLUMN));
            String srcLcs = "";
            int n = 0;
            mappings[i] = -1;
            log("find #%s for /%s/", i, src);
            for (int j = 0; j < dsMaps.size(); j++) {
                Map<String, String> dsMap = dsMaps.get(j);
                String dst = normalize(dsMap.get(DS_COLUMN));
                String s = "";
                if (dst.length() >= n) {
                    //TicTac.tic();
                    s = LCSString.LCSAlgorithm(src, dst);
                    //TicTac.tac("LCS -> %s", s);
                }

                if (s.length() > n) {
                    log("Map: %s = %s -> %s", s, src, dst);
                    srcLcs = s;
                    n = s.length();
                    mappings[i] = j;
                    keys[i] = s;
                }
            }
            log("got at #%s as /%s/", mappings[i], srcLcs);
        }
        log("End with %s", Arrays.toString(mappings));
        for (int i = 0; i < mappings.length; i++) {
            log("%s\n  -> %s\n  => %s\n"
                    , sdcMaps.get(i).get(LINE), dsMaps.get(mappings[i]).get(LINE)
                    , keys[i]
            );
        }
    }

    private String normalize(String from) {
        String result = from.toLowerCase();
        result = result.replaceAll("corp", "");
        result = result.replaceAll("ltd", "");
        result = result.replaceAll("international", "");

        return result;

    }

    private static List<Map<String, String>> readFile(String path) {
        List<Map<String, String>> data = new ArrayList<>();
        if (missingFile(path)) {
            log("File not found: %s", path);
            return data;
        }

        File f = new File(path);
        Scanner fis = null;
        try {
            fis = new Scanner(f);
            String line;
            String[] lines;

            String[] columns = null;
            int col = 0;
            // Read header
            if (fis.hasNextLine()) {
                line = fis.nextLine();
                lines = line.split(COMMA);
                if (lines.length == 0) {
                    log("Missing header columns, omit file");
                    return data;
                }
                columns = lines;
                col = columns.length;
            }

            while (fis.hasNextLine()) {
                line = fis.nextLine();
                lines = line.split(COMMA);
                if (lines.length > 0) {
                    Map<String, String> m = new HashMap<>();
                    m.put(LINE, line);
                    if (lines.length != col) {
                        log("Bad data, missing columns\n  %s columns expected:\n  %s", col, line);
                        return data;
                    }

                    for (int i = 0; i < col; i++) {
                        m.put(columns[i], lines[i]);
                    }
                    data.add(m);
                }

                //log("%s -> %s", lines.length, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            Util.closeIt(fis);
        }

        return data;
    }
}
