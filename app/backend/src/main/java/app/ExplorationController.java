package app;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import main.mazerunner.explorers.QueueBasedMazeSearch;
import main.mazerunner.explorers.StackBasedMazeSearch;
import main.mazerunner.structures.MazeCell;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controller for running and visualizing the maze search algorithms.
 */
public class ExplorationController {
    private static final Gson gson = new Gson();

    private MazeController.VisualizableMaze maze;
    private CompletableFuture<Void> currentRun;

    private final java.util.concurrent.atomic.AtomicBoolean isPaused = new java.util.concurrent.atomic.AtomicBoolean(false);
    private final java.util.concurrent.atomic.AtomicBoolean skipToEndRequested = new java.util.concurrent.atomic.AtomicBoolean(false);
    private final java.util.concurrent.Semaphore stepSemaphore = new java.util.concurrent.Semaphore(0);
    private final AtomicInteger frontierSize = new AtomicInteger(0);

    public void attachMaze(MazeController.VisualizableMaze maze) {
        this.maze = maze;
    }

    public void handleExplorationMessage(WsContext ctx, AlgorithmMessage message) {
        try {
            switch (message.type) {
                case "run_exploration":
                    runExploration(ctx, message);
                    break;
                case "pause_exploration":
                    pauseExploration(ctx);
                    break;
                case "resume_exploration":
                    resumeExploration(ctx);
                    break;
                case "step_exploration":
                    stepExploration(ctx);
                    break;
                case "skip_to_end":
                    skipToEnd(ctx);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown exploration command: " + message.type)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Exploration error: " + e.getMessage())));
        }
    }

    private void runExploration(WsContext ctx, AlgorithmMessage message) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No maze available")));
            return;
        }
        Map<String, Object> data = message.data != null ? message.data : new HashMap<>();
        String strategy = (String) data.getOrDefault("strategy", "bfs");

        maze.resetVisited();
        isPaused.set(false);
        skipToEndRequested.set(false);
        stepSemaphore.drainPermits();

        // Set listener on the maze object to spy on getNeighbors calls
        maze.setListener((cell, neighbors) -> {
            sendStep(ctx, "visit", cell, frontierSize.get());
            waitIfPaused();
            sleepIfNotSkipped();
        });

        currentRun = CompletableFuture.runAsync(() -> {
            try {
                ctx.send(gson.toJson(new AlgorithmMessage("exploration_started", strategy.toUpperCase() + " search started")));

                int startRow = ((Number) data.getOrDefault("startRow", 0)).intValue();
                int startCol = ((Number) data.getOrDefault("startCol", 0)).intValue();
                int goalRow = ((Number) data.getOrDefault("goalRow", maze.getHeight() - 1)).intValue();
                int goalCol = ((Number) data.getOrDefault("goalCol", maze.getWidth() - 1)).intValue();

                MazeCell start = maze.getCell(startRow, startCol);
                MazeCell goal = maze.getCell(goalRow, goalCol);
                List<MazeCell> path;

                if ("dfs".equalsIgnoreCase(strategy)) {
                    InstrumentedStackSearch search = new InstrumentedStackSearch(maze, frontierSize);
                    path = search.findPath(start, goal);
                } else {
                    InstrumentedQueueSearch search = new InstrumentedQueueSearch(maze, frontierSize);
                    path = search.findPath(start, goal);
                }

                sendComplete(ctx, !path.isEmpty(), path);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Exploration failed: " + e.getMessage())));
            } finally {
                maze.setListener(null); // Clean up listener
            }
        });
    }

    private void sendStep(WsContext ctx, String op, MazeCell cell, int frontier) {
        Map<String, Object> data = new HashMap<>();
        data.put("row", ReflectionHelper.getIntField(cell, "row"));
        data.put("col", ReflectionHelper.getIntField(cell, "col"));
        data.put("frontier", frontier);
        AlgorithmMessage step = new AlgorithmMessage("exploration_step", op, data);
        ctx.send(gson.toJson(step));
    }

    private void sendComplete(WsContext ctx, boolean found, List<MazeCell> path) {
        List<int[]> pathCoords = path.stream()
                .map(cell -> new int[]{
                        ReflectionHelper.getIntField(cell, "row"),
                        ReflectionHelper.getIntField(cell, "col")})
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("found", found);
        data.put("pathLength", pathCoords.size());
        data.put("path", pathCoords);
        AlgorithmMessage complete = new AlgorithmMessage("exploration_complete", found ? "Path found" : "No path found", data);
        ctx.send(gson.toJson(complete));
    }

    private void sleepIfNotSkipped() {
        try {
            if (!skipToEndRequested.get()) {
                Thread.sleep(50);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitIfPaused() {
        if (isPaused.get()) {
            try {
                stepSemaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void pauseExploration(WsContext ctx) {
        isPaused.set(true);
        ctx.send(gson.toJson(new AlgorithmMessage("exploration_paused", "Exploration paused")));
    }

    private void resumeExploration(WsContext ctx) {
        isPaused.set(false);
        stepSemaphore.release();
        ctx.send(gson.toJson(new AlgorithmMessage("exploration_resumed", "Exploration resumed")));
    }

    private void stepExploration(WsContext ctx) {
        if (isPaused.get()) {
            stepSemaphore.release();
        }
    }

    private void skipToEnd(WsContext ctx) {
        skipToEndRequested.set(true);
        if (isPaused.get()) {
            isPaused.set(false);
            stepSemaphore.release(10000);
        }
        ctx.send(gson.toJson(new AlgorithmMessage("skip_started", "Skipping to end")));
    }
}
