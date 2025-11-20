package com.terminal.commands;

import com.webforj.component.optiondialog.InputDialog;
import com.webforj.component.optiondialog.OptionDialog;
import com.webforj.component.terminal.Terminal;

public class PromptCommand implements TerminalCommand {

  @Override
  public String getName() {
    return "prompt";
  }

  @Override
  public String getDescription() {
    return "Prompt the user for input";
  }

  @Override
  public void execute(Terminal term, String[] args) {
    if (args.length < 2) {
      term.writeln("\u001B[1;31mUsage: prompt <question>\u001B[0m");
      return;
    }

    String question = String.join(" ", args).substring(7);
    String answer = OptionDialog.showInputDialog(question, "Terminal Prompt", InputDialog.MessageType.QUESTION);

    if (answer != null && !answer.isEmpty()) {
      term.writeln("\u001B[1;32mYou answered: " + answer + "\u001B[0m");
    } else {
      term.writeln("\u001B[1;33mNo input provided\u001B[0m");
    }
  }
}
