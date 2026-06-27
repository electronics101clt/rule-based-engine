package com.eliza;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ELIZA GUI - Swing interface for the ELIZA chatbot
 */
public class ElizaGUI extends JFrame {

    private ElizaEngine engine;
    private JTextArea chatArea;
    private boolean waitingForInput = true;
    private String currentLine = "";

    public ElizaGUI() {
        engine = new ElizaEngine();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ELIZA - Rogerian Psychotherapist");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create chat area
        chatArea = new JTextArea();
        chatArea.setEditable(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Event handlers
        chatArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && waitingForInput) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        // Display greeting
        chatArea.append("ELIZA: " + engine.getGreeting() + "\n\nYOU: ");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void sendMessage() {
        try {
            String fullText = chatArea.getText();
            int lastYouIndex = fullText.lastIndexOf("YOU: ");

            if (lastYouIndex == -1) {
                return;
            }

            String userInput = fullText.substring(lastYouIndex + 5).trim();

            if (userInput.isEmpty()) {
                return;
            }

            waitingForInput = false;
            chatArea.setEditable(false);

            // Get and display response
            String response = engine.getResponse(userInput);
            chatArea.append("\n\nELIZA: " + response + "\n\n");

            // Check for shutdown
            if (response.equals("SHUT UP...")) {
                return;
            }

            chatArea.append("YOU: ");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            chatArea.setEditable(true);
            waitingForInput = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            ElizaGUI gui = new ElizaGUI();
            gui.setVisible(true);
        });
    }
}
