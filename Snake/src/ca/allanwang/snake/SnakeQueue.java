package ca.allanwang.snake;

import java.util.LinkedList;

/**
 * Created by Allan Wang on 2017-05-12.
 * <p>
 * LinkedList with a max size cap
 * Adds to the front by default and trims end until size <= maxSize
 */
public class SnakeQueue extends LinkedList<C> {

    private int maxSize;

    public SnakeQueue(int maxSize) {
        if (maxSize < 1) throw new IllegalArgumentException("Max size must be at least 1");
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(C c) {
        addFirst(c);
        return true;
    }

    @Override
    public C remove() {
        return super.removeLast();
    }

    public C getHead() {
        return getFirst();
    }

    public C move(int direction) {
        C prev = getHead();
        switch (direction) {
            case SnakeGame.UP:
                add(new C(prev.x, prev.y - 1));
                break;
            case SnakeGame.RIGHT:
                add(new C(prev.x + 1, prev.y));
                break;
            case SnakeGame.DOWN:
                add(new C(prev.x, prev.y + 1));
                break;
            case SnakeGame.LEFT:
                add(new C(prev.x - 1, prev.y));
                break;
        }
        return getHead();
    }

    @Override
    public void addFirst(C c) {
        super.addFirst(c);
        trim();
    }

    @Override
    public void addLast(C c) {
        super.addLast(c);
        trim();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        trim();
    }

    public void incrementMaxSize() {
        incrementMaxSize(1);
    }

    public void incrementMaxSize(int i) {
        setMaxSize(maxSize() + i);
    }

    public int maxSize() {
        return maxSize;
    }

    private void trim() {
        while (size() > maxSize)
            removeLast();
    }
}
