package main.mazerunner.adapters;

import main.mazerunner.structures.Deque;

/**
 * Stack ADT implementation using a Deque.
 *
 * @param <T> base type.
 */
public class DequeStack<T> implements Stack<T> {
    private final Deque<T> deque;

    /**
     * Constructs a DequeStack with the provided Deque.
     *
     * @param deque the Deque to be used as the underlying storage.
     */
    public DequeStack(Deque<T> deque) {
        this.deque = deque;
    }

    @Override
    public void push(T element) {
        deque.addFirst(element);
    }

    @Override
    public void pop() {
        deque.removeFirst();
    }

    @Override
    public T top() {
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
