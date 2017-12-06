package util.tool;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class IOUtil {
    private IOUtil() {}

    public static void closeIt(Closeable... c) {
        if (c == null) return;
        for (Closeable d : c) {
            if (d != null) {
                try {
                    d.close();
                } catch (IOException e) {
                    // Ignore it
                }
            }
        }
    }

    public static void flushIt(Flushable... f) {
        if (f == null) return;
        for (Flushable g : f) {
            if (g != null) {
                try {
                    g.flush();
                } catch (IOException e) {
                    // Ignore it
                }
            }
        }
    }
}
