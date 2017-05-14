package ca.allanwang.snake;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Created by Allan Wang on 2017-05-13.
 */
public class SnakeTest {

    Snake snake;

    @Before
    public void init() {
        snake = new Snake(0, 3, null);
    }

    @Test
    public void badId() {
        try {
            new Snake(5, 3, null);
            fail("Bad id not caught");
        } catch (IllegalArgumentException e) {
            assertEquals("We only support snakes with ids 0 through 3", e.getMessage());
        }
    }

    @Test
    public void badSize() {
        try {
            new Snake(1, 0, null);
            fail("Bad size not caught");
        } catch (IllegalArgumentException e) {
            assertEquals("Max size must be at least 1", e.getMessage());
        }
    }

    @Test
    public void clearQueueOnTerminate() {
        snake.terminate(null, SnakeGame.FLAG_TERMINATE_WALL);
        try {
            snake.getHead();
            fail("Positions not empty after termination");
        } catch (NoSuchElementException e) {
            assertNull(e.getMessage());
        }
    }
}
