package com.terminal.commands;

import com.terminal.views.TerminalView;
import com.webforj.Interval;
import com.webforj.component.terminal.Terminal;
import com.webforj.component.terminal.event.TerminalKeyEvent;

import java.util.*;

public class SnakeCommand implements TerminalCommand {

  private static final int WIDTH = 40;
  private static final int HEIGHT = 20;
  private static final int GAME_SPEED = 150; // milliseconds

  @Override
  public String getName() {
    return "snake";
  }

  @Override
  public String getDescription() {
    return "Play the classic Snake game (Use arrow keys to move, 'q' to quit)";
  }

  @Override
  public void execute(Terminal term, String[] args) {
    new SnakeGame(term).start();
  }

  private static class SnakeGame {
    private final Terminal terminal;
    private final TerminalView terminalView;
    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private boolean running = false;
    private int score = 0;
    private Interval gameInterval;

    enum Direction {
      UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

      final int dx, dy;

      Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
      }
    }

    static class Point {
      int x, y;

      Point(int x, int y) {
        this.x = x;
        this.y = y;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
      }

      @Override
      public int hashCode() {
        return Objects.hash(x, y);
      }
    }

    public SnakeGame(Terminal terminal) {
      this.terminal = terminal;
      this.terminalView = (TerminalView) terminal.getUserData("terminalView");
    }

    public void start() {
      // Disable history navigation while game is active
      if (terminalView != null) {
        terminalView.setHistoryNavigationEnabled(false);
      }

      // Initialize snake
      snake.clear();
      snake.add(new Point(WIDTH / 2, HEIGHT / 2));
      snake.add(new Point(WIDTH / 2 - 1, HEIGHT / 2));
      snake.add(new Point(WIDTH / 2 - 2, HEIGHT / 2));

      direction = Direction.RIGHT;
      nextDirection = Direction.RIGHT;
      score = 0;

      // Spawn food
      spawnFood();

      // Clear and draw
      terminal.clear();
      drawWelcome();
      drawGame();

      // Set up key listener
      terminal.onKey(this::handleKey);

      // Start auto-movement
      running = true;
      gameInterval = new Interval(GAME_SPEED / 1000.0f, e -> {
        if (running) {
          update();
        }
      });
      gameInterval.start();
    }

    private void drawWelcome() {
      terminal.writeln("\u001B[1;36m╔════════════════════════════════════════════════════════════════╗\u001B[0m");
      terminal.writeln("\u001B[1;36m║                         SNAKE GAME                             ║\u001B[0m");
      terminal.writeln("\u001B[1;36m╚════════════════════════════════════════════════════════════════╝\u001B[0m");
      terminal.writeln("\u001B[1;33mUse Arrow Keys to move  |  Press 'Q' to quit\u001B[0m");
      terminal.writeln("");
    }

    private void handleKey(TerminalKeyEvent event) {
      if (!running) return;

      String key = event.getKey();

      switch (key.toLowerCase()) {
        case "arrowup":
          if (direction != Direction.DOWN) nextDirection = Direction.UP;
          break;
        case "arrowdown":
          if (direction != Direction.UP) nextDirection = Direction.DOWN;
          break;
        case "arrowleft":
          if (direction != Direction.RIGHT) nextDirection = Direction.LEFT;
          break;
        case "arrowright":
          if (direction != Direction.LEFT) nextDirection = Direction.RIGHT;
          break;
        case "q":
          stop();
          break;
      }
    }

    private void update() {
      // Update direction
      direction = nextDirection;

      // Get current head
      Point head = snake.getFirst();

      // Calculate new head position
      Point newHead = new Point(head.x + direction.dx, head.y + direction.dy);

      // Check wall collision
      if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
        gameOver("You hit the wall!");
        return;
      }

      // Check self collision
      if (snake.contains(newHead)) {
        gameOver("You bit yourself!");
        return;
      }

      // Add new head
      snake.addFirst(newHead);

      // Check if ate food
      if (newHead.equals(food)) {
        score += 10;
        spawnFood();
        // Don't remove tail - snake grows
      } else {
        // Remove tail - snake stays same length
        snake.removeLast();
      }

      // Redraw
      drawGame();
    }

    private void drawGame() {
      StringBuilder frame = new StringBuilder();

      // Move cursor to game area
      frame.append("\u001B[6;0H");

      // Draw top border
      frame.append("\u001B[1;34m┌");
      for (int i = 0; i < WIDTH; i++) {
        frame.append("─");
      }
      frame.append("┐\u001B[0m\n");

      // Draw game area
      for (int y = 0; y < HEIGHT; y++) {
        frame.append("\u001B[1;34m│\u001B[0m");
        for (int x = 0; x < WIDTH; x++) {
          Point p = new Point(x, y);
          if (snake.getFirst().equals(p)) {
            frame.append("\u001B[1;32m●\u001B[0m"); // Head
          } else if (snake.contains(p)) {
            frame.append("\u001B[32m○\u001B[0m"); // Body
          } else if (food.equals(p)) {
            frame.append("\u001B[1;31m★\u001B[0m"); // Food
          } else {
            frame.append(" ");
          }
        }
        frame.append("\u001B[1;34m│\u001B[0m\n");
      }

      // Draw bottom border
      frame.append("\u001B[1;34m└");
      for (int i = 0; i < WIDTH; i++) {
        frame.append("─");
      }
      frame.append("┘\u001B[0m\n\n");

      // Draw score
      frame.append("\u001B[1;33mScore: ").append(score)
           .append("  |  Length: ").append(snake.size())
           .append("\u001B[0m\n");

      terminal.write(frame.toString());
    }

    private void spawnFood() {
      Random random = new Random();
      do {
        food = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
      } while (snake.contains(food));
    }

    private void gameOver(String reason) {
      running = false;
      if (gameInterval != null) {
        gameInterval.stop();
      }

      // Re-enable history navigation
      if (terminalView != null) {
        terminalView.setHistoryNavigationEnabled(true);
      }

      terminal.writeln("");
      terminal.writeln("\u001B[1;31m╔════════════════════════════════════════════════════════════════╗\u001B[0m");
      terminal.writeln("\u001B[1;31m║                          GAME OVER!                            ║\u001B[0m");
      terminal.writeln("\u001B[1;31m╚════════════════════════════════════════════════════════════════╝\u001B[0m");
      terminal.writeln("");
      terminal.writeln("\u001B[1;33m" + reason + "\u001B[0m");
      terminal.writeln("\u001B[1;36mFinal Score: " + score + "\u001B[0m");
      terminal.writeln("\u001B[1;36mFinal Length: " + snake.size() + "\u001B[0m");
      terminal.writeln("");
      terminal.writeln("Type \u001B[1;32msnake\u001B[0m to play again!");
      terminal.writeln("");
      terminal.write("$ ");
    }

    private void stop() {
      running = false;
      if (gameInterval != null) {
        gameInterval.stop();
      }

      // Re-enable history navigation
      if (terminalView != null) {
        terminalView.setHistoryNavigationEnabled(true);
      }

      terminal.writeln("");
      terminal.writeln("\u001B[1;33mGame stopped. Final score: " + score + "\u001B[0m");
      terminal.writeln("");
      terminal.write("$ ");
    }
  }
}
