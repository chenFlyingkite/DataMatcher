package util.tool;

public class TextUtil {
    private TextUtil() {}

    public static boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }
}
