package datamatcher.algorithm;

public class LevenshteinDistance {
    private LevenshteinDistance() {}

    public static int get(String s1, String s2) {
        int n = s1.length();
        int m = s2.length();
        int[][] d = new int[n+1][m+1];

        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int modi;
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    modi = 0;
                } else {
                    modi = 1;
                }
                d[i][j] = Math.min(Math.min(
                        d[i-1][j  ] + 1, // Delete
                        d[i  ][j-1] + 1), // Insert
                        d[i-1][j-1] + modi // Change
                );
            }
        }
        return d[n][m];
    }
}
