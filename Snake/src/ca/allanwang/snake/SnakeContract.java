package ca.allanwang.snake;

import java.awt.*;

/**
 * Created by Allan Wang on 2017-05-12.
 * <p>
 * Snake interaction with the game
 */
public interface SnakeContract {

    void draw(Graphics g);

    void step();

    void score(int points);

    int getScore();

    SnakeQueue getPositions();

    void onKeyPress(int direction);

    void terminate(Window window, int flag);

    boolean isDead();

}
