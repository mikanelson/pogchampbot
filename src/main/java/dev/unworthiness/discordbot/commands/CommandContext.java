package dev.unworthiness.discordbot.commands;

import java.util.List;
import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandContext implements ICommandContext {
  private GuildMessageReceivedEvent event;
  private List<String> args;

  public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
    this.event = event;
    this.args = args;
  }

  @Override
  public Guild getGuild() {
    return this.getEvent().getGuild();
  }

  @Override
  public GuildMessageReceivedEvent getEvent() {
    return this.event;
  }

  public List<String> getArgs() {
    return args;
  }
}
