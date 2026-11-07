package test.mazerunner.deque;

import main.mazerunner.structures.ArrayDeque;
import main.mazerunner.structures.Deque;

public class ArrayDequeTest extends DequeTest {

    @Override
    public Deque<Integer> createDeque() {
        return new ArrayDeque<>();
    }
}
