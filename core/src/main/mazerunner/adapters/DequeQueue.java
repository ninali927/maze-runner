package main.mazerunner.adapters;

import main.mazerunner.structures.Deque;

/**
 * Queue ADT implementation using a Deque.
 *
 * @param <T> base type.
 */
public class DequeQueue<T> implements Queue<T> {
    private final Deque<T> deque;

    /**
     * Constructs a DequeQueue with the provided Deque.
     *
     * @param deque the Deque to be used as the underlying storage.
     */
    public DequeQueue(Deque<T> deque) {
        this.deque = deque;
    }

    @Override
    public void enqueue(T element) {
        deque.addLast(element);
    }

    @Override
    public void dequeue() {
        deque.removeFirst();
    }

    @Override
    public T front() {
        return deque.peekFirst();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public int size() {
        return deque.size();
    }
}
