package com.terminal.commands;

import com.webforj.component.terminal.Terminal;

public interface TerminalCommand {

  /**
   * Get the command name.
   *
   * @return the command name
   */
  String getName();

  /**
   * Get the command description.
   *
   * @return the command description
   */
  String getDescription();

  /**
   * Execute the command.
   *
   * @param term the terminal
   * @param args the command arguments
   */
  void execute(Terminal term, String[] args);
}
