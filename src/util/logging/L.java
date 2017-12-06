package util.logging;

import util.files.CSVTable;

/**
 * The class for System.out.println()
 */
public class L {
    private static final Impl impl = new Impl();

    public static void log(String msg) {
        impl.log(msg);
    }

    public static void log(String msg, Object... param) {
        impl.log(msg, param);
    }

    public static Impl getImpl() {
        return impl;
    }

    public static class Impl implements Loggable, CSVTable.OnReadCSV {

        public void print(String msg) {
            System.out.print(msg);
        }

        @Override
        public void log(String msg) {
            System.out.println(msg);
        }

        @Override
        public void onMissingFile(String path) {
            log("File not found: %s", path);
        }

        @Override
        public void onNoHeader(String path) {
            log("Missing header columns, omit file");
        }

        @Override
        public void onMissingColumn(String path, int lineNumber, int columnCount, String line) {
            log("Missing column at line #%s. Expected %s columns", lineNumber, columnCount);
            log("  %s", line);
        }
    }
}
