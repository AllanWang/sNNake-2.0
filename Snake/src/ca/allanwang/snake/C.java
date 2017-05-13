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
    public int hashCode() {
        return x << 16 + y;
    }

    public boolean isWithin(C c, int proximity) {
        return Math.abs(c.x - x) < proximity && Math.abs(c.y - y) < proximity;
    }

    public int set(int[][] map, int toSet) {
        int original = get(map);
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length)
            return SnakeGame.MAP_INVALID;
        map[y][x] = toSet;
        return original;
    }

    public int get(int[][] map) {
        return map[y][x];
    }

    @Override
    public String toString() {
        return String.format("C(%d, %d)", x, y);
    }
}
