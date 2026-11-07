package test.mazerunner.adapters;

import main.mazerunner.adapters.DequeQueue;
import main.mazerunner.adapters.DequeStack;
import main.mazerunner.structures.Deque;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AdapterContractTest {

    public abstract Deque<Integer> createDeque();

    @Test
    public void testStackLIFOOrdering() {
        Deque<Integer> deque = createDeque();
        DequeStack<Integer> stack = new DequeStack<>(deque);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.top());
        stack.pop();
        assertEquals(2, stack.top());
        stack.pop();
        assertEquals(1, stack.top());
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    public void testQueueFIFOOrdering() {
        Deque<Integer> deque = createDeque();
        DequeQueue<Integer> queue = new DequeQueue<>(deque);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.front());
        queue.dequeue();
        assertEquals(2, queue.front());
        queue.dequeue();
        assertEquals(3, queue.front());
        queue.dequeue();
        assertTrue(queue.isEmpty());
    }
}
