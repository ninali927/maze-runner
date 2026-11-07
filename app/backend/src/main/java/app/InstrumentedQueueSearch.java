package app;

import java.util.concurrent.atomic.AtomicInteger;

import main.mazerunner.explorers.MazeSearch;
import main.mazerunner.explorers.QueueBasedMazeSearch;
import main.mazerunner.structures.Maze;
import main.mazerunner.structures.MazeCell;

/**
 * A queue-based search that exposes the current frontier size by
 * instrumenting the underlying Frontier implementation.
 *
 * It subclasses the core
 * QueueBasedMazeSearch and overrides createFrontier to track size.
 */
public class InstrumentedQueueSearch extends QueueBasedMazeSearch {

    private final AtomicInteger frontierSizeRef;

    public InstrumentedQueueSearch(Maze maze, AtomicInteger frontierSizeRef) {
        super(maze);
        this.frontierSizeRef = frontierSizeRef;
        this.frontierSizeRef.set(0);
    }

    @Override
    protected Frontier<MazeCell> createFrontier() {
        // Delegate to the core implementation so its adapters/deque are used
        final Frontier<MazeCell> base = super.createFrontier();

        // Wrap the core frontier to observe size on each mutation
        return new MazeSearch.Frontier<MazeCell>() {
            @Override
            public void add(MazeCell element) {
                base.add(element);
                frontierSizeRef.set(base.size());
            }

            @Override
            public MazeCell remove() {
                MazeCell val = base.remove();
                frontierSizeRef.set(base.size());
                return val;
            }

            @Override
            public boolean isEmpty() {
                return base.isEmpty();
            }

            @Override
            public int size() {
                return base.size();
            }
        };
    }
}
