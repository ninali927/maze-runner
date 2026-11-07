# Maze Runner

A Java implementation of a linked-grid maze, a circular-array deque with stack/queue
adapters, and two maze-solving strategies built on them: a stack-based depth-first search
and a queue-based breadth-first search. A small web visualizer lets you draw walls and watch
each search explore the maze step by step.

## What it demonstrates

- **`LinkedMaze`**: a grid where every cell holds pointers to its north/south/east/west
  neighbors. It is built in O(width × height) time with O(1) auxiliary space.
- **`ArrayDeque`**: a generic circular-buffer deque that doubles in size when full, giving
  amortized O(1) operations at both ends.
- **`DequeStack` / `DequeQueue`**: Stack and Queue ADTs implemented as adapters over the deque.
- **`MazeSearch`**: a template-method search. The only difference between DFS and BFS is the
  frontier: `StackBasedMazeSearch` (LIFO) or `QueueBasedMazeSearch` (FIFO).

### Array-based vs. linked maze

| Operation | Array-based | Linked (`LinkedMaze`) |
|---|---|---|
| Space | O(width × height) | O(width × height), plus 4 neighbor pointers per cell |
| Construction | O(width × height) | O(width × height) |
| `getCell(row, col)` | O(1) by index | O(row + col), walks from the top-left cell |
| `getNeighbors(cell)` | O(1) | O(1), follows the pointers |
| `isWall` / `setWall(row, col)` | O(1) | O(row + col), goes through `getCell` |
| `resetVisited()` | O(width × height) | O(width × height) |

An array-based grid is better when random access is frequent. The linked grid suits traversals
that move from neighbor to neighbor, because each cell already points to its neighbors.

### Stack-based (DFS) vs. queue-based (BFS) search

- BFS always finds a shortest path. DFS finds *a* path, which is often longer.
- DFS usually visits fewer cells and uses less memory, since it follows a single branch at a time.
  BFS can still win when the goal is close to the start.
- DFS does well in deep mazes with many branches. BFS does well in open mazes with few walls,
  because it checks nearby cells before committing to one direction.
- Use DFS to explore the whole space or when any path will do. Use BFS when you need the
  shortest path or the goal is probably nearby.

## Project layout

```
.
├── core/                        Data structures and search algorithms
│   ├── src/main/mazerunner/
│   │   ├── structures/          Maze, LinkedMaze, MazeCell, Deque, ArrayDeque
│   │   ├── adapters/            Stack, Queue, DequeStack, DequeQueue
│   │   └── explorers/           MazeSearch, StackBasedMazeSearch, QueueBasedMazeSearch
│   ├── src/main/exceptions/     EmptyException
│   ├── src/test/mazerunner/     JUnit 5 tests for the core code
│   └── start-visualization.sh   Launches the visualizer
└── app/
    ├── backend/                 Javalin server (Gradle build that also compiles and tests core/)
    └── frontend/                Static HTML/CSS/JS client, served by the backend
```

The Gradle build in `app/backend` adds `core/src/...` as extra source directories, so a single
build compiles and tests both the core library and the visualizer backend.

## Running it

Requirements: **JDK 25**. The Gradle wrapper downloads Gradle 9.1 the first time it runs.

Run the tests:

```bash
cd app/backend
./gradlew test
```

An HTML coverage report is written to `app/backend/build/reports/jacoco/index.html`.

Start the visualizer:

```bash
cd app/backend
./gradlew run
```

Then open <http://localhost:7001>. Press `Ctrl+C` to stop the server.

You can also run `bash core/start-visualization.sh` from the project root. It starts the
server in the background. Stop it with `pkill -f "gradle.*run"`.

In the visualizer, create a maze, click cells to toggle walls, pick DFS or BFS, then run,
pause, step through, or skip to the end of the search.

## Tech stack

- Java 25, Gradle 9.1 (wrapper)
- Javalin 6 (HTTP + WebSocket), Gson, SLF4J
- JUnit 5, JaCoCo
- Vanilla HTML/CSS/JavaScript frontend
