package test.mazerunner.maze;

import main.mazerunner.structures.Maze;
import main.mazerunner.structures.MazeCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Abstract base class for testing implementations of the Maze interface.
 */
public abstract class MazeTest {

    protected Maze maze;
    protected final int DEFAULT_WIDTH = 10;
    protected final int DEFAULT_HEIGHT = 8;

    /**
     * Factory method for creating an instance of the Maze implementation under test.
     * @param width The width of the maze.
     * @param height The height of the maze.
     * @return A new maze instance.
     */
    public abstract Maze createMaze(int width, int height);

    @BeforeEach
    public void setup() {
        maze = createMaze(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Test
    public void testConstructorCreatesProperDimensions() {
        assertEquals(DEFAULT_WIDTH, maze.getWidth());
        assertEquals(DEFAULT_HEIGHT, maze.getHeight());
    }

    @Test
    public void testGetCellSameInstance() {
        MazeCell a = maze.getCell(0, 0);
        MazeCell b = maze.getCell(0, 0);
        assertEquals(a, b);
    }

    @Test
    public void testGetCellDifferent() {
        MazeCell a = maze.getCell(0, 0);
        MazeCell b = maze.getCell(0, 1);
        assertNotEquals(a, b);
    }

    @Test
    public void testGetCellWithinBounds() {
        MazeCell cell = maze.getCell(0, 0);
        if (cell == null) {
            fail("getCell(0, 0) returned null");
        }

        cell = maze.getCell(DEFAULT_HEIGHT - 1, DEFAULT_WIDTH - 1);
        if (cell == null) {
            fail("getCell at bottom-right corner returned null");
        }

        cell = maze.getCell(3, 5);
        if (cell == null) {
            fail("getCell(3, 5) returned null");
        }
    }

    @Test
    public void testGetCellOutOfBoundsforNegativeRow() {
        try {
            maze.getCell(-1, 0);
            fail("expected IndexOutOfBoundsException for negative row");
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    @Test
    public void testGetCellOutOfBoundsforNegativeCol() {
        try {
            maze.getCell(0, -1);
            fail("expected IndexOutOfBoundsException for negative column");
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    @Test
    public void testGetCellOutOfBoundsRowTooLarge() {
        try {
            maze.getCell(DEFAULT_HEIGHT, 0);
            fail("expected IndexOutOfBoundsException for row >= height");
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    @Test
    public void testGetCellOutOfBoundsColTooLarge() {
        try {
            maze.getCell(0, DEFAULT_WIDTH);
            fail("expected IndexOutOfBoundsException for col >= width");
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    @Test
    public void testSetWallAndIsWallWorkTogether() {
        maze.setWall(1, 1, true);
        assertTrue(maze.isWall(2, 3));
        maze.setWall(2, 2, false);
        assertFalse(maze.isWall(2, 2));
    }

    @Test
    public void testCellSetWallSyncsWithMaze() {
        MazeCell cell = maze.getCell(1, 1);
        assertFalse(cell.isWall());
        cell.setWall(true);
        assertTrue(maze.isWall(1, 1));
        cell.setWall(false);
        assertFalse(maze.isWall(1, 1));
    }

    @Test
    public void testIsWallNegativeRowThrows() {
        try {
            maze.isWall(-1, 0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }

        try {
            maze.isWall(DEFAULT_HEIGHT, 0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testIsWallNegativeColThrows() {
        try {
            maze.isWall(0, -1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }

        try {
            maze.isWall(0, DEFAULT_HEIGHT);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
    }


    @Test
    public void testSetWallNegativeRowThrows() {
        try {
            maze.setWall(-1, 0, true);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
        }

        try {
            maze.setWall(DEFAULT_HEIGHT, 0, true);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void testSetWallNegativeColThrows() {
        try {
            maze.setWall(0, -1, true);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
        }

        try {
            maze.setWall(0, DEFAULT_WIDTH, true);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }


    @Test
    public void testGetNeighborsInMiddle() {
        MazeCell center = maze.getCell(5, 5);
        List<MazeCell> neighbors = maze.getNeighbors(center);
        assertEquals(4, neighbors.size());
    }

    @Test
    public void testGetNeighborsInCorner() {
        MazeCell corner = maze.getCell(0, 0);
        List<MazeCell> neighbors = maze.getNeighbors(corner);
        assertEquals(2, neighbors.size());
    }

    @Test
    public void testGetNeighborsOnEdge() {
        MazeCell edge = maze.getCell(0, 5);
        List<MazeCell> neighbors = maze.getNeighbors(edge);
        assertEquals(3, neighbors.size());
    }


    @Test
    public void testGetNeighborsExcludesWalls() {
        maze.setWall(3, 5, true);
        maze.setWall(5, 5, true);
        maze.setWall(4, 4, true);
        maze.setWall(4, 6, true);
        MazeCell center = maze.getCell(4, 5);
        List<MazeCell> neighbors = maze.getNeighbors(center);
        assertEquals(0, neighbors.size());
    }

    @Test
    public void testGetNeighborsInOrder() {
        MazeCell center = maze.getCell(4, 5);
        List<MazeCell> neighbors = maze.getNeighbors(center);
        assertEquals(4, neighbors.size());
        assertSame(maze.getCell(3, 5), neighbors.get(0));
        assertSame(maze.getCell(5, 5), neighbors.get(1));
        assertSame(maze.getCell(4, 6), neighbors.get(2));
        assertSame(maze.getCell(4, 4), neighbors.get(3));
    }

    @Test
    public void testGetNeighborsInOrderWithWalls() {
        maze.setWall(3, 5, true);
        maze.setWall(4, 6, true);

        MazeCell center = maze.getCell(4, 5);
        List<MazeCell> neighbors = maze.getNeighbors(center);

        assertEquals(2, neighbors.size());
        assertSame(maze.getCell(5, 5), neighbors.get(0));
        assertSame(maze.getCell(4, 4), neighbors.get(1));
    }


    @Test
    public void testResetVisited() {
        MazeCell cell1 = maze.getCell(0, 0);
        MazeCell cell2 = maze.getCell(DEFAULT_HEIGHT - 1, DEFAULT_WIDTH - 1);

        cell1.setVisited(true);
        cell2.setVisited(true);
        assertTrue(cell1.isVisited());
        assertTrue(cell2.isVisited());

        maze.resetVisited();

        assertFalse(cell1.isVisited());
        assertFalse(cell2.isVisited());
    }
}


