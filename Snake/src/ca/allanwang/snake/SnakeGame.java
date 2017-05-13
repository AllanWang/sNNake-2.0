package ca.allanwang.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Allan Wang on 2017-05-12.
 */

public class SnakeGame extends JPanel implements KeyListener, SnakeGameContract {

    //variables for Window
    private final static int WINDOW_HEIGHT_DP = 100, WINDOW_WIDTH_DP = 100, DELAY = 100,
            BORDER_DP = 2, SCORE_BOARD_HEIGHT_DP = 5, PLAYER_COUNT = 2, CPU_COUNT = 0,
            SNAKE_COUNT = PLAYER_COUNT + CPU_COUNT;

    final static int GAME_HEIGHT_DP = WINDOW_HEIGHT_DP - BORDER_DP * 3 - SCORE_BOARD_HEIGHT_DP,
            GAME_WIDTH_DP = WINDOW_WIDTH_DP - BORDER_DP * 2,
            BLOCK_SIZE = 10,
            INIT_BORDER_DP = Math.min(GAME_HEIGHT_DP / 5, GAME_WIDTH_DP / 5);

    // actual coordinates
    final static int WINDOW_HEIGHT = WINDOW_HEIGHT_DP * BLOCK_SIZE,
            WINDOW_WIDTH = WINDOW_WIDTH_DP * BLOCK_SIZE,
            BORDER = BORDER_DP * BLOCK_SIZE,
            SCORE_BOARD_HEIGHT = SCORE_BOARD_HEIGHT_DP * BLOCK_SIZE,
            GAME_HEIGHT = GAME_HEIGHT_DP * BLOCK_SIZE,
            GAME_WIDTH = GAME_WIDTH_DP * BLOCK_SIZE,
            SCORE_OFFSETS_X = GAME_WIDTH / SNAKE_COUNT,
            SCORE_OFFSET_Y = BORDER * 2 + GAME_HEIGHT;

    // directions
    final static int NONE = -3, UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    // map data
    final static int MAP_INVALID = -9, MAP_EMPTY = 0, MAP_APPLE = -1, MAP_SNAKE_MOD = 10, MAP_SNAKE_MASK = MAP_SNAKE_MOD * 10, MAP_HEAD_MASK = MAP_SNAKE_MOD * 11;
    // flags for movement
    final static int FLAG_SCORE_APPLE = 1, FLAG_SCORE_CAPTURED_SNAKE = 10;
    // flags for termination
    final static int FLAG_TERMINATE_WALL = -1;

    private final Snake[] snakes = new Snake[SNAKE_COUNT];
    private int snakeCount = SNAKE_COUNT, applesToSpawn = SNAKE_COUNT;
    private final C[] apples = new C[SNAKE_COUNT];
    private final int[][] map = new int[GAME_HEIGHT_DP][GAME_WIDTH_DP];

    //in game stuff
    private boolean gameCont = false;
    private boolean pause = false;

    private JFrame window;
    private Font scoreFont = new Font("Helvetica", Font.PLAIN, 20),
            statusFont = new Font("Helvetica", Font.BOLD, 60);


    @Override
    protected void paintComponent(Graphics g) {

        // refresh board
        super.paintComponent(g);
//        g.drawRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        setBackground(ColorUtils.BACKGROUND);
        g.setFont(scoreFont);

        if (gameCont) {
            // drawScore game field
            g.setColor(ColorUtils.BORDER);
            g.drawRect(BORDER, BORDER, GAME_WIDTH, GAME_HEIGHT);

            for (int y = 0; y < map.length; y++)
                for (int x = 0; x < map[y].length; x++) {
                    int i = map[y][x];
                    if (i == MAP_EMPTY) continue;
                    if (i == MAP_APPLE)
                        drawBlock(g, x, y, ColorUtils.APPLE);
                    else if ((i & MAP_SNAKE_MASK) == MAP_SNAKE_MASK) {
                        Snake snake = snakes[i % MAP_SNAKE_MOD];
                        if (snake.isDead())
                            map[y][x] = MAP_EMPTY;
                        else {
                            Color color = snake.getColor();
                            if ((i & MAP_HEAD_MASK) != MAP_HEAD_MASK)  // not the head; darken
                                color.darker();
                            drawBlock(g, x, y, color);
                        }
                    }
                }
        } else {
            // game over
            String message = "GAME OVER";
            g.setFont(statusFont);
            FontMetrics metr = getFontMetrics(g.getFont());
            g.setColor(Color.GREEN);
            g.drawString(message, (GAME_WIDTH - metr.stringWidth(message)) / 2, GAME_HEIGHT / 2 - g.getFont().getSize());
        }

        for (Snake s : snakes)
            s.drawScore(g);
    }

    private void drawBlock(Graphics g, C coord, Color color) {
        drawBlock(g, coord.x, coord.y, color);
    }

    private void drawBlock(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void logDimensions() {
        //TODO
    }


    public SnakeGame() {
        window = new JFrame();
        window.setTitle("sNNake 2.0");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setSize(WINDOW_WIDTH + BORDER, WINDOW_HEIGHT);
        window.add(this);
        window.addKeyListener(this);
        gameReset();
    }

    private void gameReset() {
        makeSnakes();
        spawnApples();
        if (!gameCont) {
            gameCont = true;
            gameLoop();
        }
    }

    private void gameLoop() {
        try {
            while (gameCont) {
                moveAndUpdate();
                spawnApples();
                repaint();
                while (pause)
                    Thread.sleep(DELAY);
                Thread.sleep(DELAY);
            }
        } catch (InterruptedException e) {
            print("InterruptedException %s", e.getMessage());
            gameCont = false;
        }
        // display end game
        repaint();
    }

    public static void main(String args[]) throws InterruptedException {
        new SnakeGame();
    }

    private void makeSnakes() {
        for (int i = 0; i < snakes.length; i++)
            snakes[i] = new Snake(i, 5, window);
    }

    private void spawnApples() {
        while (applesToSpawn > 0) {
            C apple = spawnApple();
            map[apple.y][apple.x] = MAP_APPLE;
            applesToSpawn--;
        }
    }

    private C spawnApple() {
        C c;
        do {
            c = randomC();
        } while (!validateApple(c));
        return c;
    }

    /**
     * Apple should not generate within 3 dp from a snake head and should not rest on the snake's body
     * Apple should also not be on top of another existing apple
     *
     * @param c apple position
     * @return true if valid, false otherwise
     */
    private boolean validateApple(C c) {
        if (map[c.y][c.x] != MAP_EMPTY) return false;
        for (Snake s : snakes)
            if (!s.isDead())
                if (c.isWithin(s.getHead(), 3))
                    return false;
        return true;
    }

    private C randomC() {
        int x = (int) (Math.random() * GAME_WIDTH_DP);
        int y = (int) (Math.random() * GAME_HEIGHT_DP);
        return new C(x, y);
    }

    private void moveAndUpdate() {
        for (int i = 0; i < 3; i++)
            for (Snake s : snakes)
                s.step(map, i, this);
        gameCont = false;
        for (Snake s : snakes)
            gameCont |= !s.isDead();
    }


    static void print(String s, Object... o) {
        System.out.println(String.format(s, o));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                pause = !pause;
                break;
            case KeyEvent.VK_ENTER:
                if (!gameCont)
                    gameReset();
                break;
            case KeyEvent.VK_R:
                if (e.isControlDown())
                    gameReset();
                break;
            case KeyEvent.VK_C:
                if (e.isControlDown()) {
                    window.setVisible(false);
                    window.dispose();
                    System.exit(0);
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }

    @Override
    public void sendSnakeStatus(int snakeId, int mapFlag) {
        if (mapFlag == MAP_EMPTY) return;
        if (mapFlag == MAP_APPLE)
            snakes[snakeId].score(FLAG_SCORE_APPLE);
        else if ((mapFlag & MAP_SNAKE_MASK) == MAP_SNAKE_MASK) { // snake collision
            int otherSnake = mapFlag % MAP_SNAKE_MOD;
            snakes[snakeId].terminate(window, otherSnake);
            if ((mapFlag & MAP_HEAD_MASK) == MAP_HEAD_MASK)  // other snake died as well
                snakes[otherSnake].terminate(window, snakeId);
            else snakes[otherSnake].score(FLAG_SCORE_CAPTURED_SNAKE);
        }

    }
}
