package util.tool;

public class TicTac {
    private static final TicTac2 core = new TicTac2();

    public static void tic() {
        core.tic();
    }

    public static void tac(String msg) {
        core.tac(msg);
    }

    public static void tac(String format, Object... params) {
        core.tac(format, params);
    }

    public void reset() {
        core.reset();
    }
}


