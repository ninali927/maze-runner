package main.mazerunner.structures;

import main.exceptions.EmptyException;

/**
 * A generic double‑ended queue (deque) supporting insertion, removal, and
 * inspection of elements at both the front and the back.
 *
 * Implementations should provide constant time (or amortized constant time)
 * performance for all operations.
 *
 * @param <T> the type of elements held in this deque
 */
public interface Deque<T> {
    /**
     * Inserts the specified element at the front of this deque.
     *
     * @param element the element to add 
     */
    void addFirst(T element);

    /**
     * Inserts the specified element at the back of this deque.
     *
     * @param element the element to add 
     */
    void addLast(T element);

    /**
     * Removes and returns the element at the front of this deque.
     *
     * @return the element previously at the front
     * @throws EmptyException if this deque is empty
     */
    T removeFirst();

    /**
     * Removes and returns the element at the back of this deque.
     *
     * @return the element previously at the back
     * @throws EmptyException if this deque is empty
     */
    T removeLast();

    /**
     * Returns, but does not remove, the element at the front of this deque.
     *
     * @return the element at the front
     * @throws EmptyException if this deque is empty
     */
    T peekFirst();

    /**
     * Returns, but does not remove, the element at the back of this deque.
     *
     * @return the element at the back
     * @throws EmptyException if this deque is empty
     */
    T peekLast();

    /**
     * Returns the number of elements in this deque.
     *
     * @return the current size, never negative
     */
    int size();

    /**
     * Returns {@code true} if this deque contains no elements.
     *
     * @return {@code true} if empty; {@code false} otherwise
     */
    boolean isEmpty();
}
