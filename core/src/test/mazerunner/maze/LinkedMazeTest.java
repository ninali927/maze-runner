package test.mazerunner.maze;

import main.mazerunner.structures.LinkedMaze;
import main.mazerunner.structures.Maze;

public class LinkedMazeTest extends MazeTest {

    @Override
    public Maze createMaze(int width, int height) {
        return new LinkedMaze(width, height);
    }
}
