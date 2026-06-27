package com.eliza;

import java.util.*;

public class UbuntuAssistantAdvanced extends UbuntuAssistant {

    private AdvancedMatcher matcher;
    private boolean useAdvancedMatching = true;

    public UbuntuAssistantAdvanced() {
        super();
        this.matcher = new AdvancedMatcher(getContext());
    }

    @Override
    public String getResponse(String input) {
        if (!useAdvancedMatching) {
            return super.getResponse(input);
        }

        List<AdvancedMatcher.ScoredMatch> matches = matcher.findMatches(input, super.rules);

        String response;
        if (!matches.isEmpty()) {
            AdvancedMatcher.ScoredMatch best = matches.get(0);
            Rule selectedRule = best.rule;
            response = selectedRule.getResponse(getContext());
            selectedRule.executeActions(getContext());

            if (selectedRule.getTopic() != null) {
                getContext().setCurrentTopic(selectedRule.getTopic());
            }

            // Debug info
            System.out.println("[DEBUG] Matched: " + best);
            if (matches.size() > 1) {
                System.out.println("[DEBUG] Alternatives: ");
                for (int i = 1; i < Math.min(3, matches.size()); i++) {
                    System.out.println("  " + matches.get(i));
                }
            }
        } else {
            if (getLLMConfig().isLLMEnabled()) {
                LLMProvider llmProvider = new LLMProvider(getLLMConfig());
                String llmResponse = llmProvider.query(input);
                if (llmResponse != null && !llmResponse.isEmpty()) {
                    response = "🤖 " + llmResponse;
                } else {
                    response = getDefaultResponse();
                }
            } else {
                response = getDefaultResponse();
            }
        }

        getContext().addToHistory(input, response);
        return response;
    }

    public void setUseAdvancedMatching(boolean use) {
        this.useAdvancedMatching = use;
    }

    private String getDefaultResponse() {
        String[] defaults = {
            "I'm not sure I understand. Can you rephrase that?",
            "Could you be more specific?",
            "What exactly do you need help with?"
        };
        return defaults[new Random().nextInt(defaults.length)];
    }
}
