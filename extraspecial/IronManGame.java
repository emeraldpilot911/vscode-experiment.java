import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class IronManGame extends JPanel implements ActionListener, KeyListener {

    // Screen dimensions
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;

    // Game loop timer
    private final Random random = new Random();

    // Player (Iron Man) state
    private int playerX = 100;
    private int playerY = 250;
    private final int playerWidth = 60;
    private final int playerHeight = 30;
    private final int playerSpeed = 6;
    private int powerLevel = 100;
    private int score = 0;
    private boolean gameOver = false;

    // Key input flags
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean shootPressed = false;
    private long lastShotTime = 0;

    // Generic Lists for game objects
    private final List<Beam> beams = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();

    public IronManGame() {
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
    }

    private void startGameLoop() {
        addKeyListener(this);
        Timer gameTimer = new Timer(16, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updatePlayer();
            updateBeams();
            spawnEnemies();
            updateEnemies();
            updateParticles();
            checkCollisions();
        }
        repaint();
    }

    private void updatePlayer() {
        if (upPressed && playerY > 30) playerY -= playerSpeed;
        if (downPressed && playerY < GAME_HEIGHT - playerHeight - 30) playerY += playerSpeed;
        if (leftPressed && playerX > 20) playerX -= playerSpeed;
        if (rightPressed && playerX < GAME_WIDTH / 2) playerX += playerSpeed;

        // Shoot Repulsor Beam (cooldown 200ms)
        if (shootPressed && System.currentTimeMillis() - lastShotTime > 200) {
            beams.add(new Beam(playerX + playerWidth, playerY + playerHeight / 2 - 4));
            lastShotTime = System.currentTimeMillis();
        }
    }

    private void updateBeams() {
        Iterator<Beam> it = beams.iterator();
        while (it.hasNext()) {
            Beam b = it.next();
            b.x += 12; // Beam speed
            if (b.x > GAME_WIDTH) {
                it.remove();
            }
        }
    }

    private void spawnEnemies() {
        // Spawn Hydra Drones periodically
        if (random.nextInt(100) < 3) {
            int enemyY = random.nextInt(GAME_HEIGHT - 100) + 30;
            enemies.add(new Enemy(GAME_WIDTH, enemyY));
        }
    }

    private void updateEnemies() {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();
            enemy.x -= enemy.speed;
            if (enemy.x < -enemy.width) {
                it.remove();
            }
        }
    }

    private void updateParticles() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.alpha <= 0) {
                it.remove();
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        // Beam hits Enemy
        List<Beam> beamsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Beam b : beams) {
            Rectangle beamBounds = new Rectangle(b.x, b.y, b.width, b.height);
            for (Enemy e : enemies) {
                if (enemiesToRemove.contains(e)) continue;

                Rectangle enemyBounds = new Rectangle(e.x, e.y, e.width, e.height);
                if (beamBounds.intersects(enemyBounds)) {
                    createExplosion(e.x + e.width / 2, e.y + e.height / 2);
                    beamsToRemove.add(b);
                    enemiesToRemove.add(e);
                    score += 100;
                    break;
                }
            }
        }

        beams.removeAll(beamsToRemove);
        enemies.removeAll(enemiesToRemove);

        // Enemy hits Iron Man
        enemiesToRemove.clear();
        for (Enemy e : enemies) {
            Rectangle enemyBounds = new Rectangle(e.x, e.y, e.width, e.height);
            if (playerBounds.intersects(enemyBounds)) {
                createExplosion(e.x, e.y);
                enemiesToRemove.add(e);
                powerLevel -= 20;

                if (powerLevel <= 0) {
                    powerLevel = 0;
                    gameOver = true;
                }
            }
        }
        enemies.removeAll(enemiesToRemove);
    }

    private void createExplosion(int x, int y) {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Background Night Sky / Stars
        g2d.setColor(new Color(10, 15, 30));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        if (!gameOver) {
            // Draw Iron Man Thruster Particles
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(playerX - 10, playerY + 10, 10, 10);
            g2d.setColor(Color.CYAN);
            g2d.fillRect(playerX - 18, playerY + 12, 8, 6);

            // Draw Iron Man Body (Red Armor)
            g2d.setColor(new Color(180, 0, 0));
            g2d.fillRoundRect(playerX, playerY, playerWidth, playerHeight, 10, 10);

            // Draw Gold Accents (Faceplate / Helmet)
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillRoundRect(playerX + playerWidth - 15, playerY + 4, 15, 12, 5, 5);

            // Draw Arc Reactor (Cyan Glow)
            g2d.setColor(Color.CYAN);
            g2d.fillOval(playerX + 25, playerY + 10, 10, 10);

            // Draw Repulsor Beams
            g2d.setColor(Color.CYAN);
            for (Beam b : beams) {
                g2d.fillRect(b.x, b.y, b.width, b.height);
            }

            // Draw Hydra Enemies
            for (Enemy enemy : enemies) {
                g2d.setColor(new Color(30, 130, 60)); // Hydra Green
                g2d.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
                g2d.setColor(Color.RED); // Eye/Cockpit
                g2d.fillRect(enemy.x + 5, enemy.y + 8, 8, 8);
            }

            // Draw Explosions
            for (Particle p : particles) {
                g2d.setColor(new Color(255, p.colorG, 0, p.alpha));
                g2d.fillOval((int) p.x, (int) p.y, p.size, p.size);
            }

            // Draw HUD (Heads-Up Display)
            drawHUD(g2d);

        } else {
            // Game Over Screen
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.drawString("SUIT CRITICAL - GAME OVER", 60, GAME_HEIGHT / 2 - 30);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            g2d.drawString("Final Score: " + score, GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2 + 20);
            g2d.drawString("Press ENTER to Restart", GAME_WIDTH / 2 - 130, GAME_HEIGHT / 2 + 70);
        }
    }

    private void drawHUD(Graphics2D g2d) {
        // Draw Power Level Bar
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.setColor(Color.CYAN);
        g2d.drawString("SUIT POWER: " + powerLevel + "%", 20, 30);

        g2d.setColor(Color.GRAY);
        g2d.drawRect(160, 15, 150, 15);
        if (powerLevel > 50) g2d.setColor(Color.CYAN);
        else if (powerLevel > 20) g2d.setColor(Color.YELLOW);
        else g2d.setColor(Color.RED);
        g2d.fillRect(161, 16, (int) (1.48 * powerLevel), 14);

        // Draw Score
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, GAME_WIDTH - 150, 30);
    }

    private void restartGame() {
        playerX = 100;
        playerY = 250;
        powerLevel = 100;
        score = 0;
        beams.clear();
        enemies.clear();
        particles.clear();
        gameOver = false;
    }

    // Key Handling
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_DOWN) downPressed = true;
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_SPACE) shootPressed = true;

        if (gameOver && key == KeyEvent.VK_ENTER) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_SPACE) shootPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Inner classes for game elements
    private static class Beam {
        int x, y, width = 18, height = 6;

        Beam(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Enemy {
        int x, y, width = 35, height = 25, speed;

        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
            this.speed = new Random().nextInt(4) + 4; // Variable speed
        }
    }

    private static class Particle {
        double x, y, vx, vy;
        int size, alpha = 255, colorG;

        Particle(int x, int y) {
            Random rand = new Random();
            this.x = x;
            this.y = y;
            this.vx = rand.nextDouble() * 6 - 3;
            this.vy = rand.nextDouble() * 6 - 3;
            this.size = rand.nextInt(6) + 4;
            this.colorG = rand.nextInt(150) + 100;
        }

        void update() {
            x += vx;
            y += vy;
            alpha = Math.max(0, alpha - 12);
        }
    }

    // Main Method to Launch Game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Iron Man: JARVIS Defense Protocol");
            IronManGame gamePanel = new IronManGame();

            frame.add(gamePanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
            gamePanel.startGameLoop();
            gamePanel.requestFocusInWindow();
        });
    }
}