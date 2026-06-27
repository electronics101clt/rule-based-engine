# ELIZA - Ubuntu Flavor (Desktop/Server)

**Target**: Ubuntu/Linux desktop and server environments
**Vocabulary Size**: 2 GB (maximum capacity)
**Philosophy**: Complete offline assistant with massive vocabulary

## Features

### Full 2GB Vocabulary
- **All Ubuntu packages**: Complete repository (93,411+ packages)
- **Man pages**: Full content, not just references
- **Documentation**: Ubuntu docs, forum posts, Stack Overflow
- **Programming**: Standard libraries for 10+ languages
- **Troubleshooting**: Complete error message database
- **Configuration**: All config files and options

### Ubuntu-Specific
- Desktop environment commands (GNOME, KDE, XFCE, etc.)
- Server administration (systemd, networking, services)
- Development tools (compilers, IDEs, build systems)
- Package management (apt, snap, flatpak)
- Hardware drivers (NVIDIA, AMD, Intel, etc.)

## Quick Start

```bash
# Compile
javac ELIZA.java

# Run
java -Xmx3G ELIZA
```

## Memory Requirements

- **Minimum**: 512 MB RAM
- **Recommended**: 3 GB RAM (for 2 GB data + JVM overhead)
- **Disk Space**: 2.5 GB

## Data Files

Located in `ubuntu/data/`:
- `ELIZA_DATA.txt` - Main keyword→response database (~1.5 GB)
- `WORD_BIGRAMS.txt` - Word prediction table (~300 MB)
- `WORD_TRIGRAMS.txt` - 3-word patterns (~100 MB)
- `COMMAND_SEQUENCES.txt` - Common phrases (~100 MB)

## Generating Full 2GB Vocabulary

```bash
cd ubuntu/data
../generate_full_vocab.sh  # Expands to 2 GB
```

## Performance

- **Startup**: ~3-5 seconds (loading 2 GB data)
- **Query**: ~50ms average
- **Memory**: ~2.5 GB resident
- **CPU**: Single-threaded, low usage

## Use Cases

1. **Offline Linux Assistant**: No internet required
2. **Server Administration**: SSH into server, run ELIZA
3. **Learning Ubuntu**: Interactive command reference
4. **Troubleshooting**: Error message lookup
5. **Development**: Programming language reference

## Comparison to Android Flavor

| Feature | Ubuntu Flavor | Android Flavor |
|---------|---------------|----------------|
| Vocabulary | 2 GB | 200-500 MB |
| Packages | All 93K+ | Most common (~5K) |
| Man pages | Full content | Summaries only |
| Memory | 2.5 GB | 300 MB |
| Startup | 3-5 sec | <1 sec |
| Use case | Desktop/server | Mobile assistant |

## Architecture

Same single-file ELIZA.java as Android, but with:
- Larger data files
- More comprehensive responses
- Complete documentation

## Building

```bash
# Compile single file
javac ELIZA.java

# Run with adequate memory
java -Xmx3G ELIZA

# Or create JAR
jar cfe ELIZA-ubuntu.jar ELIZA ELIZA.class
java -Xmx3G -jar ELIZA-ubuntu.jar
```

## Voice Commands (Optional)

Install speech recognition on Ubuntu:
```bash
sudo apt install speech-dispatcher espeak
```

Then modify ELIZA.java to add voice input via `speech-dispatcher`.

---

**Version**: 3.0.0-ubuntu
**Data Size**: 2 GB (target)
**Last Updated**: 2026-06-27
