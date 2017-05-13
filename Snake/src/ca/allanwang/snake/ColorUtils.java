package ca.allanwang.snake;


import java.awt.*;

/**
 * Created by Allan Wang on 2017-05-12.
 */
public class ColorUtils {

    public static final Color APPLE = Color.RED,
            BORDER = Color.WHITE,
            BACKGROUND = Color.BLACK;

    public static Color tint(Color color) {
        return color.darker();
    }

}
