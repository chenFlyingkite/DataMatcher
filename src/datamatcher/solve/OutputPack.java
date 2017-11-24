package datamatcher.solve;

import datamatcher.util.Util;

import java.io.*;
import java.util.Locale;

public class OutputPack {
    private PrintWriter pw;
    private FileOutputStream fos;
    private String filename;
    private File file;

    public OutputPack(String name) {
        filename = name;
        file = new File(name);

        if (file.exists() && file.isDirectory()) {
            file.delete();
        }
    }

    public void open(boolean append) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file, append);
            pw = new PrintWriter(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputPack writeln(String format, Object... param) {
        return writeln(String.format(Locale.US, format, param));
    }

    public OutputPack writeln(String msg) {
        //Basics.log(msg);
        if (pw != null) {
            pw.append(msg).append("\n");
        }
        return this;
    }

    public void close() {
        if (pw != null) {
            pw.flush();
        }
        Util.closeIt(fos, pw);
    }

    public void delete() {
        file.delete();
    }
}