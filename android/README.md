# ELIZA - Android Flavor (Mobile)

**Target**: Android phones and tablets
**Vocabulary Size**: 200-500 MB (optimized for mobile)
**Philosophy**: Lightweight offline assistant with essential vocabulary

## Features

### Optimized Mobile Vocabulary
- **Common packages**: Top 5,000 most-used (~50 MB)
- **Essential commands**: Core Linux/Android commands
- **Programming basics**: Common languages, snippets
- **Quick troubleshooting**: Most frequent errors
- **Voice-first**: Designed for speech input/output

### Android-Specific
- Termux commands and usage
- ADB troubleshooting
- Android app management
- Mobile development (Android Studio, React Native)
- Device-specific tips (rooting, custom ROMs)

## Quick Start - Android Studio

1. **Create new Android project**
2. **Copy ELIZA.java** to `app/src/main/java/com/yourapp/`
3. **Create assets/ folder**: `app/src/main/assets/`
4. **Add data files** (generate with `../ubuntu/data/generate_mobile_vocab.sh`)
5. **Build APK**

## Memory Requirements

- **Minimum**: 100 MB RAM
- **Recommended**: 300 MB RAM
- **APK Size**: 250-550 MB (with data)
- **Runtime**: 200-400 MB

## Data Files

Located in `app/src/main/assets/`:
- `ELIZA_DATA.txt` - Core responses (~100 MB)
- `WORD_BIGRAMS.txt` - Word predictions (~80 MB)
- `WORD_TRIGRAMS.txt` - 3-word patterns (~20 MB)
- `COMMAND_SEQUENCES.txt` - Mobile-optimized (~10 MB)

## Loading Data in Android

```java
// In your Activity
InputStream is = getAssets().open("ELIZA_DATA.txt");
BufferedReader reader = new BufferedReader(new InputStreamReader(is));
// Parse and load into ELIZA
```

## Voice Commands (Built-in)

Android flavor includes voice recognition:

```java
// Use Android SpeechRecognizer API
SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this);
Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
recognizer.startListening(intent);
```

## Permissions Required

**AndroidManifest.xml**:
```xml
<!-- For voice input -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- NO network permissions needed - works completely offline -->
```

## Performance

- **Startup**: <1 second (loading 200-500 MB data)
- **Query**: ~20ms average
- **Memory**: 200-400 MB
- **Battery**: Minimal impact (no network, no background services)

## Use Cases

1. **Mobile Linux Learning**: Learn commands on the go
2. **Termux Assistant**: Help with Termux environment
3. **Quick Reference**: Fast command lookup
4. **Voice-Activated**: Hands-free Linux help
5. **Offline**: Works without internet

## Comparison to Ubuntu Flavor

| Feature | Android Flavor | Ubuntu Flavor |
|---------|----------------|---------------|
| Vocabulary | 200-500 MB | 2 GB |
| Packages | Top 5K common | All 93K+ |
| Man pages | Summaries | Full content |
| Memory | 300 MB | 2.5 GB |
| Startup | <1 sec | 3-5 sec |
| Voice | Built-in | Optional |
| Platform | Android 7+ | Ubuntu/Linux |

## Building APK

```bash
# Android Studio
1. File → New → New Project
2. Copy ELIZA.java to app/src/main/java/
3. Generate mobile data: cd android/data && ./generate_mobile_vocab.sh
4. Copy data/*.txt to app/src/main/assets/
5. Build → Build APK

# Command line (with Android SDK)
./gradlew assembleRelease
```

## Integration Example

```java
public class MainActivity extends AppCompatActivity {
    private ELIZA eliza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eliza = new ELIZA(this); // Pass context
        eliza.loadDataFromAssets(); // Load vocabulary
    }

    public void onVoiceInput(String text) {
        String response = eliza.getResponse(text);
        speakResponse(response); // Text-to-speech
    }
}
```

## Minimum Android Version

- **Target SDK**: 33 (Android 13)
- **Min SDK**: 24 (Android 7.0)

## Download

APK will be available in GitHub Releases once built.

---

**Version**: 3.0.0-android
**Data Size**: 200-500 MB (optimized)
**Last Updated**: 2026-06-27
**Voice**: Yes (built-in)
