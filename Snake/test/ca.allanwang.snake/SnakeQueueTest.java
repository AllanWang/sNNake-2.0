package ca.allanwang.snake;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Allan Wang on 2017-05-13.
 */
public class SnakeQueueTest {

    SnakeQueue queue;

    @Before
    public void init() {
        queue = new SnakeQueue(3);
    }

    @Test
    public void head() {
        C head = new C(2, 3);
        queue.add(head);
        assertEquals("Last item added is head", head, queue.getHead());
    }

    /**
     * Data is passed by adding to the head
     * Iteration happens from head to tail
     * In this case, the last item is (3, 4), which should be the start of the iterator
     */
    @Test
    public void iterator() {
        List<C> positions = Arrays.asList(new C(1, 1), new C(2, 3), new C(3, 4));
        for (C c : positions)
            queue.add(c);
        Collections.reverse(positions);
        assertEquals(positions, queue.subList(0, queue.size()));
    }

    /**
     * Remove will strip away the tail
     */
    @Test
    public void remove() {
        List<C> positions = Arrays.asList(new C(1, 1), new C(2, 3), new C(3, 4));
        for (C c : positions)
            queue.add(c);
        assertEquals(new C(1, 1), queue.remove());
        assertEquals("Queue is now of size 2", 2, queue.size());
        assertEquals("Queue's max size is still 3", 3, queue.maxSize());
    }
}
