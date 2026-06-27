# ELIZA - Pure Rule-Based Conversational Engine

**Philosophy**: Stay true to Joseph Weizenbaum's original ELIZA.BAS (1966)
**Approach**: ONE FILE + massive data, WORD-based matching, PURE RULES
**Flavors**: Ubuntu (2GB desktop) + Android (500MB mobile)

![ELIZA](eliza-ubuntu.png)

## 🎯 Choose Your Flavor

### 📱 [Android Flavor →](android/)
**Mobile Linux Assistant with Voice Commands**

- **Size**: 200-500 MB (APK-ready)
- **Memory**: 300 MB RAM
- **Features**: Voice input/output, offline, optimized vocabulary
- **Platform**: Android 7+ (phones/tablets)
- **Use Cases**: Mobile learning, Termux help, hands-free reference
- **Data**: Top 5K packages, essential commands, quick troubleshooting

[**Get Started with Android →**](android/README.md)

---

### 🖥️ [Ubuntu Flavor →](ubuntu/)
**Complete Desktop/Server Assistant - 2GB Vocabulary**

- **Size**: 2 GB vocabulary (maximum capacity)
- **Memory**: 2.5 GB RAM
- **Features**: All packages, full man pages, complete documentation
- **Platform**: Ubuntu/Linux desktop/server
- **Use Cases**: System admin, development, offline reference
- **Data**: 93K+ packages, full docs, all programming languages

[**Get Started with Ubuntu →**](ubuntu/README.md)

---

## Core Philosophy

True to ELIZA.BAS (1966):

1. **ONE FILE** - ELIZA.java contains all logic (~700 lines)
2. **WORD-BASED** - Exact word matching (ELIZA.BAS line 340: `IF MID$(I$,L,LEN(K$))=K$`)
3. **PURE RULES** - No AI/LLM calls, completely deterministic
4. **MASSIVE DATA** - Scale to 2GB vocabulary via external text files
5. **OFFLINE** - No network, no permissions, works anywhere

## Architecture

```
User Input
    ↓
Normalize & Fix Typos (Fuzzy Matching)
    ↓
Find Keyword (Word-Based Exact Match)
    ↓
Extract Tail (Text After Keyword)
    ↓
Conjugate (Swap Pronouns: YOU ↔ I)
    ↓
Get Template Response (Round-Robin)
    ↓
Insert Tail into Template
    ↓
Return Response
```

Same algorithm as ELIZA.BAS (lines 290-640), enhanced with:
- Fuzzy matching (Levenshtein + N-gram similarity)
- Word prediction (bigrams/trigrams)
- Massive vocabulary (2GB capability)

## Comparison: Android vs Ubuntu

| Feature | Android Flavor | Ubuntu Flavor |
|---------|----------------|---------------|
| **Vocabulary** | 200-500 MB | 2 GB |
| **Packages** | Top 5K common | All 93K+ |
| **Man Pages** | Summaries | Full content |
| **Memory** | 300 MB | 2.5 GB |
| **Startup** | <1 second | 3-5 seconds |
| **APK Size** | 250-550 MB | N/A |
| **Voice** | Built-in | Optional |
| **Platform** | Android 7+ | Ubuntu/Linux |
| **Use Case** | Mobile learning | Desktop/server reference |

## Quick Start

### Android
```bash
cd android/
# See android/README.md for Android Studio setup
```

### Ubuntu
```bash
cd ubuntu/
javac ELIZA.java
java -Xmx3G ELIZA
```

## Data Files (Per Flavor)

Both flavors use same format, different sizes:

- **ELIZA_DATA.txt** - Keyword→response lookup (tab-delimited)
- **WORD_BIGRAMS.txt** - Word1 → Word2 predictions
- **WORD_TRIGRAMS.txt** - 3-word sequence patterns
- **COMMAND_SEQUENCES.txt** - Common command phrases

## What Makes This ELIZA

```
Original ELIZA.BAS (1966):          This Implementation:
========================            ====================
36 keywords                    →    100,000+ keywords (Ubuntu)
112 responses                  →    10,000,000+ responses (Ubuntu)
6 conjugation rules            →    10 conjugation rules + fuzzy
One BASIC file (~5KB)          →    One Java file + data (2GB capable)
Word matching                  →    Word matching + prediction
Template responses             →    Templates + massive data
Psychotherapy                  →    Linux/Android assistance
```

## Example

```
Input:  "instal grafics driver" (typos)
Fixed:  "install graphics driver"
Match:  GRAPHICS keyword
Tail:   " driver"
Response: "Install NVIDIA driver: sudo apt install nvidia-driver-550"
```

## Repository Structure

```
rule-based-engine/
├── README.md (this file)
├── ELIZA.BAS (original 1966 reference)
├── android/
│   ├── README.md
│   ├── ELIZA.java (same file as ubuntu)
│   └── data/ (mobile-optimized, 200-500 MB)
├── ubuntu/
│   ├── README.md
│   ├── ELIZA.java (same file as android)
│   └── data/ (full vocabulary, 2 GB)
├── dictionaries/ (source vocabulary)
└── src/main/java/com/eliza/ (old multi-file version on master branch)
```

## Branches

- **master**: Old multi-file architecture (v2.1.0, with LLM)
- **eliza-pure-v3**: New single-file pure ELIZA (v3.0.0, this branch)

## Philosophy: Why Pure Rules?

**ELIZA.BAS was revolutionary because**:
- Simple pattern matching felt intelligent
- No AI needed - just clever rules
- User's mind filled in the gaps
- Completely transparent

**This stays true by**:
- NO black-box AI/LLM calls
- Every response traceable to a rule
- Massive data ≠ complexity
- Works offline forever

## Building

### Compile
```bash
javac ELIZA.java
```

### Run (Ubuntu flavor)
```bash
java -Xmx3G ELIZA
```

### Create JAR
```bash
jar cfe ELIZA.jar ELIZA ELIZA.class
java -Xmx3G -jar ELIZA.jar
```

## Credits

- **Original ELIZA**: Joseph Weizenbaum (1966)
- **ELIZA.BAS**: Jeff Shrager, Steve North, Creative Computing
- **This Implementation**: Electronics101 (2026)
  - Built with Claude Code
  - True to ELIZA.BAS philosophy
  - Dual-platform (Android + Ubuntu)

## License

MIT License

## Contributing

Contributions welcome! Please:
1. Stay true to ELIZA.BAS philosophy (pure rules, no AI)
2. Expand vocabulary files
3. Keep one-file architecture
4. Support both flavors

## Links

- **Repository**: https://github.com/electronics101clt/rule-based-engine
- **Original ELIZA**: See `ELIZA.BAS` in this repo
- **ELIZA History**: https://en.wikipedia.org/wiki/ELIZA

---

**Version**: 3.0.0 - Pure ELIZA Dual-Platform
**Branch**: eliza-pure-v3
**Flavors**: Android (500MB) + Ubuntu (2GB)
**Philosophy**: Pure rule-based, true to ELIZA.BAS
**Last Updated**: 2026-06-27
