package dev.unworthiness.discordbot;

import javax.annotation.Nonnull;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener  extends ListenerAdapter {
  private static Logger LOGGER = LoggerFactory.getLogger(Listener.class);

  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    LOGGER.info("{} has initialized successfully.", event.getJDA().getSelfUser().getAsTag());
  }

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    // ignore bot messages
    User author = event.getAuthor();
    if (author.isBot()) {
      return;
    }
    String prefix = Config.get("prefix");
    String message = event.getMessage().getContentRaw();
    Member member = event.getGuild().getMember(author);
    if (message.equalsIgnoreCase(prefix + " shutdown")) {
      if (member.hasPermission(Permission.ADMINISTRATOR)) {
        // shut bot down
        LOGGER.info("Bot shut down by {}, ID: {}", author.getAsTag(), author.getIdLong());
        event.getJDA().shutdown();
        BotCommons.shutdown(event.getJDA());
      } else {
        // log attempted shut down by unauthorized user
        LOGGER.info("Attempted shutdown by {}, ID: {}", author.getAsTag(), author.getIdLong());
        // inform user
        TextChannel channel = event.getMessage().getTextChannel();
        channel.sendMessage("no u, <@" + author.getId() + ">").queue();
      }
    }
  }

  @Override
  public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    // ignore bot messages
    if (event.getAuthor().isBot()) {
      return;
    }
  }
}
