package util.logging;

import util.files.CSVTable;

import java.io.File;

/**
 * The class for logging message to console log and file
 * L = console log of {@link L}
 * F = File output of {@link FileOutput}
 *
 * @see System#out
 * @see FileOutput
 */
public class LF implements CSVTable.OnReadCSV {
    private final FileOutput file;

    private boolean logToFile = true;

    public LF(String folder) {
        file = new FileOutput(folder + File.separator + "log.txt");
    }

    public FileOutput getFile() {
        return file;
    }

    /**
     * True if following callings of {@link #log(String)}, {@link #log(String, Object...)}
     * will also output to file<br/>
     * False if no need to output to file
     */
    public void setLogToFile(boolean toFile) {
        logToFile = toFile;
    }

    private static final L.Impl impl = L.getImpl();

    public void log(String msg) {
        impl.log(msg);
        if (logToFile) {
            file.writeln(msg);
        }
    }

    public void log(String msg, Object... param) {
        impl.log(msg, param);
        if (logToFile) {
            file.writeln(msg, param);
        }
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
