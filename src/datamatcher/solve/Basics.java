package datamatcher.solve;

import java.io.File;
import java.util.Locale;

public class Basics {
    protected static boolean missingFile(String path) {
        if (path == null || "".equals(path)) return true;

        File f = new File(path);
        if (!f.exists()) return true;

        return false;
    }

    protected static void log(String format, Object... param) {
        System.out.println(String.format(Locale.US, format, param));
    }

    protected static void log(String msg) {
        System.out.println(msg);
    }
}
