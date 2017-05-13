package ca.allanwang.snake;

/**
 * Created by Allan Wang on 2017-05-12.
 * <p>
 * Coordinates
 */
public class C {

    public final int x, y;

    public C(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x * SnakeGame.BLOCK_SIZE;
    }

    public int y() {
        return y * SnakeGame.BLOCK_SIZE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof C)) return false;
        C o = (C) obj;
        return x == o.x && y == o.y;
    }

    @Override
    public String toString() {
        return String.format("C(%d, %d)", x, y);
    }
}
