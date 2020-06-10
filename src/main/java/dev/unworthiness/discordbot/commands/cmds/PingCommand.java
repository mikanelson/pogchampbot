package dev.unworthiness.discordbot.commands.cmds;

import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import java.util.List;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    JDA jda = ctx.getJDA();

    jda.getRestPing().queue( (ping) -> ctx.getChannel().sendMessageFormat("Ping: %s ms", ping).queue());
  }

  @Override
  public String getName() {
    return "ping";
  }

}
