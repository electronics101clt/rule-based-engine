package com.eliza;

import java.util.*;
import java.util.regex.*;

public class AdvancedMatcher {

    private ConversationContext context;
    private Map<String, String[]> synonyms;

    public AdvancedMatcher(ConversationContext context) {
        this.context = context;
        initializeSynonyms();
    }

    private void initializeSynonyms() {
        synonyms = new HashMap<>();
        synonyms.put("INSTALL", new String[]{"INSTALL", "SETUP", "ADD", "GET", "DOWNLOAD"});
        synonyms.put("REMOVE", new String[]{"REMOVE", "UNINSTALL", "DELETE", "PURGE", "ERASE"});
        synonyms.put("FIX", new String[]{"FIX", "REPAIR", "SOLVE", "RESOLVE", "TROUBLESHOOT"});
        synonyms.put("CONFIGURE", new String[]{"CONFIGURE", "SETUP", "SET", "CHANGE", "MODIFY"});
        synonyms.put("DISPLAY", new String[]{"DISPLAY", "SCREEN", "MONITOR"});
        synonyms.put("SLOW", new String[]{"SLOW", "SLUGGISH", "LAGGY", "UNRESPONSIVE"});
    }

    public List<ScoredMatch> findMatches(String input, List<Rule> rules) {
        List<ScoredMatch> matches = new ArrayList<>();
        input = " " + input.toUpperCase() + " ";

        for (Rule rule : rules) {
            for (String pattern : rule.getPatterns()) {
                // Check direct match
                int position = findPattern(input, pattern);
                if (position >= 0) {
                    int score = calculateScore(rule, pattern, position, false);
                    matches.add(new ScoredMatch(rule, score, position, pattern));
                }

                // Check synonym matches
                String[] words = pattern.split(" ");
                for (String word : words) {
                    if (synonyms.containsKey(word)) {
                        for (String synonym : synonyms.get(word)) {
                            String synPattern = pattern.replace(word, synonym);
                            int synPos = findPattern(input, synPattern);
                            if (synPos >= 0) {
                                int score = calculateScore(rule, synPattern, synPos, true);
                                matches.add(new ScoredMatch(rule, score, synPos, synPattern));
                            }
                        }
                    }
                }
            }
        }

        matches.sort((a, b) -> Integer.compare(b.score, a.score));
        return matches;
    }

    private int findPattern(String input, String pattern) {
        // Word boundary matching
        String regex = "\\b" + Pattern.quote(pattern.trim()) + "\\b";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.find() ? m.start() : -1;
    }

    private int calculateScore(Rule rule, String pattern, int position, boolean isSynonym) {
        int score = 0;

        // Base priority (0-100) × 10
        score += rule.getPriority() * 10;

        // Specificity bonus - word count × 5
        int wordCount = pattern.trim().split("\\s+").length;
        score += wordCount * 5;

        // Position bonus - earlier in text = higher score
        score += (1000 - position);

        // Context relevance
        if (context.getCurrentTopic() != null &&
            context.getCurrentTopic().equals(rule.getTopic())) {
            score += 30;
        }

        // Synonym penalty
        if (isSynonym) {
            score -= 10;
        }

        // Exact phrase bonus
        if (wordCount >= 3) {
            score += 20;
        }

        return score;
    }

    public static class ScoredMatch {
        public Rule rule;
        public int score;
        public int position;
        public String matchedPattern;

        public ScoredMatch(Rule rule, int score, int position, String matchedPattern) {
            this.rule = rule;
            this.score = score;
            this.position = position;
            this.matchedPattern = matchedPattern;
        }

        @Override
        public String toString() {
            return String.format("[%s] score=%d pos=%d pattern='%s'",
                rule.getName(), score, position, matchedPattern);
        }
    }
}
