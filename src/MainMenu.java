import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainMenu extends JFrame implements ActionListener {
    private JPanel mainContainer;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel versionLabel;
    private JPanel buttonContainer;
    private JButton startButton;
    private JButton leaderboardButton;
    private JButton tutorialButton;
    private JButton exitButton;
    private BufferedImage backgroundImage;
    private final Color PRIMARY_BLUE = new Color(40, 124, 253);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color WARNING_ORANGE = new Color(249, 115, 22);
    private final Color DANGER_RED = new Color(239, 68, 68);
    private final Color DARK_BG = new Color(15, 23, 42);
    private final Color CARD_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private final Color TEXT_SECONDARY = new Color(148, 163, 184);

    public MainMenu() {
        this.loadBackgroundImage();
        this.initComponents();
        this.setupLayout();
        this.setTitle("COMSATS Geo Guesser");
        this.setSize(1920, 1080);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void loadBackgroundImage() {
        try {
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            String[] possiblePaths = new String[]{"background.jpg", "background.png", "src/background.jpg", "src/background.png", "./background.jpg", "./background.png"};

            for(String path : possiblePaths) {
                File imageFile = new File(path);
                System.out.println("Trying to load: " + imageFile.getAbsolutePath());
                System.out.println("File exists: " + imageFile.exists());
                if (imageFile.exists()) {
                    this.backgroundImage = ImageIO.read(imageFile);
                    System.out.println("Background image loaded successfully from: " + path);
                    return;
                }
            }

            System.out.println("No background image found in any of the expected locations.");
            System.out.println("Please place 'background.jpg' or 'background.png' in your project directory.");
            this.backgroundImage = null;
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
            this.backgroundImage = null;
        }
    }

    private void initComponents() {
        this.mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (MainMenu.this.backgroundImage != null) {
                    g2d.drawImage(MainMenu.this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(0.0F, 0.0F, MainMenu.this.DARK_BG, (float)this.getWidth(), (float)this.getHeight(), MainMenu.this.CARD_BG);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                }
                g2d.dispose();
            }
        };
        this.mainContainer.setLayout(new BorderLayout());

        this.titleLabel = new JLabel("COMSATS Geo Guesser", JLabel.CENTER);
        this.titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        this.titleLabel.setForeground(this.TEXT_PRIMARY);

        this.subtitleLabel = new JLabel("Explore Your Campus Like Never Before", JLabel.CENTER);
        this.subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        this.subtitleLabel.setForeground(this.TEXT_SECONDARY);

        this.buttonContainer = new JPanel();
        this.buttonContainer.setLayout(new BoxLayout(this.buttonContainer, BoxLayout.Y_AXIS));
        this.buttonContainer.setOpaque(false);

        this.startButton = this.createButton("START GAME", this.PRIMARY_BLUE);
        this.leaderboardButton = this.createButton("LEADERBOARD", this.SUCCESS_GREEN);
        this.tutorialButton = this.createButton("HOW TO PLAY", this.WARNING_ORANGE);
        this.exitButton = this.createButton("EXIT GAME", this.DANGER_RED);

        this.startButton.addActionListener(this);
        this.leaderboardButton.addActionListener(this);
        this.tutorialButton.addActionListener(this);
        this.exitButton.addActionListener(this);

        this.buttonContainer.add(Box.createVerticalStrut(20));
        this.buttonContainer.add(this.startButton);
        this.buttonContainer.add(Box.createVerticalStrut(15));
        this.buttonContainer.add(this.leaderboardButton);
        this.buttonContainer.add(Box.createVerticalStrut(15));
        this.buttonContainer.add(this.tutorialButton);
        this.buttonContainer.add(Box.createVerticalStrut(15));
        this.buttonContainer.add(this.exitButton);

        this.versionLabel = new JLabel("Version 1.0.1 - Campus Edition", JLabel.CENTER);
        this.versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.versionLabel.setForeground(this.TEXT_SECONDARY);
    }

    private JButton createButton(String text, final Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = this.getWidth();
                int h = this.getHeight();
                Color darkColor = new Color((int)((double)color.getRed() * 0.8), (int)((double)color.getGreen() * 0.8), (int)((double)color.getBlue() * 0.8));
                GradientPaint gradient = new GradientPaint(0.0F, 0.0F, color, 0.0F, (float)h, darkColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, h, 12, 12);
                g2d.setColor(new Color(255, 255, 255, 51));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.setFont(this.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(this.getText());
                int textH = fm.getAscent();
                g2d.drawString(this.getText(), (w - textW) / 2, (h + textH) / 2 - 2);
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(this.titleLabel, BorderLayout.CENTER);
        titlePanel.add(this.subtitleLabel, BorderLayout.SOUTH);

        header.add(titlePanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        footer.add(this.versionLabel, BorderLayout.CENTER);

        this.mainContainer.add(header, BorderLayout.NORTH);
        this.mainContainer.add(this.buttonContainer, BorderLayout.CENTER);
        this.mainContainer.add(footer, BorderLayout.SOUTH);
        this.add(this.mainContainer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.startButton) {
            new MainGame();
            this.dispose();
        } else if (e.getSource() == this.leaderboardButton) {
            new GameRanking();
            this.dispose();
        } else if (e.getSource() == this.tutorialButton) {
            new HowToPlay();
            this.dispose();
        } else if (e.getSource() == this.exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainMenu();
        });
    }
}