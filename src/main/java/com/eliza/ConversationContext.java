package com.eliza;

import java.util.*;

/**
 * Tracks conversation state for iterative, rule-based dialogue
 */
public class ConversationContext {

    private Map<String, Object> variables;
    private List<String> conversationHistory;
    private String currentTopic;
    private String lastKeyword;
    private Map<String, Integer> topicFrequency;
    private int turnCount;
    private String emotionalState;
    private Set<String> mentionedTopics;

    public ConversationContext() {
        this.variables = new HashMap<>();
        this.conversationHistory = new ArrayList<>();
        this.topicFrequency = new HashMap<>();
        this.mentionedTopics = new HashSet<>();
        this.turnCount = 0;
    }

    public void addToHistory(String userInput, String elizaResponse) {
        conversationHistory.add("USER: " + userInput);
        conversationHistory.add("ELIZA: " + elizaResponse);
        turnCount++;
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public boolean hasVariable(String key) {
        return variables.containsKey(key);
    }

    public void setCurrentTopic(String topic) {
        this.currentTopic = topic;
        topicFrequency.put(topic, topicFrequency.getOrDefault(topic, 0) + 1);
        mentionedTopics.add(topic);
    }

    public String getCurrentTopic() {
        return currentTopic;
    }

    public void setLastKeyword(String keyword) {
        this.lastKeyword = keyword;
    }

    public String getLastKeyword() {
        return lastKeyword;
    }

    public int getTopicFrequency(String topic) {
        return topicFrequency.getOrDefault(topic, 0);
    }

    public boolean hasDiscussedTopic(String topic) {
        return mentionedTopics.contains(topic);
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setEmotionalState(String state) {
        this.emotionalState = state;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public List<String> getRecentHistory(int n) {
        int size = conversationHistory.size();
        if (size <= n) {
            return new ArrayList<>(conversationHistory);
        }
        return conversationHistory.subList(size - n, size);
    }

    public String getLastUserInput() {
        for (int i = conversationHistory.size() - 1; i >= 0; i--) {
            String line = conversationHistory.get(i);
            if (line.startsWith("USER: ")) {
                return line.substring(6);
            }
        }
        return null;
    }

    public Set<String> getAllMentionedTopics() {
        return new HashSet<>(mentionedTopics);
    }
}
