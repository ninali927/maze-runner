package main.mazerunner.explorers;

import main.mazerunner.adapters.DequeStack;
import main.mazerunner.structures.ArrayDeque;
import main.mazerunner.structures.Maze;
import main.mazerunner.structures.MazeCell;

/**
 * A search algorithm that explores the maze using a stack-based (LIFO) approach.
 * It extends the abstract MazeSearch class and provides a stack-based frontier.
 */
public class StackBasedMazeSearch extends MazeSearch {

    /**
     * Constructor that initializes the maze to be searched.
     *
     * @param maze The maze instance to be explored.
     */
    public StackBasedMazeSearch(Maze maze) {
        super(maze);
    }

    /**
     * Private inner class implementing the Frontier interface with a DequeStack.
     */
    private static class StackFrontier implements Frontier<MazeCell> {
        private final DequeStack<MazeCell> stack = new DequeStack<>(new ArrayDeque<>());

        @Override
        public void add(MazeCell element) {
            stack.push(element);
        }

        @Override
        public MazeCell remove() {
            MazeCell top = stack.top();
            stack.pop();
            return top;
        }

        @Override
        public boolean isEmpty() {
            return stack.isEmpty();
        }

        @Override
        public int size() {
            return stack.size();
        }
    }

    @Override
    protected Frontier<MazeCell> createFrontier() {
        return new StackFrontier();
    }
}
