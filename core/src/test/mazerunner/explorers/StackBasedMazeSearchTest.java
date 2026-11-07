package test.mazerunner.explorers;

import main.mazerunner.explorers.MazeSearch;
import main.mazerunner.explorers.StackBasedMazeSearch;
import main.mazerunner.structures.Maze;

public class StackBasedMazeSearchTest extends MazeSearchTest {

    @Override
    public MazeSearch createSearcher(Maze maze) {
        return new StackBasedMazeSearch(maze);
    }
}
