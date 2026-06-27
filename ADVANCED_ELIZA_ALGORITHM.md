# Advanced ELIZA Algorithm Improvements

## Current Algorithm Problems
```
keywords[36] → linear search → first match → no scoring
```

## Proposed Improvements

### 1. SCORING SYSTEM
```java
class KeywordMatch {
    String keyword;
    int position;
    int score;

    score = basePriority + (1000 - position) + specificityBonus;
}
```

**Example:**
- "CAN YOU INSTALL CHROME"
  - Match "CAN YOU" (priority 50, pos 0) → score 1050
  - Match "INSTALL" (priority 85, pos 8) → score 1077
  - Pick highest score (INSTALL)

### 2. PATTERN SPECIFICITY
```java
// More specific = higher bonus
"INSTALL NVIDIA DRIVER" → +30 (3 words)
"INSTALL DRIVER" → +20 (2 words)
"INSTALL" → +0 (1 word)
```

### 3. CONTEXT TRACKING
```java
class ConversationContext {
    String[] lastTopics = new String[5];
    Map<String, Integer> topicFrequency;
    String lastKeyword;

    // Boost related topics
    if (currentTopic.equals(lastTopic)) {
        score += 20;
    }
}
```

### 4. MULTI-PATTERN MATCHING
```java
// Current: first match breaks
for (keyword : keywords) {
    if (match) break;  // ❌ stops here
}

// Better: collect all matches
List<Match> allMatches = new ArrayList<>();
for (keyword : keywords) {
    if (match) allMatches.add(match);
}
allMatches.sort(byScore);
```

### 5. WORD BOUNDARIES
```java
// Current: substring matching
"INSTALL".contains("STALL") → true ❌

// Better: word boundary check
Pattern.compile("\\bINSTALL\\b")
```

### 6. SYNONYM EXPANSION
```java
Map<String, String[]> synonyms = {
    "INSTALL": ["INSTALL", "SETUP", "ADD", "GET"],
    "REMOVE": ["REMOVE", "UNINSTALL", "DELETE", "PURGE"],
    "FIX": ["FIX", "REPAIR", "SOLVE", "RESOLVE"]
}
```

### 7. PATTERN WEIGHTING
```java
// Specific commands = high weight
"sudo apt install" → weight 1.5
"install" → weight 1.0

// Vague terms = low weight
"help" → weight 0.5
"what" → weight 0.3
```

### 8. SEMANTIC SIMILARITY (advanced)
```java
// Compare input to known patterns
float similarity = cosineSimilarity(userInput, knownPattern);
if (similarity > 0.7) score += 50;
```

### 9. LEARNING MECHANISM
```java
// Track successful responses
Map<String, Integer> responseSuccess;

void recordFeedback(String pattern, String response, boolean helpful) {
    if (helpful) {
        responseSuccess.put(pattern + ":" + response,
            responseSuccess.getOrDefault(...) + 1);
    }
}

// Prioritize successful responses
responses.sort((r1, r2) ->
    Integer.compare(
        responseSuccess.get(r2),
        responseSuccess.get(r1)
    )
);
```

### 10. COMPOSITE SCORING
```java
int finalScore =
    basePriority * 10 +           // Rule priority (0-100)
    specificityBonus * 5 +         // Word count bonus
    (1000 - position) +            // Earlier in text = better
    contextBonus * 3 +             // Related to last topic
    wordBoundaryBonus * 2 +        // Exact word match
    recencyBonus;                  // Recent successful pattern

// Sort all matches by finalScore
```

## Implementation Priority

1. **Multi-pattern matching** (easy, high impact)
2. **Scoring system** (medium, high impact)
3. **Specificity bonus** (easy, medium impact)
4. **Word boundaries** (easy, medium impact)
5. **Context tracking** (medium, high impact)
6. **Synonym expansion** (easy, medium impact)
7. **Pattern weighting** (easy, low impact)
8. **Semantic similarity** (hard, high impact)
9. **Learning mechanism** (hard, medium impact)

## Code Example - Improved Matcher

```java
class AdvancedMatcher {
    List<ScoredMatch> findMatches(String input) {
        List<ScoredMatch> matches = new ArrayList<>();

        for (Rule rule : rules) {
            for (String pattern : rule.getPatterns()) {
                Matcher m = Pattern.compile("\\b" + pattern + "\\b")
                                  .matcher(input);
                if (m.find()) {
                    int score = calculateScore(
                        rule.getPriority(),
                        m.start(),
                        pattern.split(" ").length,
                        context.isRelated(rule.getTopic())
                    );
                    matches.add(new ScoredMatch(rule, score, m.group()));
                }
            }
        }

        matches.sort((a, b) -> Integer.compare(b.score, a.score));
        return matches;
    }

    int calculateScore(int priority, int position, int wordCount, boolean related) {
        return priority * 10 +
               wordCount * 5 +
               (1000 - position) +
               (related ? 30 : 0);
    }
}
```

## Performance Impact

| Feature | Complexity | Speed Impact |
|---------|-----------|--------------|
| Multi-pattern | O(n*m) | +10ms |
| Scoring | O(n log n) | +5ms |
| Word boundaries | O(n*m) | +15ms |
| Semantic similarity | O(n*m*k) | +200ms |
| Context tracking | O(1) | +1ms |

**Total: ~230ms for complex queries (acceptable for interactive chat)**

## Migration Path

1. Keep ElizaEngine as-is (legacy psychotherapist mode)
2. Use UbuntuAssistant with improvements (technical support)
3. Add toggle: `useAdvancedMatching = true/false`
