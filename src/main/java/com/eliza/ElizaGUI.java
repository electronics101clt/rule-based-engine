package com.eliza;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ELIZA GUI - Modern chat bubble interface
 */
public class ElizaGUI extends JFrame {

    private ElizaEngine engine;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    // Modern color scheme
    private static final Color BG_COLOR = new Color(230, 235, 240);
    private static final Color INPUT_BG = Color.WHITE;
    private static final Color SEND_BUTTON = new Color(52, 152, 219);

    public ElizaGUI() {
        engine = new ElizaEngine();
        initializeUI();
        addMessage(engine.getGreeting(), true);
    }

    private void initializeUI() {
        setTitle("ELIZA - AI Therapist");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ELIZA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel subtitleLabel = new JLabel("Your AI Therapist");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 220, 240));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Chat area
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG_COLOR);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setBackground(SEND_BUTTON);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Event handlers
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        inputField.requestFocus();
    }

    private void addMessage(String message, boolean isEliza) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.X_AXIS));
        messageWrapper.setOpaque(false);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        ChatBubble bubble = new ChatBubble(message, isEliza);

        if (isEliza) {
            // Left align for ELIZA
            messageWrapper.add(bubble);
            messageWrapper.add(Box.createHorizontalGlue());
        } else {
            // Right align for USER
            messageWrapper.add(Box.createHorizontalGlue());
            messageWrapper.add(bubble);
        }

        chatPanel.add(messageWrapper);
        chatPanel.revalidate();

        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();

        if (userInput.isEmpty()) {
            return;
        }

        // Add user message
        addMessage(userInput, false);

        // Get and display response
        String response = engine.getResponse(userInput);

        // Delay response slightly for natural feel
        Timer timer = new Timer(300, e -> {
            addMessage(response, true);

            // Check for shutdown
            if (response.equals("SHUT UP...")) {
                inputField.setEnabled(false);
                sendButton.setEnabled(false);
            }
        });
        timer.setRepeats(false);
        timer.start();

        // Clear input
        inputField.setText("");
        inputField.requestFocus();
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
