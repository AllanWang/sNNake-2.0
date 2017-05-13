package ca.allanwang.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * Created by Allan Wang on 2017-05-12.
 */

public class SnakeGame extends JPanel implements ActionListener {

    //variables for Window
    private final static int WINDOW_HEIGHT_DP = 100, WINDOW_WIDTH_DP = 100, DELAY = 150,
            BORDER_DP = 2, SCORE_BOARD_HEIGHT_DP = 5, PLAYER_COUNT = 1, CPU_COUNT = 0,
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

    final static int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    //flags for movement
    final static int SCORE_APPLE = 1, SCORE_CAPTURED_SNAKE = 10;
    //flags for termination
    final static int FLAG_TERMINATE_WALL = -1;
    // for apple
    private static int appleX;
    private static int appleY;

    private final Snake[] snakes = new Snake[SNAKE_COUNT];
    private int snakeCount = SNAKE_COUNT;
    private final C[] apples = new C[SNAKE_COUNT];

    //in game stuff
    private static boolean gameCont = true;
    private static boolean pause = false;

    // directions
    private static boolean up = false;
    private static boolean down = false;
    private static boolean left = false;
    private static boolean right = true;

    JFrame window;


    @Override
    protected void paintComponent(Graphics g) {

        // refresh board
        super.paintComponent(g);
        setBackground(ColorUtils.BACKGROUND);

        // set font
        Font font = new Font("Helvetica", Font.BOLD, 50);
        g.setFont(font);
        FontMetrics metr = getFontMetrics(font);

        if (gameCont) {
            // draw game field
            g.setColor(ColorUtils.GAME_BACKGROUND);
            g.drawRect(BORDER, BORDER, GAME_WIDTH, GAME_HEIGHT);

            // draw apples
            g.setColor(Color.RED);
            for (C c : apples)
                g.fillRect(c.x(), c.y(), BLOCK_SIZE, BLOCK_SIZE);

            // draw snakes
            for (Snake s : snakes)
                s.draw(g);
        } else {
            // game over
            String message = "GAME OVER";
            g.setColor(Color.GREEN);
            g.drawString(message, (GAME_WIDTH - metr.stringWidth(message)) / 2, GAME_HEIGHT / 2 - font.getSize());

        }
    }

    public SnakeGame() throws InterruptedException {
        window = new JFrame();
        window.setTitle("sNNake 2.0");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.add(this);
        makeSnakes();
        spawnApples();

        while (gameCont) {
            checkAndMove();
            spawnApples();
            repaint();
            if (pause)
                while (pause)
                    Thread.sleep(DELAY);
            Thread.sleep(DELAY);
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
        for (int i = 0; i < apples.length; i++)
            if (apples[i] == null)
                apples[i] = spawnApple();
    }

    private C spawnApple() {
        C c;
        do {
            c = randomC();
        } while (!validateApple(c));
        return c;
    }

    /**
     * Apple should not generate within 2 dp from a snake head and should not rest on the snake's body
     * Apple should also not be on top of another existing apple
     *
     * @param c apple position
     * @return true if valid, false otherwise
     */
    private boolean validateApple(C c) {
        for (Snake s : snakes)
            if (!s.isDead()) {
                SnakeQueue p = s.getPositions();
                C head = p.getFirst();
                int headProximity = 2;
                if (Math.abs(head.x - c.x) < headProximity && Math.abs(head.y - c.y) < headProximity)
                    return false;
                for (C sp : p)
                    if (sp.equals(c)) return false;
            }
        for (C a : apples)
            if (c.equals(a)) return false;
        return true;
    }

    private C randomC() {
        int x = (int) (Math.random() * GAME_WIDTH);
        int y = (int) (Math.random() * GAME_HEIGHT);
        return new C(x, y);
    }

    private void checkAndMove() {
        snakeLoop:
        for (int i = 0; i < snakes.length; i++) {
            Snake s = snakes[i];
            if (s.isDead()) continue;
            C head = s.getPositions().getFirst();
            // check for walls
            if (head.x < 0 || head.x > GAME_WIDTH || head.y < 0 || head.y > GAME_HEIGHT) {
                s.terminate(window, FLAG_TERMINATE_WALL);
                continue;
            }
            // check for apples; apples cannot also have snake body; that isn't possible
            for (int j = 0; j < apples.length; j++) {
                if (head.equals(apples[j])) {
                    apples[j] = null;
                    s.score(SCORE_APPLE);
                    s.step();
                    continue snakeLoop;
                }
            }
            // check for head collision against other snakes
            for (int j = 0; j < snakes.length; j++) {
                if (snakes[j].isDead()) continue;
                Iterator<C> iter = snakes[j].getPositions().iterator();
                if (i == j && iter.hasNext()) iter.next(); // same snake; don't compare head
                while (iter.hasNext()) {
                    if (head.equals(iter.next())) {
                        s.terminate(window, j);
                        snakes[j].score(SCORE_CAPTURED_SNAKE);
                        continue snakeLoop;
                    }
                }
            }
            //nothing happened; just move
            s.step();
        }
        gameCont = false;
        for (Snake s : snakes)
            gameCont |= !s.isDead();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }

    private void print(String s, Object... o) {
        System.out.println(String.format(s, o));
    }
}
