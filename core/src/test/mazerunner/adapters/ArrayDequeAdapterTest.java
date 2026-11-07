package test.mazerunner.adapters;

import main.mazerunner.structures.ArrayDeque;
import main.mazerunner.structures.Deque;

public class ArrayDequeAdapterTest extends AdapterContractTest {
    @Override
    public Deque<Integer> createDeque() {
        return new ArrayDeque<>();
    }
}
