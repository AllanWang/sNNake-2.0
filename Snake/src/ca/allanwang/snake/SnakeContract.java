package ca.allanwang.snake;

import java.awt.*;

/**
 * Created by Allan Wang on 2017-05-12.
 * <p>
 * Snake interaction with the game
 */
public interface SnakeContract {

    void drawScore(Graphics g);

    Color getColor();

    C getHead();

    /**
     * Move head and send status updates to the game
     *
     * @param map      data map
     * @param stage    the loop number
     * @param callback status updates for the game
     */
    void step(int[][] map, int stage, SnakeGameContract callback);

    void score(int points);

    int getScore();

    void onKeyPress(int direction);

    void terminate(Window window, int flag);

    boolean isDead();

}
