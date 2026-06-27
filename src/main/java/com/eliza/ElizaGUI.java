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
    private JTextField inputField;
    private JButton sendButton;

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
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        inputPanel.add(new JLabel("You: "), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Event handlers
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // Display greeting
        appendToChat("ELIZA: " + engine.getGreeting() + "\n\n");
        inputField.requestFocus();
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();

        if (userInput.isEmpty()) {
            return;
        }

        // Display user input
        appendToChat("YOU: " + userInput + "\n");

        // Get and display response
        String response = engine.getResponse(userInput);
        appendToChat("ELIZA: " + response + "\n\n");

        // Check for shutdown
        if (response.equals("SHUT UP...")) {
            inputField.setEnabled(false);
            sendButton.setEnabled(false);
        }

        // Clear input
        inputField.setText("");
        inputField.requestFocus();
    }

    private void appendToChat(String text) {
        chatArea.append(text);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
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
