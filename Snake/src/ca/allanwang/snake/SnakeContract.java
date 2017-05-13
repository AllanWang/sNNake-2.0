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

    void onKeyPress(int direction);

    int getScore();

    SnakeQueue getPositions();

    void terminate(Window window, int flag);

    boolean isDead();

}
