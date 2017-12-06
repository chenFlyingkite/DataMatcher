package util.tool;

import util.logging.LF;

public class TicTacLF extends TicTac2 {
    private LF logFile;

    public TicTacLF(LF file) {
        logFile = file;
        logFile.getFile().open();
    }

    @Override
    protected void logError(long tac, String msg) {
        logFile.log("X_X Omitted. tic = N/A, tac = %s : %s", getTime(tac), msg);
        logFile.getFile().flush();
    }

    @Override
    protected void logTac(String msg) {
        logFile.log(msg);
        logFile.getFile().flush();
    }
}
