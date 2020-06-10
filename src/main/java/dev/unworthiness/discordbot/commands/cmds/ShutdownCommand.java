package dev.unworthiness.discordbot.commands.cmds;

import dev.unworthiness.discordbot.Listener;
import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import java.util.Arrays;
import java.util.List;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownCommand implements ICommand {
  private static Logger LOGGER = LoggerFactory.getLogger(Listener.class);

  @Override
  public void handle(CommandContext ctx) {
    User author = ctx.getAuthor();
    Member member = ctx.getEvent().getGuild().getMember(author);
    assert member != null;
    if (member.hasPermission(Permission.ADMINISTRATOR)) {
      LOGGER.info("Bot shut down by {}, ID: {}", author.getAsTag(), author.getId());
      // shut bot down
      ctx.getEvent().getJDA().shutdown();
      BotCommons.shutdown(ctx.getEvent().getJDA());
    } else {
      // log attempted shut down by unauthorized user
      LOGGER.info("Bot shutdown attempt by {}, ID: {}", author.getAsTag(), author.getId());
      // inform user
      TextChannel channel = ctx.getChannel();
      channel.sendMessage("no u, <@" + author.getId() + ">").queue();
    }
  }

  @Override
  public String getName() {
    return "shutdown";
  }

  @Override
  public List<String> getAliases() {
    List<String> aliases = Arrays.asList("shutdown", "quit");
    return aliases;
  }
}
