package com.eliza;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes shell commands and returns output
 */
public class CommandExecutor {

    /**
     * Extract commands from text (looks for text in single quotes)
     */
    public static List<String> extractCommands(String text) {
        List<String> commands = new ArrayList<>();

        // Pattern to match text in single quotes
        Pattern pattern = Pattern.compile("'([^']+)'");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String cmd = matcher.group(1);
            // Filter out non-command text
            if (isLikelyCommand(cmd)) {
                commands.add(cmd);
            }
        }

        return commands;
    }

    /**
     * Check if string looks like a command
     */
    private static boolean isLikelyCommand(String text) {
        // Commands typically have spaces or contain common command names
        String[] commonCommands = {"ls", "cd", "mkdir", "rm", "cp", "mv", "chmod",
            "grep", "find", "sudo", "apt", "snap", "cat", "echo", "pwd", "df",
            "free", "top", "ps", "kill", "ssh", "tar", "wget", "curl", "nano",
            "vim", "systemctl", "journalctl", "dmesg", "lsblk", "ip", "nmcli"};

        String lowerText = text.toLowerCase();
        for (String cmd : commonCommands) {
            if (lowerText.startsWith(cmd + " ") || lowerText.equals(cmd)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Execute a shell command and return output
     */
    public static CommandResult execute(String command) {
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        int exitCode = -1;

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            // Read standard output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = stdError.readLine()) != null) {
                error.append(line).append("\n");
            }

            exitCode = process.waitFor();

        } catch (Exception e) {
            error.append("Error executing command: ").append(e.getMessage());
        }

        return new CommandResult(command, output.toString(), error.toString(), exitCode);
    }

    /**
     * Result of command execution
     */
    public static class CommandResult {
        public final String command;
        public final String output;
        public final String error;
        public final int exitCode;

        public CommandResult(String command, String output, String error, int exitCode) {
            this.command = command;
            this.output = output;
            this.error = error;
            this.exitCode = exitCode;
        }

        public boolean isSuccess() {
            return exitCode == 0;
        }

        public String getFormattedOutput() {
            StringBuilder sb = new StringBuilder();
            sb.append("$ ").append(command).append("\n\n");

            if (!output.isEmpty()) {
                sb.append(output);
            }

            if (!error.isEmpty()) {
                sb.append("Error: \n").append(error);
            }

            if (!isSuccess() && output.isEmpty() && error.isEmpty()) {
                sb.append("Command failed with exit code: ").append(exitCode);
            }

            return sb.toString();
        }
    }
}
