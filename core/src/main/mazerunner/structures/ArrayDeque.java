package main.mazerunner.structures;

import main.exceptions.EmptyException;

/**
 * A circular-array implementation of a generic double‑ended queue (deque).
 * 
 * See {@link Deque} for details on the interface.
 * 
 * @param <T> the type of elements held in this deque
 */
public class ArrayDeque<T> implements Deque<T> {
    private T[] elements;
    private int first; // index of the front element
    private int numElements;
    private static final int INITIAL_CAPACITY = 8;

    /**
     * Initializes an empty deque with an initial capacity of 8.
     */
    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        this.elements = (T[]) new Object[INITIAL_CAPACITY];
        this.first = 0;
        this.numElements = 0;
    }

    @Override
    public void addFirst(T element) {
        if (numElements == elements.length) {
            resize();
        }
        first = (first - 1 + elements.length) % elements.length;
        elements[first] = element;
        numElements++;
    }

    @Override
    public void addLast(T element) {
        if (numElements == elements.length) {
            resize();
        }
        int last = (first + numElements) % elements.length;
        elements[last] = element;
        numElements++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new EmptyException();
        }
        T value = elements[first];
        elements[first] = null;
        first = (first + 1) % elements.length;
        numElements--;
        return value;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new EmptyException();
        }

        int last = (first + numElements - 1 + elements.length) % elements.length;
        T value = elements[last];
        elements[last] = null;
        numElements--;

        return value;
    }

    @Override
    public T peekFirst() {
        if (isEmpty()) {
            throw new EmptyException();
        }
        return elements[first];
    }

    @Override
    public T peekLast() {
        if (isEmpty()) {
            throw new EmptyException();
        }

        int last = (first + numElements - 1 + elements.length) % elements.length;
        return elements[last];
    }

    @Override
    public int size() {
        return numElements;
    }

    @Override
    public boolean isEmpty() {
        return numElements == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newData = (T[]) new Object[elements.length * 2];
        for (int i = 0; i < numElements; i++) {
            int oldIndex = (first + i) % elements.length;
            newData[i] = elements[oldIndex];
        }
        elements = newData;
        first = 0;
    }
}
