package util.logging;

import java.util.Locale;

public interface Loggable {
    /**
     * Writing the log with message
     * @param msg The message to be logged
     */
    void log(String msg);

    /**
     * Writing the log with String format and its parameters
     *
     * @see String#format(String, Object...)
     * @see String#format(Locale, String, Object...)
     */
    default void log(String format, Object... param) {
        log(String.format(Locale.US, format, param));
    }
}
