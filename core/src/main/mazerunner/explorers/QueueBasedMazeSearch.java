package main.mazerunner.explorers;

import main.mazerunner.adapters.DequeQueue;
import main.mazerunner.adapters.Queue;
import main.mazerunner.structures.ArrayDeque;
import main.mazerunner.structures.Maze;
import main.mazerunner.structures.MazeCell;

/**
 * A search algorithm that explores the maze using a queue-based (FIFO) approach.
 * It extends the abstract MazeSearch class and provides a queue-based frontier.
 */
public class QueueBasedMazeSearch extends MazeSearch {

    /**
     * Constructor that initializes the maze to be searched.
     *
     * @param maze The maze instance to be explored.
     */
    public QueueBasedMazeSearch(Maze maze) {
        super(maze);
    }

    /**
     * Private inner class implementing the Frontier interface with a DequeQueue.
     */
    private static class QueueFrontier implements Frontier<MazeCell> {
        private final Queue<MazeCell> queue = new DequeQueue<>(new ArrayDeque<>());

        @Override
        public void add(MazeCell element) {
            queue.enqueue(element);
        }

        @Override
        public MazeCell remove() {
            MazeCell front = queue.front();
            queue.dequeue();
            return front;
        }

        @Override
        public boolean isEmpty() {
            return queue.isEmpty();
        }

        @Override
        public int size() {
            return queue.size();
        }
    }

    @Override
    protected Frontier<MazeCell> createFrontier() {
        return new QueueFrontier();
    }
}
