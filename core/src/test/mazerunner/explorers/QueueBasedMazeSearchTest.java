package test.mazerunner.explorers;

import main.mazerunner.explorers.MazeSearch;
import main.mazerunner.explorers.QueueBasedMazeSearch;
import main.mazerunner.structures.Maze;

public class QueueBasedMazeSearchTest extends MazeSearchTest {

    @Override
    public MazeSearch createSearcher(Maze maze) {
        return new QueueBasedMazeSearch(maze);
    }
}
