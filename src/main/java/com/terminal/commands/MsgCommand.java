package com.terminal.commands;

import com.webforj.component.optiondialog.OptionDialog;
import com.webforj.component.terminal.Terminal;

public class MsgCommand implements TerminalCommand {

  @Override
  public String getName() {
    return "msg";
  }

  @Override
  public String getDescription() {
    return "Show a message dialog";
  }

  @Override
  public void execute(Terminal term, String[] args) {
    if (args.length < 2) {
      term.writeln("\u001B[1;31mUsage: msg <message>\u001B[0m");
      return;
    }

    String message = String.join(" ", args).substring(4);
    OptionDialog.showMessageDialog(message, "Terminal Message");
    term.writeln("\u001B[1;32mDialog shown!\u001B[0m");
  }
}
