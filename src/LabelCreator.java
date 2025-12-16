import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class LabelCreator {

    JFrame frame = new JFrame("Developer Label Tool");
    JLabel label1 = new JLabel(); // for image
    JLabel mapLabel = new JLabel(); // for map
    JButton nextButton = new JButton("Save & Next");

    ArrayList<String> images = new ArrayList<>();
    int currentIndex = 0;
    int clickX = -1, clickY = -1;

    LabelCreator() {
        // Load image paths
        images.add("src/10th.png");

        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Image Panel (left)
        label1.setPreferredSize(new Dimension(1040, 650));
        label1.setOpaque(true);
        label1.setBackground(Color.DARK_GRAY);
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label1, BorderLayout.WEST);

        // Map Panel (center)
        ImageIcon mapIcon = new ImageIcon("src/GEO.png");
        Image scaledMapImage = mapIcon.getImage().getScaledInstance(500, 650, Image.SCALE_SMOOTH);
        mapLabel.setIcon(new ImageIcon(scaledMapImage));
        mapLabel.setPreferredSize(new Dimension(500, 650));
        mapLabel.setOpaque(true);
        mapLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                clickX = e.getX();
                clickY = e.getY();
                JOptionPane.showMessageDialog(frame, "Location selected: (" + clickX + ", " + clickY + ")");
            }
        });
        frame.add(mapLabel, BorderLayout.CENTER);

        // Button Panel (bottom)
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.setBackground(Color.CYAN);
        nextButton.addActionListener(e -> saveAndNext());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(nextButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadImage();
        frame.setVisible(true);
    }

    private void loadImage() {
        if (currentIndex < images.size()) {
            String path = images.get(currentIndex);
            ImageIcon imgIcon = new ImageIcon(path);
            Image scaledImg = imgIcon.getImage().getScaledInstance(1040, 650, Image.SCALE_SMOOTH);
            label1.setIcon(new ImageIcon(scaledImg));
        } else {
            JOptionPane.showMessageDialog(frame, "✅ All images labeled!");
            frame.dispose();
        }
    }

    private void saveAndNext() {
        if (clickX == -1 || clickY == -1) {
            JOptionPane.showMessageDialog(frame, "❗ Please click on the map first!");
            return;
        }

        String imagePath = images.get(currentIndex);
        try (FileWriter fw = new FileWriter("labels.csv", true)) {
            fw.write(imagePath + "," + clickX + "," + clickY + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reset and go to next image
        currentIndex++;
        clickX = clickY = -1;
        loadImage();
    }

    public static void main(String[] args) {
        new LabelCreator();
    }
}
