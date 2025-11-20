package com.terminal.views;

import com.terminal.commands.*;
import com.webforj.component.Composite;
import com.webforj.component.terminal.Terminal;
import com.webforj.component.terminal.event.TerminalDataEvent;
import com.webforj.component.terminal.event.TerminalKeyEvent;
import com.webforj.router.annotation.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("/terminal")
public class TerminalView extends Composite<Terminal> {

  private Terminal self = getBoundComponent();
  private StringBuilder commandBuffer = new StringBuilder();
  private List<String> commandHistory = new ArrayList<>();
  private int historyIndex = -1;
  private final Map<String, TerminalCommand> commands = new HashMap<>();
  private boolean historyNavigationEnabled = true;

  public TerminalView() {
    // Store reference to this view in the terminal component
    self.setUserData("terminalView", this);

    // Configure terminal
    self.setAutoFit(true)
        .setSize("100%", "100vh")
        .addDataListener(this::onData);

    // Display welcome message
    self.writeln("\u001B[1;32m╔═══════════════════════════════════════════════════════╗\u001B[0m");
    self.writeln("\u001B[1;32m║                                                       ║\u001B[0m");
    self.writeln("\u001B[1;32m║        Welcome to the webforJ Terminal Demo!         ║\u001B[0m");
    self.writeln("\u001B[1;32m║                                                       ║\u001B[0m");
    self.writeln("\u001B[1;32m╚═══════════════════════════════════════════════════════╝\u001B[0m");
    self.writeln("");
    self.writeln("Type \u001B[1;33mhelp\u001B[0m to see available commands.");
    self.writeln("Type \u001B[1;33msnake\u001B[0m to play the Snake game!");
    self.writeln("Use \u001B[1;33m↑↓\u001B[0m arrow keys to navigate history.");
    self.writeln("");
    self.write("$ ");

    // Register commands
    registerCommands();

    // Setup key handler for history navigation
    self.onKey(this::handleKey);

    // Focus the terminal
    self.focus();
  }

  private void registerCommands() {
    List<TerminalCommand> commandList = List.of(
        new HelpCommand(commands),
        new ClearCommand(),
        new TimeCommand(),
        new SnakeCommand(),
        new MsgCommand(),
        new PromptCommand(),
        new HistoryCommand(commandHistory)
    );

    for (TerminalCommand command : commandList) {
      commands.put(command.getName(), command);
    }
  }

  /**
   * Enable or disable history navigation with arrow keys.
   *
   * @param enabled true to enable history navigation, false to disable
   */
  public void setHistoryNavigationEnabled(boolean enabled) {
    this.historyNavigationEnabled = enabled;
  }

  /**
   * Check if history navigation is enabled.
   *
   * @return true if history navigation is enabled, false otherwise
   */
  public boolean isHistoryNavigationEnabled() {
    return historyNavigationEnabled;
  }

  /**
   * Get the terminal component.
   *
   * @return the terminal component
   */
  public Terminal getTerminal() {
    return self;
  }

  private void handleKey(TerminalKeyEvent event) {
    if (!historyNavigationEnabled) {
      return;
    }

    String key = event.getKey();

    // Handle history navigation with arrow keys
    if (key.equals("ArrowUp")) {
      if (historyIndex > 0) {
        historyIndex--;
        replaceCommandBuffer(commandHistory.get(historyIndex));
      }
    } else if (key.equals("ArrowDown")) {
      if (historyIndex < commandHistory.size() - 1) {
        historyIndex++;
        replaceCommandBuffer(commandHistory.get(historyIndex));
      } else if (historyIndex == commandHistory.size() - 1) {
        historyIndex++;
        replaceCommandBuffer("");
      }
    }
  }

  private void onData(TerminalDataEvent e) {
    String input = e.getValue();
    boolean isPrintable = input.chars().allMatch(c -> (c >= 0x20 && c <= 0x7E) || c >= 0xA0);

    switch (input) {
      case "\r":
        self.write("\r\n");
        if (commandBuffer.length() > 0) {
          commandHistory.add(commandBuffer.toString());
          historyIndex = commandHistory.size();
        }
        processCommand();
        break;

      case "\u007F":
      case "\b":
        if (commandBuffer.length() > 0) {
          commandBuffer.deleteCharAt(commandBuffer.length() - 1);
          self.write("\b \b");
        }
        break;

      default:
        // Ignore arrow key escape sequences - they're handled by onKey
        if (!input.startsWith("\u001b[")) {
          if (isPrintable) {
            commandBuffer.append(input);
            self.write(input);
          }
        }
        break;
    }
  }

  private void replaceCommandBuffer(String newBuffer) {
    while (commandBuffer.length() > 0) {
      self.write("\b \b");
      commandBuffer.setLength(commandBuffer.length() - 1);
    }
    commandBuffer.append(newBuffer);
    self.write(newBuffer);
  }

  private void processCommand() {
    String commandLine = commandBuffer.toString();
    commandBuffer.setLength(0);

    if (commandLine.isBlank()) {
      self.write("$ ");
      return;
    }

    // Parse command and arguments
    String[] parts = commandLine.trim().split("\\s+");
    String commandName = parts[0].toLowerCase();

    // Execute command
    TerminalCommand command = commands.get(commandName);
    if (command != null) {
      try {
        command.execute(self, parts);
      } catch (Exception e) {
        self.writeln("\u001B[1;31mError executing command: " + e.getMessage() + "\u001B[0m");
      }
    } else {
      self.writeln("\u001B[1;31mCommand not found: " + commandName + "\u001B[0m");
      self.writeln("Type \u001B[1;33mhelp\u001B[0m to see available commands.");
    }

    self.write("$ ");
  }
}
