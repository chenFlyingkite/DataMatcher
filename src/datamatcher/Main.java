package datamatcher;

import datamatcher.solve.WordCount;
import util.tool.IOUtil;
import util.tool.TicTac;
import util.tool.TicTac2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TicTac2 tt = new TicTac2();
        TicTac.tic();
        //new LCSJoin().solve(null);
        tt.tic();
        WordCount.solve("D:\\YSData\\20171203\\HK2\\", "SDC_HK.csv", "ISSUER", "SDC_HK_name.csv");
        tt.tac("Part 1 OK");
        tt.tic();
        WordCount.solve("D:\\YSData\\20171203\\HK2\\", "Datastream_HK.csv", "Full Name", "Datastream_HK_name.csv");
        tt.tac("Part 2 OK");
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
            IOUtil.closeIt(fis);
        }

    }

    private static void log(String format, Object... param) {
        System.out.println(String.format(Locale.US, format, param));
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}