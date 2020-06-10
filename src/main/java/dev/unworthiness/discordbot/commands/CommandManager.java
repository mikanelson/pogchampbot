package dev.unworthiness.discordbot.commands;

import dev.unworthiness.discordbot.Config;
import dev.unworthiness.discordbot.commands.cmds.GCommand;
import dev.unworthiness.discordbot.commands.cmds.HelpCommand;
import dev.unworthiness.discordbot.commands.cmds.PingCommand;
import dev.unworthiness.discordbot.commands.cmds.ShutdownCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandManager {
  private List<ICommand> commands = new ArrayList<>();

  public CommandManager() {
    addCommand(new PingCommand());
    addCommand(new ShutdownCommand());
    addCommand(new GCommand());
    addCommand(new HelpCommand(this));
  }

  private void addCommand(ICommand cmd) {
    // check if the name already exists
    boolean exists = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
    if (exists) {
      throw new IllegalArgumentException("A command with this name already exists.");
    }
    commands.add(cmd);
  }

  @Nullable
  public ICommand getCommand(String cmdName) {
    for (ICommand command : this.commands) {
      if (command.getName().equalsIgnoreCase(cmdName) || command.getAliases().contains(cmdName.toLowerCase())) {
        return command;
      }
    }
    return null;
  }

  public List<ICommand> getCommands() {
    return commands;
  }

  public void handle(GuildMessageReceivedEvent event) {
    String[] split =
        event.getMessage().getContentRaw()
            .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")), "")
            .split("\\s+");

    // get command name
    String invoke = split[0].toLowerCase();
    ICommand cmd = this.getCommand(invoke);

    // assume rest of message is arguments
    if (cmd != null) {
      List<String> args = Arrays.asList(split).subList(1, split.length);
      CommandContext context = new CommandContext(event, args);
      cmd.handle(context);
    }
  }
}
