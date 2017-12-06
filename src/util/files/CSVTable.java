package util.files;

import util.logging.L;
import util.tool.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CSVTable {
    public static final String COMMA = ",";
    public static final String LINE = "CSVTable_line";
    private static boolean csvColumnIgnoreCase = true;

    public String header;
    public List<Map<String, String>> data = new ArrayList<>();

    public interface OnReadCSV {
        void onMissingFile(String path);
        void onNoHeader(String path);
        void onMissingColumn(String path, int lineNumber, int columnCount, String line);
        default void onDone(String path) {}
    }

    public static CSVTable readCSVFile(String path) {
        return readCSVFile(path, L.getImpl());
    }

    public static CSVTable readCSVFile(String path, OnReadCSV onRead) {
        CSVTable table = new CSVTable();
        List<Map<String, String>> data = new ArrayList<>();
        if (FileUtil.isMissing(path)) {
            if (onRead != null) {
                onRead.onMissingFile(path);
            }
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
                    if (onRead != null) {
                        onRead.onNoHeader(path);
                    }
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
                        if (onRead != null) {
                            onRead.onMissingColumn(path, ln, col, line);
                        }
                        table.data = data;
                        return table;
                    }

                    // Put the parsed columns
                    for (int i = 0; i < col; i++) {
                        String key = columns[i];
                        if (csvColumnIgnoreCase) {
                            key = key.toLowerCase();
                        }
                        m.put(key, lines[i]);
                    }
                    data.add(m);
                }
            }
            table.data = data;

            if (onRead != null) {
                onRead.onDone(path);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fis);
        }

        return table;
    }
}
