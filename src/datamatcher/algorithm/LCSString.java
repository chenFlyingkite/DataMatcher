package datamatcher.algorithm;

/**
 * This is an implementation, in Java, of the Longest Common Subsequence algorithm.
 * That is, given two strings A and B, this program will find the longest sequence
 * of letters that are common and ordered in A and B.
 *
 * There are only two reasons you are reading this:
 *   - you don't care what the algorithm is but you need a piece of code to do it
 *   - you're trying to understand the algorithm, and a piece of code might help
 * In either case, you should either read an entire chapter of an algorithms textbook
 * on the subject of dynamic programming, or you should consult a webpage that describes
 * this particular algorithm.   It is important, for example, that we use arrays of size
 * |A|+1 x |B|+1.
 *
 */
public class LCSString {
    // These are "constants" which indicate a direction in the backtracking array.
    private static final int NEITHER     = 0;
    private static final int UP          = 1;
    private static final int LEFT        = 2;
    private static final int UP_AND_LEFT = 3;

    public static String LCSAlgorithm(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] S = new int[n+1][m+1];
        int[][] R = new int[n+1][m+1];
        int i, j;

        // It is important to use <=, not <.  The next two for-loops are initialization
        for (i = 0; i <= n; ++i) {
            S[i][0] = 0;
            R[i][0] = UP;
        }
        for (j = 0; j <= m; ++j) {
            S[0][j] = 0;
            R[0][j] = LEFT;
        }

        // This is the main dynamic programming loop that computes the score and
        // backtracking arrays.
        for (i = 1; i <= n; ++i) {
            for (j = 1; j <= m; ++j) {

                if (a.charAt(i-1) == b.charAt(j-1)) {
                    S[i][j] = S[i-1][j-1] + 1;
                    R[i][j] = UP_AND_LEFT;
                } else {
                    S[i][j] = S[i-1][j-1];
                    R[i][j] = NEITHER;
                }

                if (S[i-1][j] >= S[i][j]) {
                    S[i][j] = S[i-1][j];
                    R[i][j] = UP;
                }

                if (S[i][j-1] >= S[i][j]) {
                    S[i][j] = S[i][j-1];
                    R[i][j] = LEFT;
                }
            }
        }

        // The length of the longest substring is S[n][m]
        i = n;
        j = m;
        int pos = S[i][j] - 1;
        char[] lcs = new char[pos + 1];

        // Trace the backtracking matrix.
        while (i > 0 || j > 0) {
            switch (R[i][j]) {
                case UP:
                    i--;
                    break;
                case LEFT:
                    j--;
                    break;
                case UP_AND_LEFT:
                    i--;
                    j--;
                    lcs[pos--] = a.charAt(i);
                    break;
            }
        }

        return new String(lcs);
    }
}
