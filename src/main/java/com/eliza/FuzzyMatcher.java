package com.eliza;

import java.util.*;

/**
 * Fuzzy string matching using Levenshtein distance and n-gram similarity
 */
public class FuzzyMatcher {

    private Map<String, String> typoCorrections;
    private int maxDistance = 2;

    public FuzzyMatcher() {
        initializeTypoCorrections();
    }

    private void initializeTypoCorrections() {
        typoCorrections = new HashMap<>();

        // Common typos
        typoCorrections.put("instal", "install");
        typoCorrections.put("instaling", "installing");
        typoCorrections.put("configurate", "configure");
        typoCorrections.put("ubunto", "ubuntu");
        typoCorrections.put("ubnutu", "ubuntu");
        typoCorrections.put("linuks", "linux");
        typoCorrections.put("grafics", "graphics");
        typoCorrections.put("grafx", "graphics");
        typoCorrections.put("moniters", "monitors");
        typoCorrections.put("moniter", "monitor");
        typoCorrections.put("screan", "screen");
        typoCorrections.put("scren", "screen");
        typoCorrections.put("audo", "audio");
        typoCorrections.put("soud", "sound");
        typoCorrections.put("conect", "connect");
        typoCorrections.put("disconect", "disconnect");
        typoCorrections.put("netwrok", "network");
        typoCorrections.put("netowrk", "network");
        typoCorrections.put("comand", "command");
        typoCorrections.put("commnad", "command");
        typoCorrections.put("pacakge", "package");
        typoCorrections.put("packge", "package");
        typoCorrections.put("upate", "update");
        typoCorrections.put("udpate", "update");
        typoCorrections.put("upgarde", "upgrade");
        typoCorrections.put("removve", "remove");
        typoCorrections.put("delet", "delete");
        typoCorrections.put("broswer", "browser");
        typoCorrections.put("brwoser", "browser");
        typoCorrections.put("firefx", "firefox");
        typoCorrections.put("chromw", "chrome");
        typoCorrections.put("termianl", "terminal");
        typoCorrections.put("terinal", "terminal");
        typoCorrections.put("kernal", "kernel");
        typoCorrections.put("kermel", "kernel");
        typoCorrections.put("dirver", "driver");
        typoCorrections.put("drivr", "driver");
        typoCorrections.put("nvida", "nvidia");
        typoCorrections.put("nvidea", "nvidia");
        typoCorrections.put("resoltuion", "resolution");
        typoCorrections.put("reoslution", "resolution");
        typoCorrections.put("dispaly", "display");
        typoCorrections.put("dsplay", "display");
        typoCorrections.put("permision", "permission");
        typoCorrections.put("permisions", "permissions");
        typoCorrections.put("directoy", "directory");
        typoCorrections.put("direcotry", "directory");
        typoCorrections.put("commpile", "compile");
        typoCorrections.put("copile", "compile");
        typoCorrections.put("sytem", "system");
        typoCorrections.put("systme", "system");
    }

    /**
     * Correct typos in input text
     */
    public String correctTypos(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();

        for (String word : words) {
            String clean = word.replaceAll("[^a-z]", "");
            if (typoCorrections.containsKey(clean)) {
                corrected.append(typoCorrections.get(clean)).append(" ");
            } else {
                corrected.append(word).append(" ");
            }
        }

        return corrected.toString().trim();
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    public int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(
                        dp[i - 1][j],     // deletion
                        dp[i][j - 1]),    // insertion
                        dp[i - 1][j - 1]  // substitution
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Calculate similarity ratio (0.0 - 1.0)
     */
    public double similarity(String s1, String s2) {
        int distance = levenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        int maxLen = Math.max(s1.length(), s2.length());
        return maxLen == 0 ? 1.0 : 1.0 - ((double) distance / maxLen);
    }

    /**
     * Find fuzzy matches in a list of candidates
     */
    public List<FuzzyMatch> findMatches(String query, List<String> candidates, double threshold) {
        List<FuzzyMatch> matches = new ArrayList<>();

        for (String candidate : candidates) {
            double sim = similarity(query, candidate);
            if (sim >= threshold) {
                matches.add(new FuzzyMatch(candidate, sim, levenshteinDistance(query, candidate)));
            }
        }

        matches.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        return matches;
    }

    /**
     * N-gram similarity (character-level)
     */
    public double ngramSimilarity(String s1, String s2, int n) {
        Set<String> ngrams1 = getNgrams(s1.toLowerCase(), n);
        Set<String> ngrams2 = getNgrams(s2.toLowerCase(), n);

        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private Set<String> getNgrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        if (text.length() < n) {
            ngrams.add(text);
            return ngrams;
        }

        for (int i = 0; i <= text.length() - n; i++) {
            ngrams.add(text.substring(i, i + n));
        }
        return ngrams;
    }

    /**
     * Soundex phonetic matching
     */
    public String soundex(String s) {
        if (s == null || s.isEmpty()) return "";

        s = s.toUpperCase().replaceAll("[^A-Z]", "");
        if (s.isEmpty()) return "";

        StringBuilder soundex = new StringBuilder();
        soundex.append(s.charAt(0));

        String codes = "01230120022455012623010202";
        char prev = codes.charAt(s.charAt(0) - 'A');

        for (int i = 1; i < s.length() && soundex.length() < 4; i++) {
            char curr = codes.charAt(s.charAt(i) - 'A');
            if (curr != '0' && curr != prev) {
                soundex.append(curr);
            }
            prev = curr;
        }

        while (soundex.length() < 4) {
            soundex.append('0');
        }

        return soundex.toString();
    }

    public static class FuzzyMatch {
        public String text;
        public double similarity;
        public int distance;

        public FuzzyMatch(String text, double similarity, int distance) {
            this.text = text;
            this.similarity = similarity;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("%s (sim=%.2f, dist=%d)", text, similarity, distance);
        }
    }
}
