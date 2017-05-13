package ca.allanwang.snake;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Allan Wang on 2017-05-12.
 */
public class Snake implements SnakeContract {

    private int lastDirection = SnakeGame.NONE, pendingDirection;
    private Color color;
    private KeyListener keyListener;
    private final int id;
    private int score = 0;
    private boolean isDead = false;
    private int prevHeadValue;
    private SnakeQueue positions;

    public Snake(int id, int size, Window window) {
        this.id = id;
        this.positions = new SnakeQueue(size);
        switch (id) {
            case 0:
                color = Color.GREEN;
                pendingDirection = SnakeGame.RIGHT;
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
            case 1:
                color = Color.CYAN;
                pendingDirection = SnakeGame.LEFT;
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
            case 2: //TODO add more key bindings?
                color = Color.BLUE;
                pendingDirection = SnakeGame.RIGHT;
                positions.add(new C(SnakeGame.INIT_BORDER_DP, SnakeGame.GAME_HEIGHT_DP - SnakeGame.INIT_BORDER_DP));
                break;
            case 3:
                color = Color.ORANGE;
                pendingDirection = SnakeGame.LEFT;
                positions.add(new C(SnakeGame.GAME_WIDTH_DP - SnakeGame.INIT_BORDER_DP, SnakeGame.INIT_BORDER_DP));
                break;
            default:
                throw new IllegalArgumentException("We only support snakes with ids 0 through 3");
        }
        window.addKeyListener(keyListener);
    }

    @Override
    public void drawScore(Graphics g) {
        g.drawString(String.format("Snake %d: %d pts", id + 1, score), SnakeGame.BORDER + SnakeGame.SCORE_OFFSETS_X * id, SnakeGame.SCORE_OFFSET_Y + (SnakeGame.SCORE_BOARD_HEIGHT - g.getFont().getSize()) / 2);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public C getHead() {
        return positions.getHead();
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void terminate(Window window, int flag) {
        isDead = true;
        positions.clear();
        window.removeKeyListener(keyListener);
        keyListener = null;
        print("Dead %d", flag);
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void score(int points) {
        score += points;
        switch (points) {
            case SnakeGame.FLAG_SCORE_APPLE:
                positions.incrementMaxSize();
                break;
            case SnakeGame.FLAG_SCORE_CAPTURED_SNAKE:
                positions.incrementMaxSize(5);
                break;
        }
    }

    @Override
    public void step(int[][] map, int stage, SnakeGameContract callback) {
        if (isDead) return;
        switch (stage) {
            case 0: // move tail
                if (positions.size() >= positions.getMaxSize())
                    positions.remove().set(map, SnakeGame.MAP_EMPTY);
                break;
            case 1: // move head
                //        print(positions.getFirst().toString());
                lastDirection = pendingDirection;
                positions.getHead().set(map, id | SnakeGame.MAP_SNAKE_MASK);  // previous head is now body
                prevHeadValue = positions.move(pendingDirection).set(map, id | SnakeGame.MAP_HEAD_MASK);
                break;
            case 2: // status check
                callback.sendSnakeStatus(id, prevHeadValue);
        }
    }

    @Override
    public void onKeyPress(int direction) {
        if (Math.abs(this.lastDirection - direction) != 2) //cannot go 180
            this.pendingDirection = direction;
    }

    private static void print(String s, Object... o) {
        SnakeGame.print(s, o);
    }

}
