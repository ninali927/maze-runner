/**
 * Maze Runner Visualizer
 * Interactive visualization for maze exploration algorithms.
 */

document.addEventListener('alpine:init', () => {
  Alpine.data('algorithmVisualizer', () => ({
    // Connection state
    isConnected: false,
    serverInfo: '',
    messages: [],

    // Debug mode
    DEBUG: true,

    // Theme management
    currentTheme: localStorage.getItem('theme') || 'auto',

    // Maze state
    mazeState: {
      created: false,
      width: 0,
      height: 0,
      cells: [],
      startRow: 0,
      startCol: 0,
      goalRow: 0,
      goalCol: 0,
      path: [],
      isRunning: false,
      isPaused: false
    },

    mazeConfig: {
      width: 10,
      height: 10,
      startRow: 0,
      startCol: 0,
      goalRow: 9,
      goalCol: 9
    },

    mazeMetrics: {
      walls: 0,
      visited: 0,
      frontier: 0,
      pathLength: 0,
      frontierLabel: 'Frontier'
    },

    mazeVisualization: null,
    socketWrapper: null,

    init() {
      console.log('Maze Runner Visualizer initialized');
      this.initializeTheme();
      this.setupWebSocketHandlers();
      this.initializeMazeVisualization();
    },

    setupWebSocketHandlers() {
      document.body.addEventListener('htmx:wsAfterMessage', (e) => {
        try {
          const message = JSON.parse(e.detail.message);
          this.handleWebSocketMessage(message);
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      });

      document.body.addEventListener('htmx:wsOpen', (e) => {
        this.isConnected = true;
        this.socketWrapper = e.detail.socketWrapper;
        console.log('WebSocket connected');
      });

      document.body.addEventListener('htmx:wsClose', (e) => {
        this.isConnected = false;
        console.log('WebSocket disconnected');
      });

      document.body.addEventListener('htmx:wsError', (e) => {
        this.isConnected = false;
        console.error('WebSocket error:', e.detail);
      });
    },

    handleWebSocketMessage(message) {
      if (this.DEBUG) {
        console.log('Received message:', message);
      }
      this.addMessage(message.type, message.message);

      switch (message.type) {
        case 'connected':
          this.serverInfo = message.data?.serverInfo || 'Connected';
          break;
        case 'error':
          console.error('Server error:', message.message);
          break;
        case 'maze_created':
          this.handleMazeCreated(message);
          break;
        case 'maze_state':
          this.handleMazeState(message);
          break;
        case 'wall_toggled':
          this.handleWallToggled(message);
          break;
        case 'maze_reset':
          this.handleMazeReset(message);
          break;
        case 'exploration_started':
          this.handleExplorationStarted(message);
          break;
        case 'exploration_paused':
          this.handleExplorationPaused(message);
          break;
        case 'exploration_resumed':
          this.handleExplorationResumed(message);
          break;
        case 'exploration_step':
          this.handleExplorationStep(message);
          break;
        case 'exploration_complete':
          this.handleExplorationComplete(message);
          break;
        default:
          console.warn('Unknown message type:', message.type);
      }
    },

    addMessage(type, message) {
      if (!message) return;
      this.messages.unshift({ id: `${Date.now()}-${Math.random()}`, type: type, message: message, timestamp: Date.now() });
      if (this.messages.length > 100) this.messages.pop();
    },

    clearMessages() { this.messages = []; },
    formatTime(timestamp) { return new Date(timestamp).toLocaleTimeString(); },

    createMaze() {
      const cfg = this.mazeConfig;
      if (cfg.width < 2 || cfg.height < 2) {
        alert('Width/Height must be >= 2');
        return;
      }
      const cleanConfig = {
        width: cfg.width | 0,
        height: cfg.height | 0,
        startRow: Math.max(0, Math.min(cfg.startRow | 0, cfg.height - 1)),
        startCol: Math.max(0, Math.min(cfg.startCol | 0, cfg.width - 1)),
        goalRow: Math.max(0, Math.min(cfg.goalRow | 0, cfg.height - 1)),
        goalCol: Math.max(0, Math.min(cfg.goalCol | 0, cfg.width - 1)),
      };
      this.sendMessage({ type: 'create_maze', data: cleanConfig });
    },

    refreshMazeState() { this.sendMessage({ type: 'get_maze_state', data: {} }); },

    toggleMazeCell(row, col) {
      if (!this.mazeState.created) return;
      if ((row === this.mazeState.startRow && col === this.mazeState.startCol) || (row === this.mazeState.goalRow && col === this.mazeState.goalCol)) return;
      this.sendMessage({ type: 'toggle_wall', data: { row, col } });
    },

    runExploration(strategy) {
      if (!this.mazeState.created || this.mazeState.isRunning) return;
      this.mazeMetrics.frontierLabel = (strategy === 'dfs') ? 'Stack Size' : 'Queue Size';
      this.mazeState.path = [];
      this.mazeMetrics.visited = 0;
      this.mazeMetrics.pathLength = 0;
      this.mazeState.cells.forEach(cell => { cell.isVisited = false; });
      this.updateMazeVisualization();
      this.$nextTick(() => {
        const { startRow, startCol, goalRow, goalCol } = this.mazeState;
        this.sendMessage({ type: 'run_exploration', data: { strategy, startRow, startCol, goalRow, goalCol } });
      });
    },

    pauseExploration() { this.sendMessage({ type: 'pause_exploration', data: {} }); },
    resumeExploration() { this.sendMessage({ type: 'resume_exploration', data: {} }); },
    stepExploration() { this.sendMessage({ type: 'step_exploration', data: {} }); },
    skipToEndExploration() { this.sendMessage({ type: 'skip_to_end', data: {} }); },
    resetMaze() { this.sendMessage({ type: 'reset_maze', data: {} }); },

    handleMazeCreated(message) {
      const { width, height, startRow, startCol, goalRow, goalCol } = message.data;
      this.mazeState.created = true;
      this.mazeState.width = width;
      this.mazeState.height = height;
      this.mazeState.startRow = startRow;
      this.mazeState.startCol = startCol;
      this.mazeState.goalRow = goalRow;
      this.mazeState.goalCol = goalCol;
      this.mazeMetrics = { walls: 0, visited: 0, frontier: 0, pathLength: 0, frontierLabel: 'Frontier' };
      this.initializeMazeVisualization();
      this.refreshMazeState();
      this.addMessage('success', 'Maze created');
    },

    handleMazeState(message) {
      const data = message.data || {};
      this.mazeState.width = data.width || this.mazeState.width;
      this.mazeState.height = data.height || this.mazeState.height;
      this.mazeState.cells = data.cells || [];
      let walls = 0;
      for (const cell of this.mazeState.cells) { if (cell.isWall) walls++; }
      this.mazeMetrics.walls = walls;
      this.mazeMetrics.visited = 0;
      this.mazeMetrics.frontier = 0;
      this.mazeMetrics.pathLength = 0;
      this.updateMazeVisualization();
    },

    handleWallToggled(message) {
      const { row, col, isWall } = message.data;
      const cell = this.mazeState.cells.find(c => c.row === row && c.col === col);
      if (cell) {
        cell.isWall = isWall;
        this.mazeMetrics.walls += isWall ? 1 : -1;
        if (this.mazeVisualization) this.mazeVisualization.updateCell(row, col, isWall ? 'wall' : 'default');
      }
    },

    handleMazeReset(message) {
      this.mazeState.path = [];
      this.mazeMetrics.visited = 0;
      this.mazeMetrics.pathLength = 0;
      this.mazeMetrics.frontierLabel = 'Frontier';
      this.mazeState.cells.forEach(cell => { cell.isVisited = false; });
      this.updateMazeVisualization();
    },

    handleExplorationStarted(message) {
      this.mazeState.isRunning = true;
      this.mazeState.path = [];
      this.mazeState.isPaused = false;
      this.mazeMetrics.visited = 0;
      this.mazeMetrics.frontier = 0;
      this.mazeMetrics.pathLength = 0;
      this.addMessage('info', message.message || 'Exploration started');
    },

    handleExplorationPaused(message) { this.mazeState.isPaused = true; this.addMessage('info', message.message || 'Exploration paused'); },
    handleExplorationResumed(message) { this.mazeState.isPaused = false; this.addMessage('info', message.message || 'Exploration resumed'); },

    handleExplorationStep(message) {
      const d = message.data || {};
      if (d.row === undefined || d.col === undefined) return;
      this.mazeMetrics.visited++;
      if (typeof d.frontier === 'number') this.mazeMetrics.frontier = d.frontier;
      const cell = this.mazeState.cells.find(c => c.row === d.row && c.col === d.col);
      if (cell) cell.isVisited = true;
      if (this.mazeVisualization) this.mazeVisualization.updateCell(d.row, d.col, 'visited');
    },

    handleExplorationComplete(message) {
      const d = message.data || {};
      this.mazeState.isRunning = false;
      this.mazeState.isPaused = false;
      this.mazeMetrics.pathLength = d.pathLength || 0;
      this.mazeState.path = Array.isArray(d.path) ? d.path : [];
      this.addMessage(d.found ? 'success' : 'info', message.message || (d.found ? 'Path found' : 'No path found'));
      this.updateMazeVisualization();
    },

    initializeMazeVisualization() {
      const container = document.getElementById('maze-visualization');
      if (container) {
        this.mazeVisualization = new MazeVisualization(container, (r, c) => this.toggleMazeCell(r, c));
        if (this.mazeState.created) this.updateMazeVisualization();
        else this.mazeVisualization.showPlaceholder();
      }
    },

    updateMazeVisualization() {
      if (!this.mazeVisualization) return;
      const { width, height, cells, startRow, startCol, goalRow, goalCol, path } = this.mazeState;
      this.mazeVisualization.updateGrid(width, height, cells, { startRow, startCol, goalRow, goalCol, path });
    },

    toggleDebug() { this.DEBUG = !this.DEBUG; this.addMessage("info", `Debug mode ${this.DEBUG ? "enabled" : "disabled"}`); },
    debug(...args) { if (this.DEBUG) console.log("[Maze Runner]", ...args); },

    initializeTheme() { this.applyTheme(this.currentTheme); },
    applyTheme(theme) {
      const root = document.documentElement;
      root.removeAttribute('data-theme');
      if (theme === 'light' || theme === 'dark') root.setAttribute('data-theme', theme);
      this.currentTheme = theme;
      localStorage.setItem('theme', theme);
    },
    toggleTheme() {
      const themes = ['light', 'dark', 'auto'];
      const nextTheme = themes[(themes.indexOf(this.currentTheme) + 1) % themes.length];
      this.applyTheme(nextTheme);
      this.addMessage("info", `Theme switched to: ${nextTheme}`);
    },
    getThemeIcon() { return { light: '☀️', dark: '🌙', auto: '🌓' }[this.currentTheme]; },
    getThemeLabel() { return { light: 'Light', dark: 'Dark', auto: 'Auto' }[this.currentTheme]; },

    sendMessage(message) {
      if (this.DEBUG) console.log('Sending message:', message);
      if (!this.isConnected || !this.socketWrapper) { console.error('WebSocket not connected or ready'); return; }
      this.socketWrapper.send(JSON.stringify(message));
    }
  }));
});

class MazeVisualization {
  constructor(container, onToggle) {
    this.container = container;
    this.onToggle = onToggle;
    this.container.style.minHeight = '320px';
    this.container.style.display = 'grid';
    this.container.style.placeItems = 'center';
  }

  showPlaceholder() {
    this.container.innerHTML = '<p style="color: var(--text-muted);">Create a maze to begin</p>';
    this.container.style.display = 'grid';
    this.container.style.padding = '20px';
  }

  updateGrid(width, height, cells, markers) {
    if (!width || !height) { this.showPlaceholder(); return; }
    if (!this.container.firstChild || this.container.dataset.width !== `${width}` || this.container.dataset.height !== `${height}`) {
      this.createGrid(width, height);
    }
    const pathSet = new Set();
    if (markers && Array.isArray(markers.path)) {
      for (const p of markers.path) {
        if (Array.isArray(p) && p.length >= 2) pathSet.add(`${p[0]},${p[1]}`);
      }
    }
    for (const cell of cells) {
      let type = 'default';
      if (cell.isWall) type = 'wall';
      else if (cell.row === markers.startRow && cell.col === markers.startCol) type = 'start';
      else if (cell.row === markers.goalRow && cell.col === markers.goalCol) type = 'goal';
      else if (pathSet.has(`${cell.row},${cell.col}`)) type = 'path';
      else if (cell.isVisited) type = 'visited';
      this.updateCell(cell.row, cell.col, type);
    }
  }

  createGrid(width, height) {
    this.container.innerHTML = '';
    this.container.dataset.width = width;
    this.container.dataset.height = height;
    const cellSize = Math.max(12, Math.floor(400 / Math.max(width, height)));
    this.container.style.gridTemplateColumns = `repeat(${width}, ${cellSize}px)`;
    this.container.style.gridTemplateRows = `repeat(${height}, ${cellSize}px)`;
    this.container.style.gap = '0px';
    for (let r = 0; r < height; r++) {
      for (let c = 0; c < width; c++) {
        const div = document.createElement('div');
        div.dataset.row = r;
        div.dataset.col = c;
        div.style.width = `${cellSize}px`;
        div.style.height = `${cellSize}px`;
        div.style.backgroundColor = this.getColor('default');
        div.style.transition = 'background-color 150ms ease-in-out';
        div.style.borderRight = c < width - 1 ? '1px solid var(--theme-border)' : 'none';
        div.style.borderBottom = r < height - 1 ? '1px solid var(--theme-border)' : 'none';
        div.style.boxSizing = 'border-box';
        div.addEventListener('click', () => this.onToggle(r, c));
        this.container.appendChild(div);
      }
    }
  }

  updateCell(row, col, type) {
    const cell = this.container.querySelector(`[data-row="${row}"][data-col="${col}"]`);
    if (cell) cell.style.backgroundColor = this.getColor(type);
  }

  getColor(type) {
    switch (type) {
      case 'wall': return 'var(--theme-text-primary)';
      case 'start': return 'var(--color-success)';
      case 'goal': return 'var(--color-error)';
      case 'path': return 'var(--color-warning)';
      case 'visited': return '#60a5fa';
      default: return 'var(--theme-surface)';
    }
  }
}
