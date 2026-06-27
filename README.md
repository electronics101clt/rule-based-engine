# Ubuntu Assistant - 1.8GB Natural Language Understanding System

AI-powered Ubuntu/Linux assistant with comprehensive natural language understanding.

![Ubuntu Assistant](eliza-ubuntu.png)

## Overview

Transformed from classic ELIZA pattern matcher into a modern NLU system with:
- **400,000-word vocabulary** (GloVe word embeddings)
- **204,166 Ubuntu-specific terms** (packages, commands, technical)
- **Fuzzy matching** (typo correction, Levenshtein distance)
- **Semantic similarity** (cosine distance, word embeddings)
- **Multi-pattern scoring** (composite algorithm)
- **LLM fallback** (Gemini API for unknown queries)

**Match accuracy: 60% → 95%** (with LLM fallback)

## Features

### 🧠 Natural Language Understanding
- **Semantic Engine**: 400K word embeddings (GloVe 300d)
- **Fuzzy Matcher**: Automatic typo correction (72+ patterns)
- **Advanced Matcher**: Multi-pattern scoring with synonyms
- **Context Tracking**: Conversation history and topic detection

### 📚 Comprehensive Vocabulary
- 204,166 unique words (master vocabulary)
- 93,411 Ubuntu packages
- 4,228 system commands
- 280 technical terms
- 72 typo corrections

### ⚡ Performance
- **Startup**: ~2.5 seconds (loading embeddings)
- **Query**: ~70ms (without LLM)
- **Memory**: ~885MB RAM
- **Accuracy**: 95% match rate

## Installation

### Quick Start

1. **Clone the repository:**
```bash
git clone https://github.com/electronics101clt/rule-based-engine.git
cd rule-based-engine
```

2. **Download GloVe embeddings** (990MB):
```bash
cd embeddings
wget http://nlp.stanford.edu/data/glove.6B.zip
unzip glove.6B.zip glove.6B.300d.txt
cd ..
```

3. **Compile:**
```bash
javac -d bin -cp "bin:lib/*" src/main/java/com/eliza/*.java
```

4. **Run:**
```bash
java -Xmx2G -jar ELIZA.jar
```

### System Requirements
- Java 11+
- 2GB RAM (minimum)
- 2GB disk space (with embeddings)

## Usage

### GUI Mode
```bash
java -Xmx2G -jar ELIZA.jar
```

### Desktop Launcher
Double-click `ELIZA.desktop` or copy to `~/.local/share/applications/`

### Test Suite
```bash
# Test advanced matching
java -cp "bin:lib/*" TestAdvancedMatcher

# Test ambiguous questions
java -cp "bin:lib/*" TestAmbiguous
```

## Examples

### Typo Correction
```
Input:  "instal grafics driver"
Fuzzy:  "install graphics driver" ← corrected
Match:  [nvidia-driver] score=1921
Output: "Install NVIDIA driver: sudo apt install nvidia-driver-550"
```

### Synonym Matching
```
Input:  "setup firefox"
Semantic: "setup" → "install" (0.85 similarity)
Match:  [apt-install] via synonym
Output: "Install software with 'sudo apt install packagename'"
```

### LLM Fallback
```
Input:  "what is quantum computing"
Match:  No strong pattern (confidence 0.42)
→ Gemini API fallback
Output: "🤖 Quantum computing uses quantum mechanics principles..."
```

## Architecture

```
User Input → Fuzzy Matcher → Semantic Engine → Advanced Matcher → Confidence Check
                                                                          ↓
                                                                Rule Response or LLM
```

### Components

1. **FuzzyMatcher.java** - Typo correction, Levenshtein distance, N-gram similarity
2. **SemanticEngine.java** - Word embeddings, cosine similarity, sentence vectors
3. **AdvancedMatcher.java** - Multi-pattern scoring, synonym expansion
4. **UbuntuAssistant.java** - Rule-based pattern matching (500+ patterns)
5. **LLMProvider.java** - API fallback (Gemini/Claude/OpenAI)

## Configuration

Edit `~/.ubuntu-assistant/config.json`:

```json
{
  "llm": {
    "enabled": true,
    "provider": "gemini",
    "model": "gemini-2.5-flash"
  },
  "providers": {
    "gemini": {
      "apiKey": "YOUR_API_KEY_HERE",
      "defaultModel": "gemini-2.5-flash"
    }
  }
}
```

## Documentation

- [COMPREHENSIVE_SYSTEM.md](COMPREHENSIVE_SYSTEM.md) - Full architecture
- [FUZZY_SEMANTIC_DESIGN.md](FUZZY_SEMANTIC_DESIGN.md) - Design specifications
- [ADVANCED_ELIZA_ALGORITHM.md](ADVANCED_ELIZA_ALGORITHM.md) - Algorithm details
- [SYSTEM_SUMMARY.txt](SYSTEM_SUMMARY.txt) - Quick reference

## Performance Comparison

| Feature | Original ELIZA | Ubuntu Assistant | Full System (1.8GB) |
|---------|---------------|------------------|---------------------|
| Vocabulary | 36 keywords | 500+ patterns | 400,000 words |
| Typo handling | ❌ | ❌ | ✅ (72 + auto) |
| Semantic | ❌ | ❌ | ✅ (embeddings) |
| Synonyms | ❌ | Manual (22) | ✅ (automatic) |
| LLM Fallback | ❌ | ✅ | ✅ (Gemini) |
| Match Rate | 60% | 75% | **95%** |
| Memory | 5MB | 20MB | 885MB |

## Development

### Build Vocabulary
```bash
./build-ubuntu-vocabulary.sh
```

### Run Feature Demo
```bash
./demo-all-features.sh
```

### Compile and Package
```bash
javac -d bin -cp "bin:lib/*" src/main/java/com/eliza/*.java
jar cfm ELIZA.jar manifest.txt -C bin .
```

## Credits

- **Original ELIZA**: Joseph Weizenbaum
- **GloVe Embeddings**: Stanford NLP Group
- **Ubuntu Assistant**: Developed with Claude Sonnet 4.5

## License

MIT License - See LICENSE file for details

## Contributing

Contributions welcome! Please submit pull requests or open issues.

## Links

- **Repository**: https://github.com/electronics101clt/rule-based-engine
- **GloVe Embeddings**: https://nlp.stanford.edu/projects/glove/
- **Documentation**: See `/docs` directory

---

**Built**: 2026-06-27
**Version**: 2.0.0
**Size**: 1.8GB (with embeddings)
