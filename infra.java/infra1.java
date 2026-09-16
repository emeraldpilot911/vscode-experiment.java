import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class infra1 {
    public static void main(String[] args) {
        int width = 500;
        int height = 500;

        // Create an off-screen image buffer
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smooth curves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Background
        g2d.setColor(new Color(220, 245, 220)); // Soft jungle green
        g2d.fillRect(0, 0, width, height);

        // Palette
        Color furColor      = new Color(110, 60, 30);      // Dark brown
        Color skinColor     = new Color(235, 195, 150);   // Tan/Peach
        Color innerEarColor = new Color(220, 160, 150);   // Soft pink
        Color outlineColor  = new Color(60, 30, 15);

        // 2. Ears (Outer & Inner)
        g2d.setColor(furColor);
        g2d.fillOval(70, 180, 100, 100);  // Left outer
        g2d.fillOval(330, 180, 100, 100); // Right outer

        g2d.setColor(innerEarColor);
        g2d.fillOval(85, 195, 70, 70);    // Left inner
        g2d.fillOval(345, 195, 70, 70);   // Right inner

        // 3. Head (Fur Base)
        g2d.setColor(furColor);
        g2d.fillOval(120, 100, 260, 280);

        // 4. Face Patch (Skin)
        g2d.setColor(skinColor);
        g2d.fillOval(160, 170, 110, 110); // Eye mask (left)
        g2d.fillOval(230, 170, 110, 110); // Eye mask (right)
        g2d.fillOval(165, 220, 170, 130); // Muzzle

        // 5. Eyes
        g2d.setColor(Color.WHITE);
        g2d.fillOval(190, 195, 40, 50);   // Left sclera
        g2d.fillOval(270, 195, 40, 50);   // Right sclera

        g2d.setColor(Color.BLACK);
        g2d.fillOval(205, 210, 20, 25);   // Left pupil
        g2d.fillOval(275, 210, 20, 25);   // Right pupil

        // Pupil highlights
        g2d.setColor(Color.WHITE);
        g2d.fillOval(210, 212, 7, 7);
        g2d.fillOval(280, 212, 7, 7);

        // 6. Nostrils
        g2d.setColor(outlineColor);
        g2d.fillOval(232, 265, 12, 16);
        g2d.fillOval(256, 265, 12, 16);

        // 7. Smile
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawArc(210, 260, 80, 50, 200, 140);

        g2d.dispose();

        // 8. Export to File
        try {
            File outputFile = new File("monkey.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Monkey image successfully saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
        }
    }
}