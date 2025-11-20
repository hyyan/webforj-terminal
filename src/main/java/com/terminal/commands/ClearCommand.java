package com.terminal.commands;

import com.webforj.component.terminal.Terminal;

public class ClearCommand implements TerminalCommand {

  @Override
  public String getName() {
    return "clear";
  }

  @Override
  public String getDescription() {
    return "Clear the terminal screen";
  }

  @Override
  public void execute(Terminal term, String[] args) {
    term.clear();
  }
}
