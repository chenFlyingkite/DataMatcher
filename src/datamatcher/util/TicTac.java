package datamatcher.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class TicTac {
    //private static final String TAG = "TicTac";
    private static final Stack<Long> tictac = new Stack<>();

    public static void tic() {
        tictac.push(System.currentTimeMillis());
    }

    public static void tac(String format, Object... params) {
        tac(String.format(java.util.Locale.US, format, params));
    }

    public static void tac(String msg) {
        long tac = System.currentTimeMillis();
        if (tictac.empty()) {
            logError(tac, msg);
            return;
        }
        long tic = tictac.pop();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tictac.size(); i++) {
            sb.append(" ");
        }
        sb.append("[").append(tac - tic).append("] : ").append(msg);
        logTac(sb.toString());
    }

    protected static void logError(long tac,String msg) {
        String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(tac));
        System.out.println("X_X Omitted. tic = N/A, tac = " + time + " : " + msg);
    }

    protected static void logTac(String msg) {
        System.out.println(msg);
    }
}

