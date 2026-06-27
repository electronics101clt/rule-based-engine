# Fuzzy Semantic Matching System Design

## Goal
Pre-LLM layer that understands natural language using:
- Large vocabulary (100K-300K words)
- Word embeddings for semantic similarity
- Fuzzy string matching for typos
- Intent classification

## Architecture

```
User Input
    ↓
┌─────────────────────────────┐
│ 1. FUZZY MATCHER            │
│  - Fix typos (Levenshtein)  │
│  - "instal" → "install"     │
└─────────────────────────────┘
    ↓
┌─────────────────────────────┐
│ 2. TOKENIZER                │
│  - Split into words         │
│  - Remove stopwords         │
│  - "how do I install"       │
│    → ["install"]            │
└─────────────────────────────┘
    ↓
┌─────────────────────────────┐
│ 3. SEMANTIC EMBEDDINGS      │
│  - Map words to vectors     │
│  - "install" → [0.2,...]    │
│  - "setup" → [0.21,...]     │
│  - Cosine similarity        │
└─────────────────────────────┘
    ↓
┌─────────────────────────────┐
│ 4. INTENT CLASSIFIER        │
│  - Categorize query type    │
│  - "install X" → SOFTWARE   │
│  - "fix Y" → TROUBLESHOOT   │
│  - "what is Z" → EXPLAIN    │
└─────────────────────────────┘
    ↓
┌─────────────────────────────┐
│ 5. PATTERN MATCHER          │
│  - Match against rules      │
│  - Use semantic scores      │
└─────────────────────────────┘
    ↓
┌─────────────────────────────┐
│ 6. CONFIDENCE CHECK         │
│  - score > 0.7 → ANSWER     │
│  - score < 0.7 → LLM        │
└─────────────────────────────┘
```

## Data Requirements

### Option 1: Small (50-100MB)
- **fastText vectors (compressed)**
- 100K vocabulary
- 100-dim embeddings
- Good for: common words, typos
- Coverage: ~95% English

### Option 2: Medium (300-500MB)
- **GloVe 6B (Wikipedia + Gigaword)**
- 400K vocabulary
- 100-300 dim embeddings
- Good for: technical terms, compounds
- Coverage: ~98% English

### Option 3: Large (1-2GB)
- **Word2Vec Google News**
- 3M vocabulary
- 300-dim embeddings
- Good for: everything, rare terms
- Coverage: ~99.5% English

### Option 4: Hybrid (200-400MB)
- **Custom Ubuntu/Linux corpus**
- 150K vocabulary (Ubuntu docs + man pages)
- 100-dim embeddings
- Good for: Linux-specific terms
- Coverage: 99% Ubuntu questions

## Fuzzy Matching

### Levenshtein Distance
```
"instal" vs "install" → distance = 1
"configurate" vs "configure" → distance = 1
"ubunto" vs "ubuntu" → distance = 1
```

Accept if distance ≤ 2 and similarity > 0.7

### N-gram Similarity
```
"grafics" → trigrams: [gra, raf, afi, fic, ics]
"graphics" → trigrams: [gra, rap, aph, phi, hic, ics]
Common: [gra, ics] = 2/6 = 0.33
```

### Phonetic Matching
```
Soundex("graphics") = G612
Soundex("grafics") = G612 → MATCH
```

## Semantic Similarity

### Word Embeddings
```java
Vector install = embeddings.get("install");
Vector setup = embeddings.get("setup");

cosineSimilarity(install, setup) = 0.85 (very similar)
cosineSimilarity(install, remove) = 0.45 (opposite)
```

### Sentence Embeddings
```
Input: "how to setup graphics driver"
Tokens: ["setup", "graphics", "driver"]
Embedding: average([vec_setup, vec_graphics, vec_driver])

Compare against patterns:
  "install nvidia driver" → similarity = 0.82
  "fix audio" → similarity = 0.23
```

## Intent Classification

### Categories
```
SOFTWARE_INSTALL: install, setup, add, get, download
SOFTWARE_REMOVE: remove, uninstall, delete, purge
CONFIGURATION: configure, change, set, adjust, modify
TROUBLESHOOT: fix, repair, solve, broken, error
INFORMATION: what, how, why, explain, tell
SYSTEM_STATUS: check, show, list, display, view
```

### Keyword Extraction
```
Input: "my wifi keeps disconnecting help"
Keywords: wifi, disconnecting
Intent: TROUBLESHOOT
Topic: NETWORK
Confidence: 0.85
```

## Implementation Strategy

### Phase 1: Fuzzy Matching (Easy - 10MB)
- Levenshtein distance
- Common typo dictionary
- ~1000 word corrections

### Phase 2: Basic Embeddings (Medium - 100MB)
- fastText 100K vocabulary
- 50-dim vectors
- Cosine similarity

### Phase 3: Intent Classification (Small - 5MB)
- Rule-based intent patterns
- Keyword → intent mapping
- Confidence scoring

### Phase 4: Full Semantic (Large - 500MB)
- GloVe 400K vocabulary
- 200-dim vectors
- Sentence-level similarity

### Phase 5: Custom Domain (Medium - 200MB)
- Train on Ubuntu docs
- Linux man pages
- Forum posts
- Stack Overflow Ubuntu tags

## File Structure

```
rule-based-engine/
├── embeddings/
│   ├── fasttext-100k.vec       (100MB)
│   ├── glove-400k.vec          (500MB)
│   ├── ubuntu-custom.vec       (200MB)
│   └── word2vec-google.bin     (1.5GB)
├── dictionaries/
│   ├── typos.txt               (1MB)
│   ├── synonyms.txt            (2MB)
│   ├── ubuntu-terms.txt        (500KB)
│   └── stopwords.txt           (10KB)
├── models/
│   ├── intent-classifier.dat   (5MB)
│   └── topic-classifier.dat    (5MB)
```

## Performance

### Without Embeddings (Current)
- Startup: 50ms
- Query: 5ms
- Memory: 20MB
- Match rate: 60%

### With Fuzzy + Small Embeddings
- Startup: 500ms
- Query: 20ms
- Memory: 150MB
- Match rate: 80%

### With Full Embeddings
- Startup: 2000ms
- Query: 50ms
- Memory: 800MB
- Match rate: 90%

## Example Queries

### Typo Correction
```
"instal chromium" → "install chromium"
"ubunto 24.04" → "ubuntu 24.04"
"grafics driver" → "graphics driver"
```

### Semantic Understanding
```
"setup my monitor"
  → similar to: "configure display"
  → match: display-configuration rule

"get rid of firefox"
  → similar to: "remove browser"
  → match: apt-remove rule

"computer running sluggish"
  → similar to: "system slow"
  → match: performance-troubleshooting rule
```

### Intent Detection
```
"wifi won't connect"
  Intent: TROUBLESHOOT
  Topic: NETWORK
  Match: wifi-troubleshooting

"what is wayland"
  Intent: INFORMATION
  Topic: DISPLAY
  Match: wayland-explanation

"install vscode"
  Intent: SOFTWARE_INSTALL
  Topic: DEVELOPMENT
  Match: apt-install + vscode
```

## Confidence Scoring

```java
float fuzzyScore = levenshteinSimilarity(input, pattern);    // 0-1
float semanticScore = cosineSimilarity(vec_input, vec_pattern); // 0-1
float intentScore = intentMatch ? 1.0 : 0.0;                 // 0 or 1
float contextScore = topicMatch(context, rule.topic);        // 0-1

float confidence =
    fuzzyScore * 0.2 +
    semanticScore * 0.4 +
    intentScore * 0.2 +
    contextScore * 0.2;

if (confidence > 0.7) {
    return ruleResponse;
} else {
    return llmResponse;
}
```

## Download Sources

### fastText
```bash
# Small compressed (100MB)
wget https://dl.fbaipublicfiles.com/fasttext/vectors-english/wiki-news-300d-1M-subword.vec.zip
```

### GloVe
```bash
# Medium (822MB uncompressed)
wget http://nlp.stanford.edu/data/glove.6B.zip
```

### Word2Vec
```bash
# Large (1.5GB)
wget https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz
```

### Custom Training
```bash
# Train on Ubuntu docs
git clone ubuntu/docs
./train-embeddings.sh docs/ > ubuntu-embeddings.vec
```
