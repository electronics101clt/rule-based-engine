package com.eliza;

import java.util.ArrayList;
import java.util.List;

/**
 * ELIZA/DOCTOR
 * CREATED BY JOSEPH WEIZENBAUM
 * THIS VERSION BY JEFF SHRAGER
 * EDITED AND MODIFIED FOR MITS 8K BASIC 4.0 BY STEVE NORTH
 * CREATIVE COMPUTING PO BOX 789-M MORRISTOWN NJ 07960
 *
 * Converted to Java from BASIC
 */
public class ElizaEngine {

    private static final int N1 = 36; // Number of keywords
    private static final int N2 = 12; // Number of conjugation pairs

    private String[] keywords;
    private String[][] conjugations;
    private String[][] replies;
    private int[] replyStart;
    private int[] replyLength;
    private int[] replyIndex;
    private String previousInput = "";

    public ElizaEngine() {
        initializeData();
    }

    private void initializeData() {
        // Initialize keywords
        keywords = new String[] {
            "CAN YOU", "CAN I", "YOU ARE", "YOURE", "I DONT", "I FEEL",
            "WHY DONT YOU", "WHY CANT I", "ARE YOU", "I CANT", "I AM", "IM ",
            "YOU ", "I WANT", "WHAT", "HOW", "WHO", "WHERE", "WHEN", "WHY",
            "NAME", "CAUSE", "SORRY", "DREAM", "HELLO", "HI ", "MAYBE",
            " NO", "YOUR", "ALWAYS", "THINK", "ALIKE", "YES", "FRIEND",
            "COMPUTER", "NOKEYFOUND"
        };

        // Initialize conjugation pairs
        conjugations = new String[][] {
            {" ARE ", " AM "}, {"WERE ", "WAS "}, {" YOU ", " I "},
            {"YOUR ", "MY "}, {" IVE ", " YOUVE "}, {" IM ", " YOURE "}
        };

        // Initialize replies
        List<String> replyList = new ArrayList<>();

        // CAN YOU (3 replies)
        replyList.add("DON'T YOU BELIEVE THAT I CAN*");
        replyList.add("PERHAPS YOU WOULD LIKE TO BE ABLE TO*");
        replyList.add("YOU WANT ME TO BE ABLE TO*");

        // CAN I (2 replies)
        replyList.add("PERHAPS YOU DON'T WANT TO*");
        replyList.add("DO YOU WANT TO BE ABLE TO*");

        // YOU ARE (4 replies)
        replyList.add("WHAT MAKES YOU THINK I AM*");
        replyList.add("DOES IT PLEASE YOU TO BELIEVE I AM*");
        replyList.add("PERHAPS YOU WOULD LIKE TO BE*");
        replyList.add("DO YOU SOMETIMES WISH YOU WERE*");

        // YOURE (2 replies)
        replyList.add("DON'T YOU REALLY*");
        replyList.add("WHY DON'T YOU*");

        // I DONT (4 replies)
        replyList.add("DO YOU WISH TO BE ABLE TO*");
        replyList.add("DOES THAT TROUBLE YOU?");
        replyList.add("TELL ME MORE ABOUT SUCH FEELINGS.");
        replyList.add("DO YOU OFTEN FEEL*");

        // I FEEL (4 replies)
        replyList.add("DO YOU ENJOY FEELING*");
        replyList.add("DO YOU REALLY BELIEVE I DON'T*");
        replyList.add("PERHAPS IN GOOD TIME I WILL*");
        replyList.add("DO YOU WANT ME TO*");

        // WHY DONT YOU (3 replies)
        replyList.add("DO YOU THINK YOU SHOULD BE ABLE TO*");
        replyList.add("WHY CAN'T YOU*");
        replyList.add("WHY ARE YOU INTERESTED IN WHETHER OR NOT I AM*");

        // WHY CANT I (3 replies)
        replyList.add("WOULD YOU PREFER IF I WERE NOT*");
        replyList.add("PERHAPS IN YOUR FANTASIES I AM*");
        replyList.add("HOW DO YOU KNOW YOU CAN'T*");

        // ARE YOU (2 replies)
        replyList.add("HAVE YOU TRIED?");
        replyList.add("PERHAPS YOU CAN NOW*");

        // I CANT (3 replies)
        replyList.add("DID YOU COME TO ME BECAUSE YOU ARE*");
        replyList.add("HOW LONG HAVE YOU BEEN*");
        replyList.add("DO YOU BELIEVE IT IS NORMAL TO BE*");

        // I AM (5 replies)
        replyList.add("DO YOU ENJOY BEING*");
        replyList.add("WE WERE DISCUSSING YOU-- NOT ME.");
        replyList.add("OH, I*");
        replyList.add("YOU'RE NOT REALLY TALKING ABOUT ME, ARE YOU?");
        replyList.add("WHAT WOULD IT MEAN TO YOU IF YOU GOT*");

        // IM (4 replies - same as I AM but different index)
        replyList.add("WHY DO YOU WANT*");
        replyList.add("SUPPOSE YOU SOON GOT*");
        replyList.add("WHAT IF YOU NEVER GOT*");
        replyList.add("I SOMETIMES ALSO WANT*");

        // YOU (4 replies)
        replyList.add("WHY DO YOU ASK?");
        replyList.add("DOES THAT QUESTION INTEREST YOU?");
        replyList.add("WHAT ANSWER WOULD PLEASE YOU THE MOST?");
        replyList.add("WHAT DO YOU THINK?");

        // I WANT (9 replies)
        replyList.add("ARE SUCH QUESTIONS ON YOUR MIND OFTEN?");
        replyList.add("WHAT IS IT THAT YOU REALLY WANT TO KNOW?");
        replyList.add("HAVE YOU ASKED ANYONE ELSE?");
        replyList.add("HAVE YOU ASKED SUCH QUESTIONS BEFORE?");
        replyList.add("WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?");
        replyList.add("NAMES DON'T INTEREST ME.");
        replyList.add("I DON'T CARE ABOUT NAMES-- PLEASE GO ON.");
        replyList.add("IS THAT THE REAL REASON?");
        replyList.add("DON'T ANY OTHER REASONS COME TO MIND?");

        // WHAT through WHY (9 replies each)
        String[] questionReplies = {
            "DOES THAT REASON EXPLAIN ANYTHING ELSE?",
            "WHAT OTHER REASONS MIGHT THERE BE?",
            "PLEASE DON'T APOLOGIZE!",
            "APOLOGIES ARE NOT NECESSARY.",
            "WHAT FEELINGS DO YOU HAVE WHEN YOU APOLOGIZE.",
            "DON'T BE SO DEFENSIVE!",
            "WHAT DOES THAT DREAM SUGGEST TO YOU?",
            "DO YOU DREAM OFTEN?",
            "WHAT PERSONS APPEAR IN YOUR DREAMS?"
        };
        for (String r : questionReplies) replyList.add(r);

        // Continue with more replies...
        replyList.add("ARE YOU DISTURBED BY YOUR DREAMS?");
        replyList.add("HOW DO YOU DO ... PLEASE STATE YOUR PROBLEM.");
        replyList.add("YOU DON'T SEEM QUITE CERTAIN.");
        replyList.add("WHY THE UNCERTAIN TONE?");
        replyList.add("CAN'T YOU BE MORE POSITIVE?");
        replyList.add("YOU AREN'T SURE?");
        replyList.add("DON'T YOU KNOW?");
        replyList.add("ARE YOU SAYING NO JUST TO BE NEGATIVE?");
        replyList.add("YOU ARE BEING A BIT NEGATIVE.");
        replyList.add("WHY NOT?");
        replyList.add("ARE YOU SURE?");
        replyList.add("WHY NO?");
        replyList.add("WHY ARE YOU CONCERNED ABOUT MY*");
        replyList.add("WHAT ABOUT YOUR OWN*");
        replyList.add("CAN YOU THINK OF A SPECIFIC EXAMPLE?");
        replyList.add("WHEN?");
        replyList.add("WHAT ARE YOU THINKING OF?");
        replyList.add("REALLY, ALWAYS?");
        replyList.add("DO YOU REALLY THINK SO?");
        replyList.add("BUT YOU ARE NOT SURE YOU*");
        replyList.add("DO YOU DOUBT YOU*");
        replyList.add("IN WHAT WAY?");
        replyList.add("WHAT RESEMBLANCE DO YOU SEE?");
        replyList.add("WHAT DOES THE SIMILARITY SUGGEST TO YOU?");
        replyList.add("WHAT OTHER CONNECTIONS DO YOU SEE?");
        replyList.add("COULD THERE REALLY BE SOME CONNECTION?");
        replyList.add("HOW?");
        replyList.add("YOU SEEM QUITE POSITIVE.");
        replyList.add("ARE YOU SURE?");
        replyList.add("I SEE.");
        replyList.add("I UNDERSTAND.");
        replyList.add("WHY DO YOU BRING UP THE TOPIC OF FRIENDS?");
        replyList.add("DO YOUR FRIENDS WORRY YOU?");
        replyList.add("DO YOUR FRIENDS PICK ON YOU?");
        replyList.add("ARE YOU SURE YOU HAVE ANY FRIENDS?");
        replyList.add("DO YOU IMPOSE ON YOUR FRIENDS?");
        replyList.add("PERHAPS YOUR LOVE FOR FRIENDS WORRIES YOU.");
        replyList.add("DO COMPUTERS WORRY YOU?");
        replyList.add("ARE YOU TALKING ABOUT ME IN PARTICULAR?");
        replyList.add("ARE YOU FRIGHTENED BY MACHINES?");
        replyList.add("WHY DO YOU MENTION COMPUTERS?");
        replyList.add("WHAT DO YOU THINK MACHINES HAVE TO DO WITH YOUR PROBLEM?");
        replyList.add("DON'T YOU THINK COMPUTERS CAN HELP PEOPLE?");
        replyList.add("WHAT IS IT ABOUT MACHINES THAT WORRIES YOU?");
        replyList.add("SAY, DO YOU HAVE ANY PSYCHOLOGICAL PROBLEMS?");
        replyList.add("WHAT DOES THAT SUGGEST TO YOU?");
        replyList.add("I SEE.");
        replyList.add("I'M NOT SURE I UNDERSTAND YOU FULLY.");
        replyList.add("COME COME ELUCIDATE YOUR THOUGHTS.");
        replyList.add("CAN YOU ELABORATE ON THAT?");
        replyList.add("THAT IS QUITE INTERESTING.");

        replies = new String[replyList.size()][1];
        for (int i = 0; i < replyList.size(); i++) {
            replies[i][0] = replyList.get(i);
        }

        // Initialize reply indices (from DATA at lines 2530-2560)
        int[] starts = {1,4,6,6,10,14,17,20,22,25,28,28,32,35,40,40,40,40,40,40,49,51,55,59,63,63,64,69,74,76,80,83,90,93,99,106};
        int[] lengths = {3,2,4,4,4,3,3,2,3,4,4,3,5,9,9,9,9,9,9,2,4,4,4,1,1,5,5,2,4,3,7,3,6,7,6,6};

        replyStart = new int[N1];
        replyLength = new int[N1];
        replyIndex = new int[N1];

        for (int i = 0; i < N1; i++) {
            replyStart[i] = starts[i] - 1; // Convert to 0-based
            replyLength[i] = lengths[i];
            replyIndex[i] = 0;
        }
    }

    public String getResponse(String input) {
        // Add spaces for matching
        input = " " + input.toUpperCase() + "  ";

        // Remove apostrophes
        input = input.replace("'", "");

        // Check for SHUT
        if (input.contains("SHUT")) {
            return "SHUT UP...";
        }

        // Check for repetition
        if (input.equals(previousInput)) {
            previousInput = input;
            return "PLEASE DON'T REPEAT YOURSELF!";
        }

        previousInput = input;

        // Find keyword
        int keywordIndex = -1;
        int keywordPosition = -1;
        String foundKeyword = "";

        for (int k = 0; k < N1; k++) {
            String keyword = keywords[k];
            int pos = input.indexOf(keyword);
            if (pos >= 0) {
                keywordIndex = k;
                keywordPosition = pos;
                foundKeyword = keyword;
                break;
            }
        }

        // If no keyword found, use default (last keyword)
        if (keywordIndex == -1) {
            keywordIndex = 35; // NOKEYFOUND
        }

        // Extract right part and conjugate
        String conjugated = "";
        if (keywordPosition >= 0 && foundKeyword.length() > 0) {
            conjugated = " " + input.substring(keywordPosition + foundKeyword.length());

            // Apply conjugations
            for (String[] pair : conjugations) {
                conjugated = conjugated.replace(pair[0], "###" + pair[1] + "###");
                conjugated = conjugated.replace(pair[1], "###" + pair[0] + "###");
            }
            conjugated = conjugated.replace("###", "");

            // Remove extra leading space
            if (conjugated.length() > 1 && conjugated.charAt(1) == ' ') {
                conjugated = conjugated.substring(1);
            }
        }

        // Get reply
        int replyIdx = replyStart[keywordIndex] + replyIndex[keywordIndex];
        String reply = replies[replyIdx][0];

        // Update reply index for next time
        replyIndex[keywordIndex] = (replyIndex[keywordIndex] + 1) % replyLength[keywordIndex];

        // Apply conjugated text if reply ends with *
        if (reply.endsWith("*")) {
            reply = reply.substring(0, reply.length() - 1) + conjugated;
        }

        return reply;
    }

    public String getGreeting() {
        return "HI!  I'M ELIZA.  WHAT'S YOUR PROBLEM?";
    }
}
