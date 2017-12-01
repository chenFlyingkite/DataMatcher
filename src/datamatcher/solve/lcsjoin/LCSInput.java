package datamatcher.solve.lcsjoin;

import datamatcher.solve.Basics;

import java.util.Arrays;
import java.util.Comparator;

public class LCSInput {
    public static final LCSInput ERIC_DATA = new LCSInput(
            //"D:\\YSData\\data\\",
            "F:\\GitHub\\DataMatcher\\data\\",
            "output.csv", "misMatch.csv",
            new CSVInput("SDC.csv", "Issuer" // # = 1107 rows
                , new String[]{" International" // # 33
                    , " Industrial" // # = 46
                    , " Technology" // # = 174
                    , " Inc" // # = 184
                    , " Ltd" // # = 459
                    , " Co Ltd" // # = 404
                    , " Corp" // # = 290
                    , " Electronic" // #= 17
                    , "\\x2E" // = .
                    , " "}),
            new CSVInput("DataStream.csv", "Full Name" // # = 2592
                , new String[]{" International" // # = 184
                    , " Technology" // # = 784
                    , " Corporation" // # = 28
                    , " Limited" // # = 15
                    , " Industry" // # = 80
                    , " Conversion Certificate" // # = 24
                    , " First Year of Conversion" // # = 11
                    , " Second Year of Conversion" // # = 5
                    , " Industrial" // # 176
                    , " (Taiwan)" // # = 16
                    , " Company" // # = 48
                    , "\\x2E" // = .
                    , " "})
    );
    // The replaced text is in regular expression
    // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

    // ASCII Table and Description
    // http://www.asciitable.com/

    // edit distance
    // https://en.wikipedia.org/wiki/Levenshtein_distance

    public static LCSInput get() {
        return ERIC_DATA;
    }

    public final String folderPath;
    public final String outputPath;
    public final String mismatchPath;
    public final CSVInput sdc;
    public final CSVInput ds;

    public LCSInput(String path, String outputName, String mismatchName
            , CSVInput sdcInput, CSVInput dsInput) {
        folderPath = path;
        outputPath = outputName;
        mismatchPath = mismatchName;
        sdc = sdcInput;
        ds = dsInput;
    }

    public static class CSVInput {
        public final String csvName;
        /** Case sensitive */
        public final String keyColumn;
        public final String[] keywords;

        public CSVInput(String name, String column, String[] erased) {
            csvName = name;
            keyColumn = column;
            if (erased == null) {
                keywords = null;
            } else {
                Arrays.sort(erased, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int n = o1.length();
                        int m = o2.length();
                        return Integer.compare(m, n);
                    }
                });
                keywords = new String[erased.length];
                for (int i = 0; i < erased.length; i++) {
                    keywords[i] = erased[i].toLowerCase();
                }
                Basics.log("CSVInput name = %s\n  keywords = %s"
                        , name, Arrays.toString(keywords));
            }
        }
    }
}
