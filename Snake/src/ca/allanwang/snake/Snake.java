package ca.allanwang.snake;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

/**
 * Created by Allan Wang on 2017-05-12.
 */
public class Snake implements SnakeContract {

    private int direction;
    private Color color;
    private KeyListener keyListener;
    private int size;
    private final int id;
    private int score = 0;
    private boolean isDead = false;

    private SnakeQueue positions;

    public Snake(int id, int size, Window window) {
        this.id = id;
        this.size = size;
        positions = new SnakeQueue(size);
        switch (id + 1) {
            case 1:
                color = Color.GREEN;
                direction = SnakeGame.RIGHT;
                positions.add(new C(SnakeGame.INIT_BORDER_DP, SnakeGame.INIT_BORDER_DP));
                keyListener = new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                onKeyPress(SnakeGame.LEFT);
                                break;
                            case KeyEvent.VK_UP:
                                onKeyPress(SnakeGame.UP);
                                break;
                            case KeyEvent.VK_RIGHT:
                                onKeyPress(SnakeGame.RIGHT);
                                break;
                            case KeyEvent.VK_DOWN:
                                onKeyPress(SnakeGame.DOWN);
                                break;
                        }
                    }
                };
                break;
            case 2:
                color = Color.CYAN;
                direction = SnakeGame.LEFT;
                positions.add(new C(SnakeGame.GAME_WIDTH_DP - SnakeGame.INIT_BORDER_DP, SnakeGame.GAME_HEIGHT_DP - SnakeGame.INIT_BORDER_DP));
                keyListener = new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_A:
                                onKeyPress(SnakeGame.LEFT);
                                break;
                            case KeyEvent.VK_W:
                                onKeyPress(SnakeGame.UP);
                                break;
                            case KeyEvent.VK_D:
                                onKeyPress(SnakeGame.RIGHT);
                                break;
                            case KeyEvent.VK_S:
                                onKeyPress(SnakeGame.DOWN);
                                break;
                        }
                    }
                };
                break;
            case 3:
                color = Color.BLUE;
                direction = SnakeGame.RIGHT;
                positions.add(new C(SnakeGame.INIT_BORDER_DP, SnakeGame.GAME_HEIGHT_DP - SnakeGame.INIT_BORDER_DP));
                break;
            case 4:
                color = Color.ORANGE;
                direction = SnakeGame.LEFT;
                positions.add(new C(SnakeGame.GAME_WIDTH_DP - SnakeGame.INIT_BORDER_DP, SnakeGame.INIT_BORDER_DP));
                break;
            default:
                throw new IllegalArgumentException("We only support snakes 1 through 4");
        }
        window.addKeyListener(keyListener);
    }

    @Override
    public void draw(Graphics g) {
        if (!isDead) {
            Iterator<C> iter = positions.iterator();
            if (iter.hasNext()) {
                g.setColor(color);
                C c = iter.next();
                g.fillRect(c.x(), c.y(), SnakeGame.BLOCK_SIZE, SnakeGame.BLOCK_SIZE);
            }
            g.setColor(ColorUtils.tint(color));
            while (iter.hasNext()) {
                C c = iter.next();
                g.fillRect(c.x(), c.y(), SnakeGame.BLOCK_SIZE, SnakeGame.BLOCK_SIZE);
            }
        }
        g.drawString(String.format("Snake %d: %d pts", id + 1, score), SnakeGame.SCORE_OFFSETS_X * id, SnakeGame.SCORE_OFFSET_Y + (SnakeGame.SCORE_BOARD_HEIGHT - g.getFont().getSize()) / 2);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public SnakeQueue getPositions() {
        return positions;
    }

    @Override
    public void terminate(Window window, int flag) {
        isDead = true;
        positions.clear();
        window.removeKeyListener(keyListener);
        keyListener = null;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void score(int points) {
        score += points;
        switch (points) {
            case SnakeGame.SCORE_APPLE:
                positions.incrementMaxSize();
                break;
            case SnakeGame.SCORE_CAPTURED_SNAKE:
                positions.incrementMaxSize(5);
                break;
        }
    }

    @Override
    public void step() {
        positions.move(direction);
    }

    @Override
    public void onKeyPress(int direction) {
        if (Math.abs(this.direction - direction) != 2) //cannot go 180
            this.direction = direction;
    }

}
