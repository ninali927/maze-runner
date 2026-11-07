package main.mazerunner.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * A linked-list implementation of a rectangular maze.
 * 
 * See {@link Maze} for details on the interface.
 */
public class LinkedMaze implements Maze {
    private final int width;
    private final int height;
    private final LinkedMazeCell topLeft;

    // A private, nested implementation of the MazeCell interface.
    // This class contains the actual grid structure and pointers.
    private static class LinkedMazeCell implements MazeCell {
        final int row;
        final int col;
        boolean isWall;
        boolean isVisited;

        LinkedMazeCell north;
        LinkedMazeCell south;
        LinkedMazeCell east;
        LinkedMazeCell west;

        LinkedMazeCell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean isWall() {
            return isWall;
        }

        @Override
        public void setWall(boolean isWall) {
            this.isWall = isWall;
        }

        @Override
        public boolean isVisited() {
            return isVisited;
        }

        @Override
        public void setVisited(boolean visited) {
            this.isVisited = visited;
        }
    }

    /**
     * Creates a linked maze with the specified dimensions. 
     *
     * @param width the number of columns in the maze, must be positive
     * @param height the number of rows in the maze, must be positive
     * @throws IllegalArgumentException if width or height is non-positive
     */
    public LinkedMaze(int width, int height) {
        // Each new cell is linked to its west and north neighbors (and they back to it),
        // building the grid in O(width * height) time with O(1) auxiliary space.
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
        }
        this.width = width;
        this.height = height;

        LinkedMazeCell prevRowFirstCell = null;
        LinkedMazeCell currRowFirstCell = null;
        LinkedMazeCell firstCell = null;

        for (int row = 0; row < height; row++) {
            LinkedMazeCell prevCell = null;
            LinkedMazeCell prevRowThatCell = prevRowFirstCell;
            for (int col = 0; col < width; col++) {
                LinkedMazeCell cell = new LinkedMazeCell(row, col);

                if (prevCell != null) {
                    cell.west = prevCell;
                    prevCell.east = cell;
                } else {
                    currRowFirstCell = cell;
                }

                if (prevRowThatCell != null) {
                    cell.north = prevRowThatCell;
                    prevRowThatCell.south = cell;
                    prevRowThatCell = prevRowThatCell.east;
                }
                if (row == 0 && col == 0) {
                    firstCell = cell;
                }
                prevCell = cell;
            }
            prevRowFirstCell = currRowFirstCell;
        }
        this.topLeft = firstCell;
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public MazeCell getCell(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("Invalid coordinates");
        }
        LinkedMazeCell current = topLeft;
        for (int r = 0; r < row; r++) {
            current = current.south;
        }
        for (int c = 0; c < col; c++) {
            current = current.east;
        }
        return current;
    }

    @Override
    public boolean isWall(int row, int col) {
        return getCell(row, col).isWall();
    }

    @Override
    public void setWall(int row, int col, boolean isWall) {
        getCell(row, col).setWall(isWall);
    }

    @Override
    public List<MazeCell> getNeighbors(MazeCell cell) {
        if (!(cell instanceof LinkedMazeCell)) {
            throw new IllegalArgumentException("Invalid cell type");
        }
        LinkedMazeCell current = (LinkedMazeCell) cell;
        List<MazeCell> neighbors = new ArrayList<>(4);

        if (current.north != null && !current.north.isWall) {
            neighbors.add(current.north);
        }
        if (current.south != null && !current.south.isWall) {
            neighbors.add(current.south);
        }
        if (current.east != null && !current.east.isWall) {
            neighbors.add(current.east);
        }
        if (current.west != null && !current.west.isWall) {
            neighbors.add(current.west);
        }
        return neighbors;
    }

    @Override
    public void resetVisited() {
        LinkedMazeCell firstInRow = (LinkedMazeCell) topLeft;
        for (int row = 0; row < height; row++) {
            LinkedMazeCell current = firstInRow;
            for (int col = 0; col < width; col++) {
                current.setVisited(false);
                current = current.east;
            }
            firstInRow = firstInRow.south;
        }
    }

}
