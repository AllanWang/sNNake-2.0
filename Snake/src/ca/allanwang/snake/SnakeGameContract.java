package ca.allanwang.snake;

/**
 * Created by Allan Wang on 2017-05-13.
 * <p>
 * Callbacks that Snakes can send to the game
 */
public interface SnakeGameContract {

    /**
     * Emits a flag to the game
     *
     * @param snakeId id of snake sending the flag
     * @param mapFlag the previous map value of the new head position
     */
    void sendSnakeStatus(int snakeId, int mapFlag);

}
