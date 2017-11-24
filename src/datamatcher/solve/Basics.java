package datamatcher.solve;

import datamatcher.solve.lcsjoin.LCSInput;

import java.io.File;
import java.util.Locale;

public class Basics {
    protected static boolean missingFile(String path) {
        if (path == null || "".equals(path)) return true;

        File f = new File(path);
        if (!f.exists()) return true;

        return false;
    }

    public static void log(String format, Object... param) {
        System.out.println(String.format(Locale.US, format, param));
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    private static String logFile = LCSInput.get().folderPath + "log.txt";

    private static OutputPack logs = new OutputPack(logFile);

    public static void logFile(String format, Object... param) {
        logs.writeln(format, param);
    }

    public static void logFile(String msg) {
        logs.writeln(msg);
    }

    public static void deleteLogFile() {
        logs.delete();
    }

    public static void openLogFile() {
        logs.open(true);
    }

    public static void closeLogFile() {
        logs.close();
    }
}
