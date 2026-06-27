package com.eliza;

import javax.swing.*;
import java.awt.*;

/**
 * Settings Dialog for LLM Configuration
 */
public class SettingsDialog extends JDialog {

    private LLMConfig config;
    private JCheckBox enableLLMCheckbox;
    private JComboBox<String> providerCombo;
    private JTextField modelField;
    private JPasswordField apiKeyField;
    private JTextField ollamaEndpointField;
    private JButton saveButton;
    private JButton cancelButton;

    public SettingsDialog(Frame parent, LLMConfig config) {
        super(parent, "LLM Settings", true);
        this.config = config;
        initializeUI();
        loadCurrentSettings();
    }

    private void initializeUI() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("LLM Integration Settings");
        titleLabel.setFont(new Font("Sans-Serif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Description
        JTextArea descText = new JTextArea(
            "Enable LLM integration to provide AI-powered responses when rule-based matching fails. " +
            "Configure your preferred provider below."
        );
        descText.setWrapStyleWord(true);
        descText.setLineWrap(true);
        descText.setEditable(false);
        descText.setBackground(mainPanel.getBackground());
        descText.setAlignmentX(Component.LEFT_ALIGNMENT);
        descText.setMaximumSize(new Dimension(450, 60));
        mainPanel.add(descText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Enable LLM checkbox
        enableLLMCheckbox = new JCheckBox("Enable LLM Fallback");
        enableLLMCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        enableLLMCheckbox.addActionListener(e -> toggleFields());
        mainPanel.add(enableLLMCheckbox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Provider selection
        JLabel providerLabel = new JLabel("Provider:");
        providerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(providerLabel);

        String[] providers = {"claude", "openai", "gemini", "ollama"};
        providerCombo = new JComboBox<>(providers);
        providerCombo.setMaximumSize(new Dimension(450, 30));
        providerCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        providerCombo.addActionListener(e -> updateFieldsForProvider());
        mainPanel.add(providerCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Model field
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(modelLabel);

        modelField = new JTextField();
        modelField.setMaximumSize(new Dimension(450, 30));
        modelField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(modelField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // API Key field
        JLabel apiKeyLabel = new JLabel("API Key:");
        apiKeyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(apiKeyLabel);

        apiKeyField = new JPasswordField();
        apiKeyField.setMaximumSize(new Dimension(450, 30));
        apiKeyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(apiKeyField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Ollama endpoint field (hidden by default)
        JLabel ollamaLabel = new JLabel("Ollama Endpoint:");
        ollamaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ollamaLabel.setVisible(false);
        mainPanel.add(ollamaLabel);

        ollamaEndpointField = new JTextField();
        ollamaEndpointField.setMaximumSize(new Dimension(450, 30));
        ollamaEndpointField.setAlignmentX(Component.LEFT_ALIGNMENT);
        ollamaEndpointField.setVisible(false);
        mainPanel.add(ollamaEndpointField);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveSettings());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCurrentSettings() {
        enableLLMCheckbox.setSelected(config.isLLMEnabled());
        providerCombo.setSelectedItem(config.getProvider());
        modelField.setText(config.getModel());

        String provider = (String) providerCombo.getSelectedItem();
        if ("ollama".equals(provider)) {
            ollamaEndpointField.setText(config.getOllamaEndpoint());
        } else {
            apiKeyField.setText(config.getApiKey(provider));
        }

        toggleFields();
    }

    private void toggleFields() {
        boolean enabled = enableLLMCheckbox.isSelected();
        providerCombo.setEnabled(enabled);
        modelField.setEnabled(enabled);
        apiKeyField.setEnabled(enabled);
        ollamaEndpointField.setEnabled(enabled);
    }

    private void updateFieldsForProvider() {
        String provider = (String) providerCombo.getSelectedItem();

        if ("ollama".equals(provider)) {
            apiKeyField.setVisible(false);
            ollamaEndpointField.setVisible(true);
            ollamaEndpointField.setText(config.getOllamaEndpoint());
        } else {
            apiKeyField.setVisible(true);
            ollamaEndpointField.setVisible(false);
            apiKeyField.setText(config.getApiKey(provider));
        }

        // Set default model
        String defaultModel = config.getDefaultModel(provider);
        if (!defaultModel.isEmpty() && modelField.getText().isEmpty()) {
            modelField.setText(defaultModel);
        }
    }

    private void saveSettings() {
        config.setLLMEnabled(enableLLMCheckbox.isSelected());

        String provider = (String) providerCombo.getSelectedItem();
        config.setProvider(provider);
        config.setModel(modelField.getText());

        if ("ollama".equals(provider)) {
            config.setOllamaEndpoint(ollamaEndpointField.getText());
        } else {
            String apiKey = new String(apiKeyField.getPassword());
            if (!apiKey.isEmpty()) {
                config.setApiKey(provider, apiKey);
            }
        }

        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!\nConfig file: " + config.getConfigPath(),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}
