package com.eliza;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom chat bubble component for messages
 */
public class ChatBubble extends JPanel {

    private String message;
    private boolean isEliza;
    private static final int RADIUS = 18;
    private static final int PADDING = 12;

    // Modern colors
    private static final Color ELIZA_BUBBLE = new Color(52, 152, 219);  // Blue
    private static final Color USER_BUBBLE = new Color(236, 240, 241);  // Light gray
    private static final Color ELIZA_TEXT = Color.WHITE;
    private static final Color USER_TEXT = new Color(44, 62, 80);

    public ChatBubble(String message, boolean isEliza) {
        this.message = message;
        this.isEliza = isEliza;

        setOpaque(false);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(isEliza ? ELIZA_TEXT : USER_TEXT);
        textArea.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        add(textArea, BorderLayout.CENTER);

        // Set max width
        setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle bubble
        int width = getWidth();
        int height = getHeight();

        g2.setColor(isEliza ? ELIZA_BUBBLE : USER_BUBBLE);
        g2.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, RADIUS, RADIUS));

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = Math.min(size.width, 400);
        return size;
    }
}
