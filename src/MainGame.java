import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

public class MainGame {
    private JFrame frame;
    private String username;
    private BufferedImage backgroundImage;
    private JLabel imageLabel;
    private JLabel mapLabel;
    private JLabel userLabel;
    private JLabel roundLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel hintLabel;
    private ArrayList<String> locationImages = new ArrayList<>();
    private ArrayList<String> locationNames = new ArrayList<>();
    private ArrayList<String> originalLocationImages = new ArrayList<>();
    private ArrayList<String> originalLocationNames = new ArrayList<>();
    private HashMap<String, Point> locationCoordinates = new HashMap<>();
    private Random random = new Random();
    private int currentRound = 1;
    private int totalScore = 0;
    private int timeLeft = 30;
    private Timer countdownTimer;
    private Timer hintTimer;
    private int totalRounds = 5;
    private String currentLocation;
    private Point actualLocation;
    private Point guessedLocation;
    private JButton submitButton;
    private JButton hintButton;
    private ImageIcon campusMap;

    // UI Colors
    private final Color PRIMARY_BLUE = new Color(40, 124, 253);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color WARNING_ORANGE = new Color(249, 115, 22);
    private final Color DANGER_RED = new Color(239, 68, 68);
    private final Color DARK_BG = new Color(15, 23, 42);
    private final Color CARD_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private final Color ACCENT_PURPLE = new Color(168, 85, 247);

    // Map dimensions (should match your map image dimensions)
    private static final int MAP_WIDTH = 1000;
    private static final int MAP_HEIGHT = 800;

    public MainGame() {
        loadBackgroundImage();
        loadLocations();
        initializeLocationCoordinates();
        setupUI();
        startGame();
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

    private void loadLocations() {
        try {
            addLocation("src/bus_stop.png", "Bus Stop");
            addLocation("src/cafe_front.png", "Cafe Front");
            addLocation("src/cs_lawn.png", "CS Lawn");
            addLocation("src/cs_lawn (2).png", "CS Lawn");
            addLocation("src/cs_view.png", "CS View");
            addLocation("src/kia ker raha ha bhai.png", "Kia ker raha ha bhai?");
            addLocation("src/nblock_front.png", "N-Block Front");
            addLocation("src/near_logo.png", "CUI Logo");
            addLocation("src/near_mosque.png", "Near Mosque");
            addLocation("src/near_parking.png", "Near Parking");
            addLocation("src/physics block.png", "Physics Block");

            // Copy initial locations to the mutable lists for the game
            originalLocationImages.addAll(locationImages);
            originalLocationNames.addAll(locationNames);

            campusMap = loadImageIcon("src/FINAL MAP.png");
            if (campusMap == null) {
                JOptionPane.showMessageDialog(null, "Failed to load campus map!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeLocationCoordinates() {
        // Actual pixel coordinates on the map image (x, y)
        // You need to adjust these to match your actual map image
        locationCoordinates.put("Bus Stop", new Point(412, 414));
        locationCoordinates.put("Cafe Front", new Point(187, 347));
        locationCoordinates.put("CS Lawn", new Point(205, 103));
        locationCoordinates.put("CS Lawn (2)", new Point(125, 241));
        locationCoordinates.put("CS View", new Point(239, 197));
        locationCoordinates.put("Kia ker raha ha bhai?", new Point(105, 255));
        locationCoordinates.put("N-Block Front", new Point(71, 268));
        locationCoordinates.put("CUI Logo", new Point(297, 69));
        locationCoordinates.put("Near Mosque", new Point(278, 54));
        locationCoordinates.put("Near Parking", new Point(99, 200));
        locationCoordinates.put("Physics Block", new Point(246, 178));
    }

    private ImageIcon loadImageIcon(String path) {
        try {
            return new ImageIcon(ImageIO.read(new File(path)));
        } catch (IOException e) {
            return null;
        }
    }

    private void addLocation(String imagePath, String locationName) {
        // Add to both current and original lists
        locationImages.add(imagePath);
        locationNames.add(locationName);
    }

    private void setupUI() {
        frame = new JFrame("COMSATS Geo Guesser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.setColor(new Color(0, 0, 0, 120));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(0, 0, DARK_BG, getWidth(), getHeight(), CARD_BG);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createGameArea(), BorderLayout.CENTER);
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);
        frame.add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setOpaque(false);

        panel.add(createStatsCard("PLAYER", "", userLabel = new JLabel(), PRIMARY_BLUE));
        panel.add(createRoundScoreCard());
        panel.add(createTimerCard());
        panel.add(createHintCard());

        return panel;
    }

    private JPanel createStatsCard(String title, String subtitle, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
            }
        };

        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRoundScoreCard() {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2d.setColor(SUCCESS_GREEN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
            }
        };

        card.setLayout(new GridLayout(2, 2, 10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setOpaque(false);

        JLabel roundText = new JLabel("ROUND", SwingConstants.CENTER);
        roundText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roundText.setForeground(TEXT_SECONDARY);

        roundLabel = new JLabel("1/" + totalRounds, SwingConstants.CENTER);
        roundLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        roundLabel.setForeground(SUCCESS_GREEN);

        JLabel scoreText = new JLabel("SCORE", SwingConstants.CENTER);
        scoreText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        scoreText.setForeground(TEXT_SECONDARY);

        scoreLabel = new JLabel("0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreLabel.setForeground(WARNING_ORANGE);

        card.add(roundText);
        card.add(roundLabel);
        card.add(scoreText);
        card.add(scoreLabel);

        return card;
    }

    private JPanel createTimerCard() {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2d.setColor(DANGER_RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
            }
        };

        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel("TIME LEFT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);

        timerLabel = new JLabel("30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timerLabel.setForeground(DANGER_RED);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(timerLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createHintCard() {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2d.setColor(ACCENT_PURPLE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
            }
        };

        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel("HINT READY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);

        hintLabel = new JLabel("10s", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hintLabel.setForeground(ACCENT_PURPLE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(hintLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createGameArea() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setOpaque(false);

        panel.add(createImagePanel(), BorderLayout.CENTER);
        panel.add(createMapPanel(), BorderLayout.EAST);

        return panel;
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(PRIMARY_BLUE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("LOCATION VIEW", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(imageLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMapPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_BG, 0, getHeight(),
                        new Color(CARD_BG.getRed() + 10, CARD_BG.getGreen() + 10, CARD_BG.getBlue() + 15));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(SUCCESS_GREEN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(500, 0));

        JLabel titleLabel = new JLabel("CLICK ON MAP TO GUESS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        mapLabel = new JLabel(campusMap);
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mapLabel.setVerticalAlignment(SwingConstants.CENTER);
        mapLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        mapLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (guessedLocation == null && countdownTimer != null && countdownTimer.isRunning()) {
                    guessedLocation = e.getPoint();

                    double mapLabelWidth = mapLabel.getWidth();
                    double mapLabelHeight = mapLabel.getHeight();
                    double campusMapIconWidth = campusMap.getIconWidth();
                    double campusMapIconHeight = campusMap.getIconHeight();

                    // Calculate scale factors if the map is resized to fit mapLabel
                    double scaleX = campusMapIconWidth / mapLabelWidth;
                    double scaleY = campusMapIconHeight / mapLabelHeight;

                    // Adjust click coordinates based on the scale and image position within the label
                    // Assuming the image is centered, calculate offset
                    int imageX = (int) ((mapLabelWidth - campusMapIconWidth / scaleX) / 2);
                    int imageY = (int) ((mapLabelHeight - campusMapIconHeight / scaleY) / 2);

                    int adjustedX = (int)((e.getX() - imageX) * scaleX);
                    int adjustedY = (int)((e.getY() - imageY) * scaleY);

                    // Ensure adjusted coordinates are within the actual map bounds
                    adjustedX = Math.max(0, Math.min(adjustedX, campusMap.getIconWidth()));
                    adjustedY = Math.max(0, Math.min(adjustedY, campusMap.getIconHeight()));

                    guessedLocation = new Point(adjustedX, adjustedY);


                    if (JOptionPane.showConfirmDialog(frame,
                            "Confirm your guess at this location?", "Confirm Guess",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        submitGuess();
                    } else {
                        guessedLocation = null; // User cancelled, allow re-guessing
                    }
                }
            }
        });


        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(mapLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        panel.setOpaque(false);

        hintButton = createModernButton("GET HINT (Cost: 50)", WARNING_ORANGE);
        hintButton.setEnabled(false);
        hintButton.addActionListener(e -> showHint());

        submitButton = createModernButton("SUBMIT GUESS", SUCCESS_GREEN);
        submitButton.addActionListener(e -> {
            if (guessedLocation == null) {
                JOptionPane.showMessageDialog(frame, "Please click on the map to make a guess first!", "No Guess", JOptionPane.WARNING_MESSAGE);
            } else {
                submitGuess();
            }
        });

        panel.add(hintButton);
        panel.add(submitButton);

        return panel;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
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

                g2d.fillRoundRect(0, 0, w, h, 12, 12);

                if (isEnabled()) {
                    g2d.setColor(new Color(255, 255, 255, 51));
                    g2d.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                }

                g2d.setColor(isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                g2d.drawString(getText(), (w - textW) / 2, (h + textH) / 2 - 2);
            }
        };

        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void startGame() {
        while (username == null || username.trim().isEmpty()) {
            username = JOptionPane.showInputDialog(frame, "Enter your username:", "Welcome", JOptionPane.PLAIN_MESSAGE);
            if (username == null) {
                System.exit(0);
            }

            if (username.trim().length() > 12) {
                JOptionPane.showMessageDialog(frame, "Maximum 12 characters allowed.", "Invalid Username", JOptionPane.WARNING_MESSAGE);
                username = null;
            }
        }

        userLabel.setText(username);
        frame.setVisible(true);
        // Reset game state for a new game
        currentRound = 1;
        totalScore = 0;
        roundLabel.setText(currentRound + "/" + totalRounds);
        scoreLabel.setText(String.valueOf(totalScore));

        locationImages.clear();
        locationNames.clear();
        locationImages.addAll(originalLocationImages); // Restore all locations
        locationNames.addAll(originalLocationNames);

        loadNewRound();
    }

    private void loadNewRound() {
        if (locationImages.isEmpty() || currentRound > totalRounds) {
            endGame();
            return;
        }

        timeLeft = 30;
        timerLabel.setText(String.valueOf(timeLeft));
        hintButton.setEnabled(false);
        hintLabel.setText("10s");
        guessedLocation = null;

        try {
            int index = random.nextInt(locationImages.size());
            String imagePath = locationImages.get(index);
            currentLocation = locationNames.get(index);
            actualLocation = locationCoordinates.get(currentLocation);

            // Remove the used location from the lists to prevent re-selection
            locationImages.remove(index);
            locationNames.remove(index);

            ImageIcon icon = new ImageIcon(ImageIO.read(new File(imagePath)));
            Image scaled = icon.getImage().getScaledInstance(900, 750, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // If an image fails to load, it might lead to issues. For robustness, you might want to:
            // 1. Try another random image.
            // 2. Skip this round and move to the next.
            // 3. End the game with an error.
            // For simplicity, we'll end the game here if a critical image load fails.
            endGame(); // Or a more specific error handling
            return;
        }

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                countdownTimer.stop();
                timeExpired();
            }
        });
        countdownTimer.start();

        if (hintTimer != null) {
            hintTimer.stop();
        }

        hintTimer = new Timer(1000, new ActionListener() {
            int count = 10;

            public void actionPerformed(ActionEvent e) {
                count--;
                hintLabel.setText(count + "s");
                if (count <= 0) {
                    hintTimer.stop();
                    hintButton.setEnabled(true);
                    hintLabel.setText("Ready!");
                }
            }
        });
        hintTimer.start();
    }

    private void showHint() {
        if (totalScore >= 50) {
            totalScore -= 50;
            scoreLabel.setText(String.valueOf(totalScore));
            JOptionPane.showMessageDialog(frame, generateHint(currentLocation), "Hint", JOptionPane.INFORMATION_MESSAGE);
            hintButton.setEnabled(false);
            hintLabel.setText("Used");
        } else {
            JOptionPane.showMessageDialog(frame, "You need at least 50 points for a hint.", "Insufficient Points", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String generateHint(String name) {
        if (name.contains("Bus Stop")) {
            return "Hint: Where students wait for campus shuttles.";
        } else if (name.contains("Cafe")) {
            return "Hint: Place to grab a quick bite and hang out.";
        } else if (name.contains("CS Lawn")) {
            return "Hint: A green area often associated with the Computer Science department.";
        } else if (name.contains("CS View")) {
            return "Hint: Offers a specific perspective near the Computer Science block.";
        } else if (name.contains("Kia ker raha ha bhai?")) {
            return "Hint: This is a unique, perhaps informal, campus spot.";
        } else if (name.contains("N-Block")) {
            return "Hint: One of the main academic blocks, starting with 'N'.";
        } else if (name.contains("CUI Logo")) {
            return "Hint: You'll find the university's emblem here.";
        } else if (name.contains("Mosque")) {
            return "Hint: The campus prayer area.";
        } else if (name.contains("Parking")) {
            return "Hint: Where you'd leave your vehicle.";
        } else if (name.contains("Physics")) {
            return "Hint: The building dedicated to the study of physical sciences.";
        } else {
            return "Hint: An important location on campus.";
        }
    }

    private void submitGuess() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        if (hintTimer != null) {
            hintTimer.stop();
        }

        if (actualLocation == null || guessedLocation == null) {
            JOptionPane.showMessageDialog(frame, "Please click on the map to make a guess first!", "No Guess", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double pixelDistance = actualLocation.distance(guessedLocation);

        int basePoints = 1000;
        int distancePenalty = (int)(pixelDistance * 0.5);
        int timeBonus = timeLeft * 3;

        int points = Math.max(0, basePoints - distancePenalty) + timeBonus;
        totalScore += points;

        String message = String.format("\uD83C\uDFAF ROUND %d RESULTS\n\n" +
                        "✅ Correct Location: %s\n" +
                        "\uD83D\uDCCD Your Guess: %.0f pixels away\n" +
                        "⭐ Points Earned: %d points\n" +
                        "\uD83C\uDFC6 Total Score: %d points",
                currentRound, currentLocation, pixelDistance, points, totalScore);

        JOptionPane.showMessageDialog(frame, message, "Round Results", JOptionPane.INFORMATION_MESSAGE);

        currentRound++;
        roundLabel.setText(currentRound + "/" + totalRounds);
        scoreLabel.setText(String.valueOf(totalScore));
        guessedLocation = null;
        loadNewRound();
    }

    private void timeExpired() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        if (hintTimer != null) {
            hintTimer.stop();
        }

        JOptionPane.showMessageDialog(frame,
                "⏰ Time's Up!\n\nCorrect Location: " + currentLocation + "\n\nNo points earned for this round.",
                "Time Expired", JOptionPane.WARNING_MESSAGE);

        currentRound++;
        roundLabel.setText(currentRound + "/" + totalRounds);
        guessedLocation = null;
        loadNewRound();
    }

    private void endGame() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        if (hintTimer != null) {
            hintTimer.stop();
        }

        String performance;
        if (totalScore > 5000) {
            performance = "\uD83C\uDFC6 EXCELLENT WORK!";
        } else if (totalScore > 3000) {
            performance = "\uD83C\uDF89 GOOD JOB!";
        } else {
            performance = "\uD83D\uDC4D NICE TRY!";
        }

        String message = String.format("%s\n\n" +
                        "Player: %s\n" + // Added username
                        "\uD83C\uDFAF Final Score: %d points\n" +
                        "\uD83D\uDD22 Rounds Completed: %d/%d\n\n" +
                        "Thanks for playing COMSATS Geo Guesser!",
                performance, username, totalScore, currentRound - 1, totalRounds);

        Object[] options = {"Back to Main Menu", "Ranking", "Exit"};
        int choice = JOptionPane.showOptionDialog(frame, message, "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: // "Back to Main Menu"
                frame.dispose(); // Close current game frame
                // In a full application, you'd navigate back to a main menu.
                // For this example, we'll just exit after showing a message.
                JOptionPane.showMessageDialog(null, "Returning to main menu (functionality to be implemented).", "Main Menu", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
                break;
            case 1: // "Ranking"
                // In a full application, you'd open a ranking/leaderboard window.
                // Save the current player's score before showing rankings
                GameRanking.addScore(username, totalScore);
                new GameRanking(); // Create and show the GameRanking window
                frame.dispose(); // Close the MainGame window
// No System.exit(0) here, as GameRanking will handle its own exit or return to MainMenu
                break;
            case 2: // "Exit"
            case JOptionPane.CLOSED_OPTION: // User clicked the 'x' to close dialog
                frame.dispose();
                System.exit(0);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGame());
    }
}