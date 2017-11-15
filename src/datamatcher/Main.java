package datamatcher;

import datamatcher.solve.LCSJoin;
import datamatcher.util.TicTac;
import datamatcher.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        log("Hello World!!");

        TicTac.tic();
        //readFile("D:\\YSData\\data\\SDC.csv");
        new LCSJoin().solve(null);
        TicTac.tac("Solved");

    }

    private static void readFile(String path) {
        log("File = %s", path);
        if (path == null || "".equals(path)) return;

        File f = new File(path);
        if (!f.exists()) {
            log("File not found: %s", path);
            return;
        }

        Scanner fis = null;
        try {
            fis = new Scanner(f);
            String line;
            String[] lines;
            while (fis.hasNextLine()) {
                line = fis.nextLine();
                lines = line.split(",");
                log("%s -> %s", lines.length, line);
            }
            log("Ended");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            Util.closeIt(fis);
        }

    }

    private static void log(String format, Object... param) {
        System.out.println(String.format(Locale.US, format, param));
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}