package datamatcher.util;

import java.io.Closeable;
import java.io.IOException;

public class Util {
    private Util() {}

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
}
