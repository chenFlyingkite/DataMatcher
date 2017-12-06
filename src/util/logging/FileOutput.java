package util.logging;

import util.tool.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class FileOutput {
    private PrintWriter pw;
    private FileOutputStream fos;
    private File file;

    public FileOutput(String name) {
        file = new File(name);
        validate();
    }

    public FileOutput(File file1) {
        file = file1;
        validate();
    }

    private void validate() {
        if (file.exists() && file.isDirectory()) {
            file.delete();
        }
    }

    public FileOutput open() {
        return open(true);
    }

    public FileOutput open(boolean append) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            close();

            fos = new FileOutputStream(file, append);
            pw = new PrintWriter(fos);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtil.closeIt(fos, pw);
        }
        return this;
    }

    public FileOutput writeln(String format, Object... param) {
        return writeln(String.format(Locale.US, format, param));
    }

    public FileOutput writeln(String msg) {
        if (pw == null) {
            L.log("Did not opened :%s", file);
        } else {
            pw.append(msg).append("\r\n");
        }
        return this;
    }

    public FileOutput close() {
        flush();
        IOUtil.closeIt(fos, pw);
        return this;
    }

    public FileOutput delete() {
        file.delete();
        return this;
    }

    public FileOutput flush() {
        IOUtil.flushIt(fos, pw);
        return this;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
