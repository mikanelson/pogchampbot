package dev.unworthiness.discordbot.commands.cmds;

import dev.unworthiness.discordbot.Config;
import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.CommandManager;
import dev.unworthiness.discordbot.commands.ICommand;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand implements ICommand {
  CommandManager manager;

  public HelpCommand(CommandManager manager) {
    this.manager = manager;
  }

  @Override
  public void handle(CommandContext ctx) {
    List<String> args = ctx.getArgs();
    TextChannel channel = ctx.getChannel();
    // generic help command
    if (args.isEmpty()) {
      StringBuilder builder = new StringBuilder();
      builder.append("Commands: \n");
      // pull name for each command and add to string builder
      manager.getCommands().stream().map(ICommand::getName).forEach(
          (cmd) -> builder.append('`').append(Config.get("prefix")).append(cmd).append("`\n")
      );
      // send results
      channel.sendMessage(builder.toString()).queue();
      return;
    }
    // command specific help
    // find command
    String query = args.get(0);
    ICommand command = manager.getCommand(query);
    // if not found, inform user
    if (command == null) {
      channel.sendMessage(query + " is not a valid command name").queue();
      return;
    }
    // send command help message
    channel.sendMessage(command.getHelp()).queue();
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getHelp() {
    return "Lists commands for bot\n`" +
        Config.get("prefix") + "help <command>`";
  }

  @Override
  public List<String> getAliases() {
    return Arrays.asList("help", "command", "commands", "cmd", "cmds");
  }
}
