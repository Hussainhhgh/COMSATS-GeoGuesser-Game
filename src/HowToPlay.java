import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HowToPlay extends JFrame {
    private BufferedImage backgroundImage;

    // UI Colors (matching your game's theme)
    private final Color PRIMARY_BLUE = new Color(40, 124, 253);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color WARNING_ORANGE = new Color(249, 115, 22);
    private final Color DANGER_RED = new Color(239, 68, 68);
    private final Color DARK_BG = new Color(15, 23, 42);
    private final Color CARD_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private final Color ACCENT_PURPLE = new Color(168, 85, 247);

    public HowToPlay() {
        loadBackgroundImage();
        setupUI();
        setVisible(true);
    }

    private void loadBackgroundImage() {
        try {
            String[] possiblePaths = {"background.jpg", "background.png", "src/background.jpg", "src/background.png"};
            for (String path : possiblePaths) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    backgroundImage = ImageIO.read(imageFile);
                    return;
                }
            }
            backgroundImage = null;
        } catch (IOException e) {
            backgroundImage = null;
        }
    }

    private void setupUI() {
        setTitle("COMSATS Geo Guesser - How to Play");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        // Main panel with custom background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.setColor(new Color(0, 0, 0, 140));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(0, 0, DARK_BG, getWidth(), getHeight(), CARD_BG);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Content panel with scroll
        JScrollPane scrollPane = createContentPanel();

        // Footer panel with back button
        JPanel footerPanel = createFooterPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("HOW TO PLAY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Master the COMSATS Campus Geography Challenge", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Game Overview Section
        contentPanel.add(createSectionPanel("Game Overview",
                "COMSATS Geo Guesser is a location-based guessing game where you identify various spots around the COMSATS campus by looking at photographs and pinpointing their locations on a campus map.",
                PRIMARY_BLUE));

        contentPanel.add(Box.createVerticalStrut(20));

        // Objective Section
        contentPanel.add(createSectionPanel("Objective",
                "Score as many points as possible by accurately guessing the locations of campus photographs within the time limit.",
                SUCCESS_GREEN));

        contentPanel.add(Box.createVerticalStrut(20));

        // How to Play Section
        contentPanel.add(createDetailedSectionPanel("How to Play", new String[]{
                "1. Enter your username (maximum 12 characters)",
                "2. Study the location photograph on the left panel",
                "3. Click on the campus map where you think the photo was taken",
                "4. Confirm your guess when prompted",
                "5. Submit your guess or let the timer auto-submit"
        }, WARNING_ORANGE));

        contentPanel.add(Box.createVerticalStrut(20));

        // Scoring System Section
        contentPanel.add(createDetailedSectionPanel("Scoring System", new String[]{
                "Base Points: 1000 points per round",
                "Distance Penalty: -0.5 points per pixel away from actual location",
                "Time Bonus: +3 points per second remaining",
                "Formula: max(0, 1000 - distance_penalty) + time_bonus"
        }, ACCENT_PURPLE));

        contentPanel.add(Box.createVerticalStrut(20));

        // Hint System Section
        contentPanel.add(createDetailedSectionPanel("Hint System", new String[]{
                "Hints become available after 10 seconds",
                "Each hint costs 50 points from your current score",
                "Provides helpful clues about the location",
                "Use wisely - you need enough points to afford them!"
        }, WARNING_ORANGE));

        contentPanel.add(Box.createVerticalStrut(20));

        // Tips Section
        contentPanel.add(createDetailedSectionPanel("Tips for Success", new String[]{
                "Study the campus layout and familiarize yourself with landmarks",
                "Look for distinctive features in the photographs",
                "Act quickly to earn time bonuses",
                "Save hints for when you're really stuck",
                "Practice makes perfect - play multiple rounds!"
        }, SUCCESS_GREEN));

        contentPanel.add(Box.createVerticalStrut(20));

        // Performance Levels Section
        contentPanel.add(createDetailedSectionPanel("Performance Levels", new String[]{
                "Excellent: 5000+ points - You're a campus expert!",
                "Good: 3000-4999 points - Well done!",
                "Nice Try: Below 3000 points - Keep practicing!"
        }, DANGER_RED));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    private JPanel createSectionPanel(String title, String content, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 15, CARD_BG.getGreen() + 15, CARD_BG.getBlue() + 20));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(accentColor);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setOpaque(false);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        contentArea.setForeground(TEXT_PRIMARY);
        contentArea.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailedSectionPanel(String title, String[] points, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 15, CARD_BG.getGreen() + 15, CARD_BG.getBlue() + 20));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(accentColor);

        JPanel pointsPanel = new JPanel();
        pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));
        pointsPanel.setOpaque(false);
        pointsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        for (String point : points) {
            JLabel pointLabel = new JLabel("• " + point);
            pointLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            pointLabel.setForeground(TEXT_PRIMARY);
            pointLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0));
            pointsPanel.add(pointLabel);
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(pointsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JButton backButton = createModernButton("BACK TO MAIN MENU", PRIMARY_BLUE);
        backButton.addActionListener(e -> {
            dispose();
        });

        JButton startGameButton = createModernButton("START PLAYING NOW!", SUCCESS_GREEN);
        startGameButton.addActionListener(e -> {
            dispose(); // Close the How to Play window
            new MainGame(); // Start the game directly
        });

        panel.add(backButton);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(startGameButton);

        return panel;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                if (isEnabled()) {
                    Color darkColor = new Color(
                            (int)(color.getRed() * 0.8),
                            (int)(color.getGreen() * 0.8),
                            (int)(color.getBlue() * 0.8));
                    GradientPaint gradient = new GradientPaint(0, 0, color, 0, h, darkColor);
                    g2d.setPaint(gradient);
                } else {
                    g2d.setColor(new Color(100, 100, 100));
                }

                g2d.fillRoundRect(0, 0, w, h, 15, 15);

                if (isEnabled()) {
                    g2d.setColor(new Color(255, 255, 255, 51));
                    g2d.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);
                }

                g2d.setColor(isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                g2d.drawString(getText(), (w - textW) / 2, (h + textH) / 2 - 2);
            }
        };

        button.setPreferredSize(new Dimension(250, 60));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Test method - remove this when integrating with your main menu
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HowToPlay());
    }
}