package test.mazerunner.deque;

import main.exceptions.EmptyException;
import main.mazerunner.structures.Deque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract base class for testing implementations of the Deque interface.
 */
public abstract class DequeTest {

    protected Deque<Integer> deque;

    /**
     * Factory method for creating an instance of the Deque implementation under test.
     * @return a new, empty Deque.
     */
    public abstract Deque<Integer> createDeque();

    @BeforeEach
    public void setup() {
        deque = createDeque();
    }

    @Test
    public void testNewDequeIsEmpty() {
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    // addFirst
    @Test
    public void testAddFirstSingleElement() {
        deque.addFirst(1);
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());
        assertEquals(1, deque.peekFirst());
    }

    @Test
    public void testAddFirstMultipleElements() {
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);

        assertEquals(3, deque.size());
        assertEquals(3, deque.peekFirst());
        assertEquals(1, deque.peekLast());
    }

    // addLast
    @Test
    public void testAddLastSingleElement() {
        deque.addLast(20);
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());
        assertEquals(20, deque.peekLast());
    }

    @Test
    public void testAddLastMultipleElements() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals(3, deque.size());
        assertEquals(1, deque.peekFirst());
        assertEquals(3, deque.peekLast());
    }

    // addFirst and addLast
    @Test
    public void testAddFirstAndLast() {
        deque.addFirst(2);
        deque.addLast(3);
        deque.addFirst(1);
        deque.addLast(4);

        assertEquals(4, deque.size());
        assertEquals(1, deque.peekFirst());
        assertEquals(4, deque.peekLast());
    }

    // removeFirst
    @Test
    public void testRemoveFirstSingleElement() {
        deque.addFirst(1);
        int value = deque.removeFirst();
        assertEquals(1, value);
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    public void testRemoveFirstMultipleElements() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals(1, deque.removeFirst());
        assertEquals(2, deque.removeFirst());
        assertEquals(1, deque.size());
        assertEquals(3, deque.peekFirst());
    }

    // removeLast
    @Test
    public void testRemoveLastSingleElement() {
        deque.addLast(1);
        int value = deque.removeLast();

        assertEquals(1, value);
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    public void testRemoveLastMultipleElements() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals(3, deque.removeLast());
        assertEquals(2, deque.removeLast());
        assertEquals(1, deque.size());
        assertEquals(1, deque.peekLast());
    }

    @Test
    public void testRemoveFirstAndLastMixed() {
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addLast(3);
        deque.addLast(4);

        assertEquals(2, deque.removeFirst());
        assertEquals(4, deque.removeLast());
        assertEquals(1, deque.removeFirst());
        assertEquals(3, deque.removeLast());

        assertTrue(deque.isEmpty());
    }


    //exceptions
    @Test
    public void testRemoveFirstOnEmptyThrowsException() {
        try {
            deque.removeFirst();
            fail("Expected EmptyException");//** */
        } catch (EmptyException ex) {
            return;
        }
    }

    @Test
    public void testRemoveLastOnEmptyThrowsException() {
        try {
            deque.removeLast();
            fail("Expected EmptyException");
        } catch (EmptyException ex) {
            return;
        }
    }

    @Test
    public void testPeekFirstOnEmptyThrowsException() {
        try {
            deque.peekFirst();
            fail("EmptyException was not thrown for peekFirst() on empty deque");
        } catch (EmptyException ex) {
            return;
        }
    }

    @Test
    public void testPeekLastOnEmptyThrowsException() {
        try {
            deque.peekLast();
            fail("EmptyException was not thrown for peekLast() on empty deque");
        } catch (EmptyException ex) {
            return;
        }
    }

    @Test
    void testAddFirstPeekFirst() {
        deque.addFirst(1);
        assertEquals(1, deque.peekFirst());
        assertEquals(1, deque.peekLast());
    }

    @Test
    void testAddLastPeekLast() {
        deque.addLast(1);
        assertEquals(1, deque.peekLast());
        assertEquals(1, deque.peekFirst());
    }

    @Test
    void testAddFrontBackOrder() {
        deque.addFirst(1);
        deque.addLast(2);
        deque.addFirst(3);
        assertEquals(3, deque.peekFirst());
        assertEquals(2, deque.peekLast());
    }


    //size
    @Test
    public void testSizeTracking() {
        assertEquals(0, deque.size());

        deque.addFirst(1);
        assertEquals(1, deque.size());

        deque.addLast(2);
        assertEquals(2, deque.size());

        deque.removeFirst();
        assertEquals(1, deque.size());

        deque.removeLast();
        assertEquals(0, deque.size());
    }

}
