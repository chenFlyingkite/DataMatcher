package datamatcher.solve.lcsjoin;

import datamatcher.solve.Basics;

import java.util.Arrays;
import java.util.Comparator;

public class LCSInput {
    public static final LCSInput ERIC_DATA = new LCSInput(
            "D:\\YSData\\data\\", "output.csv",
            "SDC.csv", "Issuer",
            "DataStream.csv", "Full Name",
            new String[]{"international", "corp", "co ltd", " "},
            new String[]{"international", "corp"
                    , "Conversion Certificate"
                    , "Year of Conversion"
                    , "co ltd"
                    , " "}
    );

    public static LCSInput get() {
        return ERIC_DATA;
    }

    public final String folderPath;
    public final String outFile;
    public final String sdcFile;
    /** Case sensitive */
    public final String SDC_COLUMN;

    public final String dsFile;
    /** Case sensitive */
    public final String DS_COLUMN;
    public final Erase sdcErase;
    public final Erase dsErase;

    public LCSInput(String path, String outputName
            , String sdcName, String sdcCol, String dsName, String dsCol
            , String[] sdcErased, String[] dsErased) {
        folderPath = path;
        outFile = outputName;
        sdcFile = sdcName;
        SDC_COLUMN = sdcCol;
        dsFile = dsName;
        DS_COLUMN = dsCol;
        sdcErase = new Erase(sdcErased);
        dsErase = new Erase(dsErased);
    }

    public static class Erase {
        public final String[] keywords;

        public Erase(String[] words) {
            if (words == null) {
                keywords = null;
            } else {
                Arrays.sort(words, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int n = o1.length();
                        int m = o2.length();
                        return Integer.compare(m, n);
                    }
                });
                keywords = new String[words.length];
                for (int i = 0; i < words.length; i++) {
                    keywords[i] = words[i].toLowerCase();
                }
                Basics.log("Erase keywords = %s", Arrays.toString(keywords));
            }
        }
    }
}
