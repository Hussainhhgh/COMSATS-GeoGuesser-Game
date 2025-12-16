import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CampusExplorer extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private JTextField usernameField;
    private JLabel gameUsername, scoreLabel, timerLabel, imageLabel, mapLabel;
    private JButton hintButton, submitButton;
    private Timer gameTimer;

    private int score = 0, timeLeft = 30;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(CampusExplorer::new);
    }

    public CampusExplorer() {
        setTitle("Campus Explorer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        setupStartScreen();
        setupGameScreen();
        setupResultScreen();

        add(mainPanel);
        setVisible(true);
    }

    private void setupStartScreen() {
        JPanel start = new JPanel(new GridBagLayout());
        start.setBackground(new Color(20, 20, 20));

        JLabel title = new JLabel("🏫 Campus Explorer");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JButton startBtn = new JButton("Start Game");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        startBtn.addActionListener(e -> {
            if (usernameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a username.");
            } else {
                gameUsername.setText("👤 " + usernameField.getText());
                score = 0;
                scoreLabel.setText("Score: 0");
                timeLeft = 30;
                timerLabel.setText("⏱ " + timeLeft + "s");
                cardLayout.show(mainPanel, "game");
                startGameTimer();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        start.add(title, gbc);
        gbc.gridy++;
        start.add(usernameField, gbc);
        gbc.gridy++;
        start.add(startBtn, gbc);

        mainPanel.add(start, "start");
    }

    private void setupGameScreen() {
        JPanel game = new JPanel(new BorderLayout());
        game.setBackground(new Color(30, 30, 30));

        // Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        topBar.setBackground(new Color(40, 40, 40));
        gameUsername = new JLabel("👤 Player");
        scoreLabel = new JLabel("Score: 0");
        timerLabel = new JLabel("⏱ 30s");
        for (JLabel label : new JLabel[]{gameUsername, scoreLabel, timerLabel}) {
            label.setFont(new Font("SansSerif", Font.BOLD, 20));
            label.setForeground(Color.WHITE);
            topBar.add(label);
        }

        // Game Center
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.setBackground(new Color(30, 30, 30));
        imageLabel = new JLabel("📷 Image Placeholder", SwingConstants.CENTER);
        mapLabel = new JLabel("🗺️ Map Placeholder", SwingConstants.CENTER);
        for (JLabel l : new JLabel[]{imageLabel, mapLabel}) {
            l.setFont(new Font("SansSerif", Font.BOLD, 24));
            l.setOpaque(true);
            l.setBackground(new Color(50, 50, 50));
            l.setForeground(Color.LIGHT_GRAY);
        }
        center.add(imageLabel);
        center.add(mapLabel);

        // Bottom Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        bottom.setBackground(new Color(25, 25, 25));
        hintButton = new JButton("💡 Hint (-50)");
        submitButton = new JButton("✅ Submit Guess");

        hintButton.addActionListener(e -> {
            if (score >= 50) {
                score -= 50;
                scoreLabel.setText("Score: " + score);
                JOptionPane.showMessageDialog(this, "Hint: It's near the central building!");
            } else {
                JOptionPane.showMessageDialog(this, "Not enough score for a hint.");
            }
        });

        submitButton.addActionListener(e -> {
            score += new Random().nextInt(100) + 100;
            scoreLabel.setText("Score: " + score);
            JOptionPane.showMessageDialog(this, "You guessed! New image loaded.");
            timeLeft = 30;
        });

        for (JButton b : new JButton[]{hintButton, submitButton}) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 16));
            bottom.add(b);
        }

        game.add(topBar, BorderLayout.NORTH);
        game.add(center, BorderLayout.CENTER);
        game.add(bottom, BorderLayout.SOUTH);

        mainPanel.add(game, "game");
    }

    private void setupResultScreen() {
        JPanel result = new JPanel(new GridBagLayout());
        result.setBackground(new Color(20, 20, 20));

        JLabel endText = new JLabel("🎉 Game Over!");
        endText.setFont(new Font("SansSerif", Font.BOLD, 36));
        endText.setForeground(Color.GREEN);

        JLabel finalScore = new JLabel();
        finalScore.setFont(new Font("SansSerif", Font.PLAIN, 28));
        finalScore.setForeground(Color.CYAN);

        JButton playAgain = new JButton("🔁 Play Again");
        playAgain.setFont(new Font("SansSerif", Font.BOLD, 18));
        playAgain.addActionListener(e -> {
            cardLayout.show(mainPanel, "start");
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        result.add(endText, gbc);
        gbc.gridy++;
        result.add(finalScore, gbc);
        gbc.gridy++;
        result.add(playAgain, gbc);

        mainPanel.add(result, "result");

        // Store reference to set score when game ends
        this.resultScoreLabel = finalScore;
    }

    private JLabel resultScoreLabel;

    private void startGameTimer() {
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏱ " + timeLeft + "s");
            if (timeLeft <= 0) {
                gameTimer.stop();
                showResults();
            }
        });
        gameTimer.start();
    }

    private void showResults() {
        resultScoreLabel.setText("Your Score: " + score);
        cardLayout.show(mainPanel, "result");
    }
}
