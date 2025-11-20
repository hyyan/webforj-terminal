package com.terminal.commands;

import com.webforj.component.terminal.Terminal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeCommand implements TerminalCommand {

  @Override
  public String getName() {
    return "time";
  }

  @Override
  public String getDescription() {
    return "Display current date and time";
  }

  @Override
  public void execute(Terminal term, String[] args) {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    term.writeln("\u001B[1;32mCurrent time: " + now.format(formatter) + "\u001B[0m");
  }
}
