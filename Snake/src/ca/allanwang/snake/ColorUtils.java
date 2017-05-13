package ca.allanwang.snake;


import java.awt.*;

/**
 * Created by Allan Wang on 2017-05-12.
 */
public class ColorUtils {

    public static final Color APPLE = Color.RED,
            GAME_BACKGROUND = Color.BLACK,
            BACKGROUND = Color.BLACK.brighter();

    public static Color tint(Color color) {
        return color.darker();
    }

}
