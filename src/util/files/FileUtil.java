package util.files;

import java.io.File;

public class FileUtil {
    private FileUtil() {}

    protected static boolean isMissing(String path) {
        if (path == null) {
            return true;
        } else {
            File f = new File(path);
            return !f.exists();
        }
    }
}
