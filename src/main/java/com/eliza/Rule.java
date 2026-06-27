package com.eliza;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a conversation rule with conditions and responses
 */
public class Rule {

    private String name;
    private int priority;
    private List<String> patterns;
    private List<String> responses;
    private List<Predicate<ConversationContext>> conditions;
    private List<RuleAction> actions;
    private int responseIndex;
    private String topic;
    private String emotionalCategory;

    public Rule(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.patterns = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.responseIndex = 0;
    }

    public void addPattern(String pattern) {
        patterns.add(pattern.toUpperCase());
    }

    public void addResponse(String response) {
        responses.add(response);
    }

    public void addCondition(Predicate<ConversationContext> condition) {
        conditions.add(condition);
    }

    public void addAction(RuleAction action) {
        actions.add(action);
    }

    public boolean matches(String input, ConversationContext context) {
        input = input.toUpperCase();

        // Check if any pattern matches
        boolean patternMatches = false;
        for (String pattern : patterns) {
            if (input.contains(pattern)) {
                patternMatches = true;
                break;
            }
        }

        if (!patternMatches) {
            return false;
        }

        // Check all conditions
        for (Predicate<ConversationContext> condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }

        return true;
    }

    public String getResponse(ConversationContext context) {
        if (responses.isEmpty()) {
            return "Tell me more.";
        }

        String response = responses.get(responseIndex);
        responseIndex = (responseIndex + 1) % responses.size();
        return response;
    }

    public void executeActions(ConversationContext context) {
        for (RuleAction action : actions) {
            action.execute(context);
        }
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setEmotionalCategory(String category) {
        this.emotionalCategory = category;
    }

    public String getEmotionalCategory() {
        return emotionalCategory;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public interface RuleAction {
        void execute(ConversationContext context);
    }
}
