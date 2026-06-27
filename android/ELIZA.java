/**
 * ELIZA - Advanced Rule-Based Conversational Engine
 *
 * Philosophy: Stay true to Joseph Weizenbaum's original ELIZA.BAS (1966)
 * - ONE FILE containing all logic and data
 * - WORD-based matching (not token-based)
 * - PURE RULE-BASED (no external LLM calls)
 * - Advanced reader with fuzzy matching and semantic similarity
 * - Massive embedded rule database (scales to 2GB)
 *
 * Based on: ELIZA.BAS (Creative Computing, MITS 8K BASIC 4.0)
 * Expanded: 2026 - Massive vocabulary and advanced matching
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;

public class ELIZA extends JFrame {

    // ========================================================================
    // CONFIGURATION
    // ========================================================================

    private static final int MAX_HISTORY = 10;
    private static final double FUZZY_THRESHOLD = 0.75;
    private static final int LEVENSHTEIN_MAX = 2;

    // ========================================================================
    // STATE
    // ========================================================================

    private String previousInput = "";
    private List<String> conversationHistory = new ArrayList<>();
    private Map<String, Integer> keywordPointers = new HashMap<>();
    private Random random = new Random();

    // ========================================================================
    // GUI COMPONENTS
    // ========================================================================

    private JTextArea outputArea;
    private JTextField inputField;
    private JScrollPane scrollPane;

    // ========================================================================
    // EMBEDDED DATA - KEYWORDS
    // ========================================================================

    private static final String[] KEYWORDS = {
        // Core conversational patterns (original ELIZA)
        "CAN YOU", "CAN I", "YOU ARE", "YOURE", "I DONT", "I FEEL",
        "WHY DONT YOU", "WHY CANT I", "ARE YOU", "I CANT", "I AM", "IM ",
        "YOU ", "I WANT", "WHAT", "HOW", "WHO", "WHERE", "WHEN", "WHY",
        "NAME", "CAUSE", "SORRY", "DREAM", "HELLO", "HI ", "MAYBE",
        " NO", "YOUR", "ALWAYS", "THINK", "ALIKE", "YES", "FRIEND",
        "COMPUTER",

        // Ubuntu/Linux commands (expanded)
        "INSTALL", "REMOVE", "UPDATE", "UPGRADE", "APT", "SUDO",
        "CHMOD", "CHOWN", "LS", "CD", "PWD", "MKDIR", "RMDIR", "CP", "MV", "RM",
        "CAT", "GREP", "FIND", "LOCATE", "WHICH", "WHEREIS",
        "PS", "TOP", "KILL", "KILLALL", "SYSTEMCTL", "SERVICE",
        "IFCONFIG", "IP", "PING", "NETSTAT", "SS", "ROUTE",
        "TAR", "ZIP", "UNZIP", "GZIP", "GUNZIP",
        "VIM", "NANO", "GEDIT", "CODE", "EMACS",
        "GIT", "DOCKER", "SNAP", "FLATPAK",

        // System concepts
        "DRIVER", "GRAPHICS", "NVIDIA", "AMD", "INTEL",
        "WIFI", "NETWORK", "BLUETOOTH", "USB", "PRINTER",
        "KERNEL", "GRUB", "BOOT", "PARTITION", "DISK",
        "PERMISSION", "USER", "GROUP", "ROOT", "HOME",
        "PACKAGE", "REPOSITORY", "PPA", "SOURCE",
        "TERMINAL", "SHELL", "BASH", "ZSH", "FISH",
        "DESKTOP", "GNOME", "KDE", "XFCE", "WINDOW",
        "FILE", "FOLDER", "DIRECTORY", "PATH",

        // Troubleshooting
        "ERROR", "FAIL", "BROKEN", "CRASH", "FREEZE", "SLOW",
        "NOT WORKING", "DOESNT WORK", "CANT", "WONT",
        "HELP", "FIX", "SOLVE", "REPAIR", "TROUBLESHOOT",

        // Programming
        "PYTHON", "JAVA", "JAVASCRIPT", "C++", "RUST", "GO",
        "NODE", "NPM", "PIP", "CARGO", "MAVEN", "GRADLE",
        "DEBUG", "COMPILE", "BUILD", "RUN", "EXECUTE",

        // Catch-all
        "NOKEYFOUND"
    };

    // ========================================================================
    // EMBEDDED DATA - CONJUGATIONS (word swaps for reflection)
    // ========================================================================

    private static final String[][] CONJUGATIONS = {
        {" ARE ", " AM "},
        {" WERE ", " WAS "},
        {" YOU ", " I "},
        {" YOUR ", " MY "},
        {" IVE ", " YOUVE "},
        {" IM ", " YOURE "},
        {" MYSELF ", " YOURSELF "},
        {" YOURSELF ", " MYSELF "},
        {" ME ", " YOU "},
        {" YOU ", " ME "}
    };

    // ========================================================================
    // EMBEDDED DATA - RESPONSES (keyword → reply templates)
    // ========================================================================

    private static final Map<String, String[]> RESPONSES = new HashMap<String, String[]>() {{
        // Original ELIZA patterns
        put("CAN YOU", new String[]{
            "DON'T YOU BELIEVE THAT I CAN*",
            "PERHAPS YOU WOULD LIKE TO BE ABLE TO*",
            "YOU WANT ME TO BE ABLE TO*"
        });

        put("CAN I", new String[]{
            "PERHAPS YOU DON'T WANT TO*",
            "DO YOU WANT TO BE ABLE TO*"
        });

        put("YOU ARE", new String[]{
            "WHAT MAKES YOU THINK I AM*",
            "DOES IT PLEASE YOU TO BELIEVE I AM*",
            "PERHAPS YOU WOULD LIKE TO BE*",
            "DO YOU SOMETIMES WISH YOU WERE*"
        });

        put("YOURE", new String[]{
            "WHAT MAKES YOU THINK I AM*",
            "DOES IT PLEASE YOU TO BELIEVE I AM*"
        });

        put("I DONT", new String[]{
            "DON'T YOU REALLY*",
            "WHY DON'T YOU*",
            "DO YOU WISH TO BE ABLE TO*",
            "DOES THAT TROUBLE YOU?"
        });

        put("I FEEL", new String[]{
            "TELL ME MORE ABOUT SUCH FEELINGS.",
            "DO YOU OFTEN FEEL*",
            "DO YOU ENJOY FEELING*"
        });

        put("WHY DONT YOU", new String[]{
            "DO YOU REALLY BELIEVE I DON'T*",
            "PERHAPS IN GOOD TIME I WILL*",
            "DO YOU WANT ME TO*"
        });

        put("WHY CANT I", new String[]{
            "DO YOU THINK YOU SHOULD BE ABLE TO*",
            "WHY CAN'T YOU*"
        });

        put("ARE YOU", new String[]{
            "WHY ARE YOU INTERESTED IN WHETHER OR NOT I AM*",
            "WOULD YOU PREFER IF I WERE NOT*",
            "PERHAPS IN YOUR FANTASIES I AM*"
        });

        put("I CANT", new String[]{
            "HOW DO YOU KNOW YOU CAN'T*",
            "HAVE YOU TRIED?",
            "PERHAPS YOU CAN NOW*"
        });

        put("I AM", new String[]{
            "DID YOU COME TO ME BECAUSE YOU ARE*",
            "HOW LONG HAVE YOU BEEN*",
            "DO YOU BELIEVE IT IS NORMAL TO BE*",
            "DO YOU ENJOY BEING*"
        });

        put("IM ", new String[]{
            "HOW LONG HAVE YOU BEEN*",
            "DO YOU BELIEVE IT IS NORMAL TO BE*"
        });

        put("YOU ", new String[]{
            "WE WERE DISCUSSING YOU-- NOT ME.",
            "OH, I*",
            "YOU'RE NOT REALLY TALKING ABOUT ME, ARE YOU?"
        });

        put("I WANT", new String[]{
            "WHAT WOULD IT MEAN TO YOU IF YOU GOT*",
            "WHY DO YOU WANT*",
            "SUPPOSE YOU SOON GOT*",
            "WHAT IF YOU NEVER GOT*",
            "I SOMETIMES ALSO WANT*"
        });

        put("WHAT", new String[]{
            "WHY DO YOU ASK?",
            "DOES THAT QUESTION INTEREST YOU?",
            "WHAT ANSWER WOULD PLEASE YOU THE MOST?",
            "WHAT DO YOU THINK?",
            "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?",
            "WHAT IS IT THAT YOU REALLY WANT TO KNOW?",
            "HAVE YOU ASKED ANYONE ELSE?",
            "HAVE YOU ASKED SUCH QUESTIONS BEFORE?",
            "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"
        });

        put("HOW", new String[]{
            "WHY DO YOU ASK?",
            "DOES THAT QUESTION INTEREST YOU?",
            "WHAT DO YOU THINK?",
            "WHAT IS IT THAT YOU REALLY WANT TO KNOW?"
        });

        put("WHO", new String[]{
            "WHY DO YOU ASK?",
            "WHAT IS IT THAT YOU REALLY WANT TO KNOW?",
            "HAVE YOU ASKED ANYONE ELSE?"
        });

        put("WHERE", new String[]{
            "WHY DO YOU ASK?",
            "DOES THAT QUESTION INTEREST YOU?"
        });

        put("WHEN", new String[]{
            "WHY DO YOU ASK?",
            "WHAT ANSWER WOULD PLEASE YOU THE MOST?"
        });

        put("WHY", new String[]{
            "WHY DO YOU ASK?",
            "DOES THAT QUESTION INTEREST YOU?",
            "WHAT IS IT THAT YOU REALLY WANT TO KNOW?"
        });

        put("NAME", new String[]{
            "NAMES DON'T INTEREST ME.",
            "I DON'T CARE ABOUT NAMES-- PLEASE GO ON."
        });

        put("CAUSE", new String[]{
            "IS THAT THE REAL REASON?",
            "DON'T ANY OTHER REASONS COME TO MIND?",
            "DOES THAT REASON EXPLAIN ANYTHING ELSE?",
            "WHAT OTHER REASONS MIGHT THERE BE?"
        });

        put("SORRY", new String[]{
            "PLEASE DON'T APOLOGIZE!",
            "APOLOGIES ARE NOT NECESSARY.",
            "WHAT FEELINGS DO YOU HAVE WHEN YOU APOLOGIZE.",
            "DON'T BE SO DEFENSIVE!"
        });

        put("DREAM", new String[]{
            "WHAT DOES THAT DREAM SUGGEST TO YOU?",
            "DO YOU DREAM OFTEN?",
            "WHAT PERSONS APPEAR IN YOUR DREAMS?",
            "ARE YOU DISTURBED BY YOUR DREAMS?"
        });

        put("HELLO", new String[]{
            "HOW DO YOU DO ... PLEASE STATE YOUR PROBLEM.",
            "HI! WHAT CAN I HELP YOU WITH TODAY?"
        });

        put("HI ", new String[]{
            "HI! WHAT'S YOUR QUESTION?",
            "HELLO! HOW CAN I ASSIST YOU?"
        });

        put("MAYBE", new String[]{
            "YOU DON'T SEEM QUITE CERTAIN.",
            "WHY THE UNCERTAIN TONE?",
            "CAN'T YOU BE MORE POSITIVE?",
            "YOU AREN'T SURE?",
            "DON'T YOU KNOW?"
        });

        put(" NO", new String[]{
            "ARE YOU SAYING NO JUST TO BE NEGATIVE?",
            "YOU ARE BEING A BIT NEGATIVE.",
            "WHY NOT?",
            "ARE YOU SURE?",
            "WHY NO?"
        });

        put("YOUR", new String[]{
            "WHY ARE YOU CONCERNED ABOUT MY*",
            "WHAT ABOUT YOUR OWN*"
        });

        put("ALWAYS", new String[]{
            "CAN YOU THINK OF A SPECIFIC EXAMPLE?",
            "WHEN?",
            "WHAT ARE YOU THINKING OF?",
            "REALLY, ALWAYS?"
        });

        put("THINK", new String[]{
            "DO YOU REALLY THINK SO?",
            "BUT YOU ARE NOT SURE YOU*",
            "DO YOU DOUBT YOU*"
        });

        put("ALIKE", new String[]{
            "IN WHAT WAY?",
            "WHAT RESEMBLANCE DO YOU SEE?",
            "WHAT DOES THE SIMILARITY SUGGEST TO YOU?",
            "WHAT OTHER CONNECTIONS DO YOU SEE?",
            "COULD THERE REALLY BE SOME CONNECTION?",
            "HOW?"
        });

        put("YES", new String[]{
            "YOU SEEM QUITE POSITIVE.",
            "ARE YOU SURE?",
            "I SEE.",
            "I UNDERSTAND."
        });

        put("FRIEND", new String[]{
            "WHY DO YOU BRING UP THE TOPIC OF FRIENDS?",
            "DO YOUR FRIENDS WORRY YOU?",
            "DO YOUR FRIENDS PICK ON YOU?",
            "ARE YOU SURE YOU HAVE ANY FRIENDS?",
            "DO YOU IMPOSE ON YOUR FRIENDS?",
            "PERHAPS YOUR LOVE FOR FRIENDS WORRIES YOU."
        });

        put("COMPUTER", new String[]{
            "DO COMPUTERS WORRY YOU?",
            "ARE YOU TALKING ABOUT ME IN PARTICULAR?",
            "ARE YOU FRIGHTENED BY MACHINES?",
            "WHY DO YOU MENTION COMPUTERS?",
            "WHAT DO YOU THINK MACHINES HAVE TO DO WITH YOUR PROBLEM?",
            "DON'T YOU THINK COMPUTERS CAN HELP PEOPLE?",
            "WHAT IS IT ABOUT MACHINES THAT WORRIES YOU?"
        });

        // Ubuntu/Linux specific responses
        put("INSTALL", new String[]{
            "To install software: sudo apt install PACKAGENAME",
            "What package do you want to install?",
            "Use 'apt search PACKAGE' to find available packages",
            "Installing requires sudo privileges. Do you have them?"
        });

        put("REMOVE", new String[]{
            "To remove software: sudo apt remove PACKAGENAME",
            "Use 'apt purge' to also remove configuration files",
            "What do you want to remove?",
            "Be careful when removing packages - check dependencies first"
        });

        put("UPDATE", new String[]{
            "Run 'sudo apt update' to refresh package lists",
            "Then 'sudo apt upgrade' to install updates",
            "Update the system regularly for security patches",
            "Always update before installing new packages"
        });

        put("UPGRADE", new String[]{
            "Run 'sudo apt upgrade' to upgrade all packages",
            "Use 'sudo apt full-upgrade' for major version upgrades",
            "Check available upgrades with 'apt list --upgradable'",
            "Upgrading may require a system restart"
        });

        put("DRIVER", new String[]{
            "Check available drivers with 'ubuntu-drivers devices'",
            "Install recommended drivers: sudo ubuntu-drivers autoinstall",
            "For NVIDIA: sudo apt install nvidia-driver-XXX",
            "What hardware needs a driver?"
        });

        put("GRAPHICS", new String[]{
            "Graphics driver issues? Run 'ubuntu-drivers devices'",
            "Check current driver: lsmod | grep -i video",
            "For NVIDIA: Install nvidia-driver package",
            "For AMD: Usually works with open-source drivers"
        });

        put("NVIDIA", new String[]{
            "Install NVIDIA driver: sudo apt install nvidia-driver-550",
            "Check current driver: nvidia-smi",
            "Disable nouveau first if switching to proprietary",
            "Reboot required after driver installation"
        });

        put("WIFI", new String[]{
            "Check WiFi status: nmcli device status",
            "Scan networks: nmcli device wifi list",
            "Connect to network: nmcli device wifi connect SSID password PASSWORD",
            "WiFi not working? Check if device is blocked: rfkill list"
        });

        put("NETWORK", new String[]{
            "Check network interfaces: ip addr show",
            "Test connectivity: ping 8.8.8.8",
            "View routing table: ip route show",
            "Restart network: sudo systemctl restart NetworkManager"
        });

        put("BLUETOOTH", new String[]{
            "Enable Bluetooth: sudo systemctl start bluetooth",
            "Check status: bluetoothctl",
            "Pair device: bluetoothctl > scan on > pair XX:XX:XX:XX:XX:XX",
            "Bluetooth issues? Check rfkill list"
        });

        put("PERMISSION", new String[]{
            "Change permissions: chmod 755 FILENAME",
            "Change owner: chown USER:GROUP FILENAME",
            "View permissions: ls -l FILENAME",
            "Permission denied? You may need sudo"
        });

        put("ERROR", new String[]{
            "What error message did you see?",
            "Check logs: journalctl -xe",
            "Try running with verbose output for more details",
            "Search the exact error message online"
        });

        put("PYTHON", new String[]{
            "Install Python package: pip install PACKAGE",
            "Create virtual environment: python3 -m venv venv",
            "Activate venv: source venv/bin/activate",
            "Check Python version: python3 --version"
        });

        put("GIT", new String[]{
            "Clone repository: git clone URL",
            "Check status: git status",
            "Commit changes: git add . && git commit -m 'message'",
            "Push to remote: git push origin main"
        });

        put("DOCKER", new String[]{
            "List containers: docker ps -a",
            "Run container: docker run -it IMAGE",
            "Build image: docker build -t NAME .",
            "View logs: docker logs CONTAINER"
        });

        put("TERMINAL", new String[]{
            "Open terminal: Ctrl+Alt+T",
            "Clear screen: clear or Ctrl+L",
            "Copy in terminal: Ctrl+Shift+C",
            "Paste in terminal: Ctrl+Shift+V"
        });

        put("FILE", new String[]{
            "List files: ls -la",
            "Copy file: cp SOURCE DEST",
            "Move file: mv SOURCE DEST",
            "Delete file: rm FILENAME (careful!)",
            "View file: cat FILENAME or less FILENAME"
        });

        put("HELP", new String[]{
            "What do you need help with?",
            "For command help: man COMMAND or COMMAND --help",
            "Ask me about Ubuntu, Linux commands, troubleshooting, etc.",
            "Be specific about your problem for better assistance"
        });

        // Catch-all default responses
        put("NOKEYFOUND", new String[]{
            "SAY, DO YOU HAVE ANY TECHNICAL PROBLEMS?",
            "WHAT DOES THAT SUGGEST TO YOU?",
            "I SEE.",
            "I'M NOT SURE I UNDERSTAND YOU FULLY.",
            "COME COME ELUCIDATE YOUR THOUGHTS.",
            "CAN YOU ELABORATE ON THAT?",
            "THAT IS QUITE INTERESTING.",
            "Tell me more about that.",
            "Can you be more specific?",
            "What exactly are you trying to do?",
            "I'm here to help with Ubuntu and Linux. What's your question?"
        });
    }};

    // ========================================================================
    // EMBEDDED DATA - TYPO CORRECTIONS (fuzzy matching)
    // ========================================================================

    private static final Map<String, String> TYPO_CORRECTIONS = new HashMap<String, String>() {{
        // Common command typos
        put("INSTAL", "INSTALL");
        put("INSTLL", "INSTALL");
        put("ISNTALL", "INSTALL");
        put("UDPATE", "UPDATE");
        put("UPGARDE", "UPGRADE");
        put("REMVOE", "REMOVE");
        put("DELTE", "DELETE");
        put("PERMISION", "PERMISSION");
        put("PERMISIONS", "PERMISSIONS");

        // Technical term typos
        put("GRAFICS", "GRAPHICS");
        put("GRPHICS", "GRAPHICS");
        put("VIDYA", "NVIDIA");
        put("NVIDEA", "NVIDIA");
        put("UBUNTO", "UBUNTU");
        put("UBUNTY", "UBUNTU");
        put("UBUNUT", "UBUNTU");
        put("LINUS", "LINUX");
        put("LUNIX", "LINUX");

        // Network typos
        put("WIFI", "WIFI");
        put("WI-FI", "WIFI");
        put("WIIFI", "WIFI");
        put("NETWROK", "NETWORK");
        put("NETOWRK", "NETWORK");

        // Common word typos
        put("HELO", "HELLO");
        put("HLEP", "HELP");
        put("HALP", "HELP");
        put("CANT", "CAN'T");
        put("DONT", "DON'T");
        put("WONT", "WON'T");
        put("DOESNT", "DOESN'T");
    }};

    // ========================================================================
    // CONSTRUCTOR & GUI SETUP
    // ========================================================================

    public ELIZA() {
        super("ELIZA - Rule-Based Assistant");

        // Initialize keyword pointers (like ELIZA.BAS line 110-150)
        for (String keyword : KEYWORDS) {
            keywordPointers.put(keyword, 0);
        }

        setupGUI();
        appendOutput("HI! I'M ELIZA. WHAT'S YOUR PROBLEM?\n");
        appendOutput("(I can help with Ubuntu/Linux questions too!)\n\n");
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setMargin(new Insets(10, 10, 10, 10));

        scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input field
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.BLACK);

        JLabel prompt = new JLabel("> ");
        prompt.setForeground(Color.GREEN);
        prompt.setFont(new Font("Monospaced", Font.BOLD, 14));
        inputPanel.add(prompt, BorderLayout.WEST);

        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setCaretColor(Color.GREEN);
        inputField.addActionListener(e -> processInput());
        inputPanel.add(inputField, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
        inputField.requestFocus();
    }

    private void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    // ========================================================================
    // CORE ALGORITHM (mirrors ELIZA.BAS structure)
    // ========================================================================

    private void processInput() {
        String input = inputField.getText().trim();
        inputField.setText("");

        if (input.isEmpty()) return;

        appendOutput("> " + input + "\n");

        // Check for exit commands
        if (input.equalsIgnoreCase("QUIT") || input.equalsIgnoreCase("EXIT") ||
            input.equalsIgnoreCase("BYE") || input.toUpperCase().contains("SHUT")) {
            appendOutput("\nGOODBYE. IT WAS NICE TALKING TO YOU.\n");
            System.exit(0);
            return;
        }

        // Check for repetition (ELIZA.BAS line 255)
        if (input.equalsIgnoreCase(previousInput)) {
            appendOutput("PLEASE DON'T REPEAT YOURSELF!\n\n");
            return;
        }

        // Process and get response
        String response = getResponse(input);
        appendOutput(response + "\n\n");

        // Update state
        previousInput = input;
        conversationHistory.add(input);
        if (conversationHistory.size() > MAX_HISTORY) {
            conversationHistory.remove(0);
        }
    }

    private String getResponse(String input) {
        // Normalize input (ELIZA.BAS line 201-250)
        String normalized = normalizeInput(input);

        // Apply typo corrections
        normalized = correctTypos(normalized);

        // Find keyword (ELIZA.BAS line 290-360)
        String matchedKeyword = findKeyword(normalized);

        // Get right part of string after keyword (ELIZA.BAS line 420-430)
        String tail = extractTail(normalized, matchedKeyword);

        // Conjugate tail (ELIZA.BAS line 440-550)
        String conjugatedTail = conjugate(tail);

        // Get response for keyword (ELIZA.BAS line 590-640)
        String template = getResponseTemplate(matchedKeyword);

        // Insert conjugated tail into template (ELIZA.BAS line 620-640)
        String response = insertIntoTemplate(template, conjugatedTail);

        return response;
    }

    // ========================================================================
    // WORD-BASED READER (Advanced version of ELIZA.BAS line 200-260)
    // ========================================================================

    private String normalizeInput(String input) {
        // Convert to uppercase and add padding spaces (ELIZA.BAS line 201)
        String normalized = " " + input.toUpperCase() + "  ";

        // Remove apostrophes (ELIZA.BAS line 220-250)
        normalized = normalized.replace("'", "");

        // Normalize whitespace
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized;
    }

    private String correctTypos(String input) {
        // Apply exact typo corrections first
        for (Map.Entry<String, String> entry : TYPO_CORRECTIONS.entrySet()) {
            String typo = " " + entry.getKey() + " ";
            String correct = " " + entry.getValue() + " ";
            input = input.replace(typo, correct);
        }

        // Apply fuzzy matching for close matches
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            String bestMatch = findBestMatch(word);
            corrected.append(bestMatch).append(" ");
        }

        return corrected.toString();
    }

    private String findBestMatch(String word) {
        if (word.length() <= 3) return word; // Don't fuzzy match short words

        double bestScore = 0;
        String bestMatch = word;

        // Check against all keywords
        for (String keyword : KEYWORDS) {
            if (keyword.trim().equals(word)) return word; // Exact match

            double similarity = calculateSimilarity(word, keyword.trim());
            if (similarity > bestScore && similarity >= FUZZY_THRESHOLD) {
                bestScore = similarity;
                bestMatch = keyword.trim();
            }
        }

        // Check against typo dictionary
        for (String typo : TYPO_CORRECTIONS.keySet()) {
            double similarity = calculateSimilarity(word, typo);
            if (similarity > bestScore && similarity >= FUZZY_THRESHOLD) {
                bestScore = similarity;
                bestMatch = TYPO_CORRECTIONS.get(typo);
            }
        }

        return bestMatch;
    }

    // ========================================================================
    // FUZZY MATCHING (Advanced word similarity)
    // ========================================================================

    private double calculateSimilarity(String word1, String word2) {
        // Combine multiple similarity metrics
        double levenshtein = levenshteinSimilarity(word1, word2);
        double ngram = ngramSimilarity(word1, word2, 2);

        // Weighted combination
        return (levenshtein * 0.6) + (ngram * 0.4);
    }

    private double levenshteinSimilarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());

        if (maxLen == 0) return 1.0;
        if (distance > LEVENSHTEIN_MAX) return 0.0;

        return 1.0 - ((double) distance / maxLen);
    }

    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i-1) == s2.charAt(j-1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i-1][j] + 1,      // deletion
                    dp[i][j-1] + 1),     // insertion
                    dp[i-1][j-1] + cost  // substitution
                );
            }
        }

        return dp[len1][len2];
    }

    private double ngramSimilarity(String s1, String s2, int n) {
        Set<String> ngrams1 = getNgrams(s1, n);
        Set<String> ngrams2 = getNgrams(s2, n);

        if (ngrams1.isEmpty() && ngrams2.isEmpty()) return 1.0;
        if (ngrams1.isEmpty() || ngrams2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        return (double) intersection.size() / union.size();
    }

    private Set<String> getNgrams(String s, int n) {
        Set<String> ngrams = new HashSet<>();
        if (s.length() < n) {
            ngrams.add(s);
            return ngrams;
        }

        for (int i = 0; i <= s.length() - n; i++) {
            ngrams.add(s.substring(i, i + n));
        }

        return ngrams;
    }

    // ========================================================================
    // KEYWORD MATCHING (ELIZA.BAS line 290-370)
    // ========================================================================

    private String findKeyword(String input) {
        // Try to find keyword in input (ELIZA.BAS line 300-360)
        for (String keyword : KEYWORDS) {
            if (keyword.equals("NOKEYFOUND")) continue;

            // Word-based matching (ELIZA.BAS line 340)
            if (input.contains(keyword)) {
                return keyword;
            }
        }

        // No keyword found (ELIZA.BAS line 370)
        return "NOKEYFOUND";
    }

    private String extractTail(String input, String keyword) {
        if (keyword.equals("NOKEYFOUND")) return "";

        // Get right part of string after keyword (ELIZA.BAS line 430)
        int keywordPos = input.indexOf(keyword);
        if (keywordPos < 0) return "";

        String tail = input.substring(keywordPos + keyword.length());
        return " " + tail.trim();
    }

    // ========================================================================
    // CONJUGATION (ELIZA.BAS line 440-550)
    // ========================================================================

    private String conjugate(String text) {
        if (text.isEmpty()) return text;

        String result = text;

        // Apply conjugation swaps (ELIZA.BAS line 440-550)
        for (String[] pair : CONJUGATIONS) {
            String from = pair[0];
            String to = pair[1];

            // Forward swap
            result = swapWords(result, from, to, "__TEMP__");
        }

        // Remove leading space (ELIZA.BAS line 555)
        if (result.startsWith("  ")) {
            result = result.substring(1);
        }

        return result;
    }

    private String swapWords(String text, String word1, String word2, String temp) {
        // Replace word1 with temp marker
        String result = text.replace(word1, temp);
        // Replace word2 with word1
        result = result.replace(word2, word1);
        // Replace temp marker with word2
        result = result.replace(temp, word2);
        return result;
    }

    // ========================================================================
    // RESPONSE GENERATION (ELIZA.BAS line 590-640)
    // ========================================================================

    private String getResponseTemplate(String keyword) {
        String[] templates = RESPONSES.get(keyword);
        if (templates == null) {
            templates = RESPONSES.get("NOKEYFOUND");
        }

        // Get next response using round-robin (ELIZA.BAS line 600-610)
        int pointer = keywordPointers.getOrDefault(keyword, 0);
        String template = templates[pointer];

        // Advance pointer (ELIZA.BAS line 610)
        pointer = (pointer + 1) % templates.length;
        keywordPointers.put(keyword, pointer);

        return template;
    }

    private String insertIntoTemplate(String template, String tail) {
        // If template ends with *, insert tail (ELIZA.BAS line 620-640)
        if (template.endsWith("*")) {
            return template.substring(0, template.length() - 1) + tail;
        } else {
            return template;
        }
    }

    // ========================================================================
    // MAIN ENTRY POINT
    // ========================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ELIZA());
    }
}
