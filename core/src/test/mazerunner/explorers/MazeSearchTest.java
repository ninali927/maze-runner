package test.mazerunner.explorers;

import main.mazerunner.explorers.MazeSearch;
import main.mazerunner.structures.LinkedMaze;
import main.mazerunner.structures.Maze;
import main.mazerunner.structures.MazeCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An abstract base class for testing MazeSearch implementations.
 */
public abstract class MazeSearchTest {

    protected Maze maze;
    protected MazeSearch searcher;

    /**
     * Factory method for creating an instance of the MazeSearch implementation under test.
     */
    public abstract MazeSearch createSearcher(Maze maze);

    @BeforeEach
    public void setUp() {
        maze = new LinkedMaze(5, 5);
        searcher = createSearcher(maze);
    }

    @Test
    public void testFindsAPathWhenOneExists() {
        MazeCell start = maze.getCell(0, 0);
        MazeCell goal = maze.getCell(2, 2);

        List<MazeCell> path = searcher.findPath(start, goal);

        assertFalse(path.isEmpty(), "A path should be found in a simple open maze.");
        assertEquals(start, path.get(0), "Path should start at the start cell.");
        assertEquals(goal, path.get(path.size() - 1), "Path should end at the goal cell.");
    }

    @Test
    public void testFindsNoPathWhenBlocked() {
        // Wall off the goal cell completely
        maze.setWall(0, 1, true);
        maze.setWall(1, 0, true);
        maze.setWall(1, 1, true);

        MazeCell start = maze.getCell(0, 0);
        MazeCell goal = maze.getCell(1, 1);

        List<MazeCell> path = searcher.findPath(start, goal);

        assertTrue(path.isEmpty(), "Should not find a path to a completely blocked cell.");
    }
}
