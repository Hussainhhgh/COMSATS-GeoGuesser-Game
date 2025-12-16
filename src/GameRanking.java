import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GameRanking extends JFrame {
    private static final String RANKINGS_FILE = "rankings.txt";
    private static final int MAX_RANKINGS = 10;

    // Color scheme matching MainGame
    private static final Color PRIMARY_BLUE = new Color(40, 124, 253);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(249, 115, 22);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color CARD_BG = new Color(30, 41, 59);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_PURPLE = new Color(168, 85, 247);
    private static final Color TABLE_HEADER_BG = new Color(51, 65, 85);
    private static final Color TABLE_ROW_BG = new Color(30, 41, 59);
    private static final Color TABLE_ALT_ROW_BG = new Color(40, 51, 69);

    private static BufferedImage backgroundImage;

    // Static block to load background image once when the class is loaded
    static {
        loadBackgroundImage();
    }

    private static void loadBackgroundImage() {
        try {
            // Updated possible paths for better flexibility
            String[] possiblePaths = {
                    "background.jpg", "background.png", // Current directory
                    "src/background.jpg", "src/background.png", // Common source folder
                    "./background.jpg", "./background.png" // Relative path
            };

            for (String path : possiblePaths) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    backgroundImage = ImageIO.read(imageFile);
                    return; // Image found and loaded, exit loop
                }
            }
            backgroundImage = null; // No image found
        } catch (IOException e) {
            // Log the error but don't prevent the application from running
            System.err.println("Error loading background image for GameRanking: " + e.getMessage());
            backgroundImage = null;
        }
    }

    // Constructor to instantiate the GameRanking window
    public GameRanking() {
        // The showRankings() method handles the JFrame creation and setup,
        // so we just call it here to display the window when a new GameRanking object is created.
        showRankings();
    }

    public static void addScore(String username, int score) {
        List<PlayerScore> scores = loadScores();
        scores.add(new PlayerScore(username, score));
        scores.sort((a, b) -> Integer.compare(b.score, a.score)); // Sort in descending order

        // Keep only top scores
        if (scores.size() > MAX_RANKINGS) {
            scores = scores.subList(0, MAX_RANKINGS);
        }

        saveScores(scores);
    }

    public static void showRankings() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("COMSATS Geo Guesser - Leaderboard");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes only this window
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null); // Center the frame on screen

            // Main panel with custom background painting
            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (backgroundImage != null) {
                        // Draw image and then overlay a semi-transparent black
                        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                        g2d.setColor(new Color(0, 0, 0, 140)); // More opaque overlay than MainMenu
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    } else {
                        // Fallback gradient if no image
                        GradientPaint gradient = new GradientPaint(
                                0, 0, DARK_BG,
                                getWidth(), getHeight(), CARD_BG
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                    g2d.dispose();
                }
            };

            // Header panel (Title and Subtitle)
            JPanel headerPanel = createHeaderPanel();
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Table panel (holds the JTable)
            JPanel tablePanel = createTablePanel();
            mainPanel.add(tablePanel, BorderLayout.CENTER);

            // Button panel (Back, Refresh, Clear, Exit buttons)
            JPanel buttonPanel = createButtonPanel(frame);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            frame.add(mainPanel); // Add the main panel to the frame
            frame.setVisible(true); // Make the frame visible
        });
    }

    private static JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));
        panel.setOpaque(false); // Make panel transparent to show background

        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Top Players - COMSATS Geo Guesser", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createTablePanel() {
        // Custom JPanel to draw a rounded rectangle background for the table
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Do not call super.paintComponent(g) here,
                // as it would paint the default JPanel background over our custom drawing.
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a slightly varying gradient for the card background
                GradientPaint gradient = new GradientPaint(
                        0, 0, CARD_BG,
                        0, getHeight(), new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24); // Rounded corners

                // Draw a border
                g2d.setColor(PRIMARY_BLUE);
                g2d.setStroke(new BasicStroke(2.0f)); // 2 pixel thick stroke
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24); // Draw inside bounds

                g2d.dispose();
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding around the table card
        panel.setOpaque(false); // Make transparent to show background image/gradient of mainPanel

        // Create the JTable with data and styling
        JTable table = createStyledTable();

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding inside the card
        // Setting background to 0 alpha makes it fully transparent if it were to paint,
        // but with setOpaque(false) on viewport, it relies on parent painting.
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));


        panel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the table panel

        return panel;
    }

    private static JTable createStyledTable() {
        List<PlayerScore> scores = loadScores(); // Load scores to populate the table

        String[] columnNames = {"RANK", "PLAYER NAME", "SCORE"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
        };

        // Populate table rows with loaded scores
        for (int i = 0; i < scores.size(); i++) {
            PlayerScore score = scores.get(i);
            Object[] row = {
                    String.valueOf(i + 1), // Rank (1-based index)
                    score.username,
                    String.valueOf(score.score)
            };
            model.addRow(row);
        }

        // If no scores are present, display a message
        if (scores.isEmpty()) {
            model.addRow(new Object[]{"", "No scores yet. Play a game!", ""});
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setRowHeight(60); // Set row height for better appearance
        table.setShowGrid(false); // Hide grid lines
        table.setIntercellSpacing(new Dimension(0, 1)); // Small vertical spacing between cells
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected
        table.setOpaque(false); // Make table transparent to show scroll pane's background (or card background)

        // Custom cell renderer for advanced styling of individual cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                // Call super to get default component and properties
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setOpaque(true); // Make sure the renderer paints its background
                setFont(new Font("Segoe UI", Font.BOLD, 20)); // Default font for cells

                // Alternating row colors for better readability
                if (row % 2 == 0) {
                    setBackground(TABLE_ROW_BG);
                } else {
                    setBackground(TABLE_ALT_ROW_BG);
                }

                // Apply specific styling based on column index
                switch (column) {
                    case 0: // Rank column
                        setHorizontalAlignment(SwingConstants.CENTER); // Center align rank
                        setForeground(getRankColor(row)); // Special colors for top ranks
                        setFont(new Font("Segoe UI", Font.BOLD, 24)); // Larger font for rank
                        break;
                    case 1: // Player Name column
                        setHorizontalAlignment(SwingConstants.LEFT); // Left align name
                        setForeground(TEXT_PRIMARY); // Primary text color
                        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Left padding
                        break;
                    case 2: // Score column
                        setHorizontalAlignment(SwingConstants.RIGHT); // Right align score
                        setForeground(SUCCESS_GREEN); // Green for scores
                        setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger, bold font for score
                        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // Right padding
                        break;
                }

                // Override background if row is selected
                if (isSelected) {
                    setBackground(PRIMARY_BLUE.darker()); // Darker blue for selected row
                }

                return this;
            }

            // Helper method to get special colors for top ranks
            private Color getRankColor(int rank) {
                switch (rank) {
                    case 0: return new Color(255, 215, 0); // Gold for 1st
                    case 1: return new Color(192, 192, 192); // Silver for 2nd
                    case 2: return new Color(205, 127, 50); // Bronze for 3rd
                    default: return ACCENT_PURPLE; // Default for others
                }
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 50)); // Set header height
        header.setBorder(BorderFactory.createEmptyBorder()); // Remove default header border

        // Custom header renderer for border and alignment
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setOpaque(true);
                setBackground(TABLE_HEADER_BG);
                setForeground(TEXT_SECONDARY);
                setFont(new Font("Segoe UI", Font.BOLD, 16));
                setHorizontalAlignment(SwingConstants.CENTER); // Center align header text
                // Custom border for header
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_BLUE), // Blue line at bottom of header
                        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
                ));

                return this;
            }
        });

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Rank
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Player Name
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Score

        return table;
    }

    private static JPanel createButtonPanel(JFrame frame) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // Center buttons with spacing
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50)); // Padding around buttons
        panel.setOpaque(false); // Transparent to show background

        JButton backButton = createModernButton("BACK TO MAIN MENU", SUCCESS_GREEN);
        backButton.addActionListener(e -> {
            frame.dispose(); // Close the current GameRanking window
            new MainMenu(); // Reopen the MainMenu
        });

        JButton refreshButton = createModernButton("REFRESH RANKINGS", PRIMARY_BLUE);
        refreshButton.addActionListener(e -> {
            frame.dispose(); // Close current rankings window
            showRankings(); // Reopen to show updated data
        });

        JButton clearButton = createModernButton("CLEAR RANKINGS", DANGER_RED);
        clearButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog( // Confirmation dialog
                    frame,
                    "Are you sure you want to clear all rankings?",
                    "Clear Rankings",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                clearRankings(); // Delete the rankings file
                frame.dispose(); // Close current window
                showRankings(); // Reopen to show empty or updated rankings
            }
        });

        JButton exitButton = createModernButton("EXIT", WARNING_ORANGE);
        exitButton.addActionListener(e -> {
            frame.dispose(); // Close current window
            System.exit(0); // Terminate the application
        });

        panel.add(backButton);
        panel.add(refreshButton);
        panel.add(clearButton);
        panel.add(exitButton);

        return panel;
    }

    private static JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                if (isEnabled()) { // Button is active
                    Color darkColor = new Color(
                            (int)(color.getRed() * 0.8),
                            (int)(color.getGreen() * 0.8),
                            (int)(color.getBlue() * 0.8)
                    );
                    GradientPaint gradient = new GradientPaint(0, 0, color, 0, h, darkColor);
                    g2d.setPaint(gradient);
                } else { // Button is disabled
                    g2d.setColor(new Color(100, 100, 100)); // Gray out disabled buttons
                }

                g2d.fillRoundRect(0, 0, w, h, 12, 12); // Fill with rounded corners

                if (isEnabled()) {
                    g2d.setColor(new Color(255, 255, 255, 51)); // Subtle white highlight
                    g2d.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                }

                // Draw button text
                g2d.setColor(isEnabled() ? Color.WHITE : Color.LIGHT_GRAY); // Text color based on enabled state
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                g2d.drawString(getText(), (w - textW) / 2, (h + textH) / 2 - 2);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(220, 55)); // Standard button size
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        return button;
    }

    private static List<PlayerScore> loadScores() {
        List<PlayerScore> scores = new ArrayList<>();
        // Use try-with-resources for automatic resource closing
        try (BufferedReader reader = new BufferedReader(new FileReader(RANKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    scores.add(new PlayerScore(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (FileNotFoundException e) {
            // File might not exist yet, which is fine for the first run.
            // No need to print stack trace for this common scenario.
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading rankings from file: " + e.getMessage());
            // Consider logging this or notifying the user if the file is corrupted
        }
        return scores;
    }

    private static void saveScores(List<PlayerScore> scores) {
        // Use try-with-resources for automatic resource closing
        try (PrintWriter writer = new PrintWriter(new FileWriter(RANKINGS_FILE))) {
            for (PlayerScore score : scores) {
                writer.println(score.username + "," + score.score);
            }
        } catch (IOException e) {
            System.err.println("Error saving rankings to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void clearRankings() {
        try {
            File file = new File(RANKINGS_FILE);
            if (file.exists()) {
                if (!file.delete()) {import java.awt.*;
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
                    System.err.println("Failed to delete rankings file: " + RANKINGS_FILE);
                }
            }
        } catch (SecurityException e) {
            System.err.println("Security exception while trying to clear rankings: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while clearing rankings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper class to store player scores (static nested class for better encapsulation)
    private static class PlayerScore {
        String username;
        int score;

        PlayerScore(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }

    // Test method - for standalone testing of GameRanking
    public static void main(String[] args) {
        // Add some test data

        // Show the rankings window
        new GameRanking(); // Creates an instance, which calls showRankings()
    }
}