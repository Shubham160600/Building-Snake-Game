package BuildingSnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        initUI();
    }

    private void initUI() {
        add(new GamePanel());
        
        setTitle("Snake Game");
        setResizable(false);
        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SnakeGame();
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements ActionListener {

    private final int PANEL_WIDTH = 600;
    private final int PANEL_HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private final int GAME_UNITS = (PANEL_WIDTH * PANEL_HEIGHT) / UNIT_SIZE;
    private final int DELAY = 100;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];

    private int bodyParts = 3;
    private int foodEaten;
    private int foodX;
    private int foodY;
    
    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private boolean gameOver = false;
    
    private Timer timer;
    private Random random;
    
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public GamePanel() {
        random = new Random();
        
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        
        startGame();
    }

    private void startGame() {
        // Initialize snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * UNIT_SIZE;
            y[i] = 100;
        }
        
        direction = Direction.RIGHT;
        bodyParts = 3;
        foodEaten = 0;
        gameOver = false;
        running = true;
        
        // Create new food
        spawnFood();
        
        // Start game timer
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Optional grid lines
            /*
            for (int i = 0; i < PANEL_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, PANEL_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, PANEL_WIDTH, i * UNIT_SIZE);
            }
            */
            
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
            
            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    // Snake head
                    g.setColor(Color.GREEN);
                } else {
                    // Snake body
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + foodEaten, 
                (PANEL_WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, 
                g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    private void spawnFood() {
        foodX = random.nextInt((PANEL_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((PANEL_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        // Move the body
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        
        // Move the head
        switch (direction) {
            case UP:
                y[0] = y[0] - UNIT_SIZE;
                break;
            case DOWN:
                y[0] = y[0] + UNIT_SIZE;
                break;
            case LEFT:
                x[0] = x[0] - UNIT_SIZE;
                break;
            case RIGHT:
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            foodEaten++;
            spawnFood();
        }
    }

    private void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        
        // Check if head touches right border
        if (x[0] >= PANEL_WIDTH) {
            running = false;
        }
        
        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        
        // Check if head touches bottom border
        if (y[0] >= PANEL_HEIGHT) {
            running = false;
        }
        
        if (!running) {
            timer.stop();
            gameOver = true;
        }
    }

    private void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", 
            (PANEL_WIDTH - metricsGameOver.stringWidth("Game Over")) / 2, 
            PANEL_HEIGHT / 2);
        
        // Score text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, 
            (PANEL_WIDTH - metricsScore.stringWidth("Score: " + foodEaten)) / 2, 
            PANEL_HEIGHT / 2 + 50);
        
        // Restart instruction
        g.setFont(new Font("Arial", Font.BOLD, 25));
        FontMetrics metricsRestart = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to Restart", 
            (PANEL_WIDTH - metricsRestart.stringWidth("Press SPACE to Restart")) / 2, 
            PANEL_HEIGHT / 2 + 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != Direction.RIGHT) {
                        direction = Direction.LEFT;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != Direction.LEFT) {
                        direction = Direction.RIGHT;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != Direction.DOWN) {
                        direction = Direction.UP;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != Direction.UP) {
                        direction = Direction.DOWN;
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (gameOver) {
                        startGame();
                    }
                    break;
            }
        }
    }
}