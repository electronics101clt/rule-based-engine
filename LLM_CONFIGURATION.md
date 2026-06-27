# LLM Configuration Guide

## Configuration File Location

**Path:** `~/.ubuntu-assistant/config.json`
**Full Path:** `/home/jonathan/.ubuntu-assistant/config.json`

Similar to openclaw's `~/.openclaw/openclaw.json`

## Setup Instructions

### 1. Open Settings Dialog
- Launch Ubuntu Assistant: `java -jar ELIZA.jar`
- Click **Settings → LLM Configuration...**

### 2. Configure LLM Provider

**Available Providers:**
- **Claude** (Anthropic API)
- **OpenAI** (GPT API)
- **Gemini** (Google API)
- **Ollama** (Local LLM)

### 3. Enter Credentials

**For Claude:**
```
Provider: claude
Model: claude-sonnet-4-5-20250929 (default)
API Key: sk-ant-api03-...
```

**For OpenAI:**
```
Provider: openai
Model: gpt-4 (default)
API Key: sk-...
```

**For Gemini:**
```
Provider: gemini
Model: gemini-2.5-flash (default)
API Key: AIza...
```

**For Ollama (Local):**
```
Provider: ollama
Model: llama2 (default)
Endpoint: http://localhost:11434
```

### 4. Enable LLM Fallback

Check the **"Enable LLM Fallback"** checkbox to activate LLM responses when rule-based matching fails.

### 5. Save Settings

Click **Save** button. Settings are persisted to `~/.ubuntu-assistant/config.json`

## Config File Structure

```json
{
  "meta": {
    "version": "1.0.0",
    "lastModified": "Sat Jun 27 03:17:56 AST 2026"
  },
  "llm": {
    "enabled": true,
    "provider": "claude",
    "model": "claude-sonnet-4-5-20250929"
  },
  "providers": {
    "claude": {
      "apiKey": "sk-ant-api03-...",
      "defaultModel": "claude-sonnet-4-5-20250929"
    },
    "openai": {
      "apiKey": "sk-...",
      "defaultModel": "gpt-4"
    },
    "gemini": {
      "apiKey": "AIza...",
      "defaultModel": "gemini-2.5-flash"
    },
    "ollama": {
      "endpoint": "http://localhost:11434",
      "defaultModel": "llama2"
    }
  }
}
```

## How It Works

1. **Rule-Based First:** Assistant searches 1000+ rules for pattern matches
2. **LLM Fallback:** If no rule matches and LLM is enabled, query LLM provider
3. **Marked Responses:** LLM responses prefixed with 🤖 emoji
4. **Graceful Degradation:** Falls back to default responses if LLM fails

## Getting API Keys

### Claude (Anthropic)
1. Visit: https://console.anthropic.com/
2. Sign up or login
3. Navigate to API Keys
4. Create new key
5. Copy key (starts with `sk-ant-api03-`)

### OpenAI
1. Visit: https://platform.openai.com/api-keys
2. Sign up or login
3. Create new secret key
4. Copy key (starts with `sk-`)

### Gemini (Google)
1. Visit: https://aistudio.google.com/app/apikey
2. Sign in with Google account
3. Create API key
4. Copy key (starts with `AIza`)

### Ollama (Local)
1. Install: https://ollama.ai/
2. Run: `ollama serve`
3. Pull model: `ollama pull llama2`
4. Default endpoint: `http://localhost:11434`

## Code Implementation

**Config Class:** `src/main/java/com/eliza/LLMConfig.java`
**Provider Class:** `src/main/java/com/eliza/LLMProvider.java`
**Settings Dialog:** `src/main/java/com/eliza/SettingsDialog.java`
**Integration:** `src/main/java/com/eliza/UbuntuAssistant.java`

## Security Notes

- API keys stored in plain text in config file
- Config file location: `~/.ubuntu-assistant/config.json`
- Set file permissions: `chmod 600 ~/.ubuntu-assistant/config.json`
- Never commit config file to git
- Config directory is git-ignored

## Troubleshooting

**LLM not responding:**
1. Check API key is correct
2. Verify provider is selected
3. Ensure LLM is enabled
4. Check internet connection (for cloud APIs)
5. Verify Ollama is running (for local)

**Config not saving:**
1. Check directory permissions: `ls -la ~/.ubuntu-assistant/`
2. Ensure disk space available
3. Check console for errors

**API errors:**
- Invalid key: Re-enter API key
- Rate limit: Wait and retry
- Network error: Check internet connection
- Model not found: Verify model name

## Example Usage

**Question not in rules:**
```
User: "What's the difference between ZFS and BTRFS?"
Assistant: 🤖 ZFS and BTRFS are both advanced filesystems...
```

**Question in rules:**
```
User: "How do I install NVIDIA drivers?"
Assistant: ⚠️ NVIDIA on Linux requires proprietary drivers for best performance.
(No 🤖 prefix - answered by rules)
```

## Configuration Defaults

| Provider | Default Model | Endpoint |
|----------|--------------|----------|
| Claude | claude-sonnet-4-5-20250929 | https://api.anthropic.com/v1/messages |
| OpenAI | gpt-4 | https://api.openai.com/v1/chat/completions |
| Gemini | gemini-2.5-flash | https://generativelanguage.googleapis.com/v1beta/models/ |
| Ollama | llama2 | http://localhost:11434 |

## Repository

**GitHub:** https://github.com/electronics101clt/rule-based-engine
**Commit:** 3c9fb87 - "Add LLM integration with multi-provider support"
