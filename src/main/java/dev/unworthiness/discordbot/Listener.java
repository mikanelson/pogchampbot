package dev.unworthiness.discordbot;

import dev.unworthiness.discordbot.commands.CommandManager;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener  extends ListenerAdapter {
  private static Logger LOGGER = LoggerFactory.getLogger(Listener.class);

  private CommandManager manager = new CommandManager();

  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    LOGGER.info("{} has initialized successfully.", event.getJDA().getSelfUser().getAsTag());
  }

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    // ignore bot messages
    User author = event.getAuthor();
    if (author.isBot() || event.isWebhookMessage()) {
      return;
    }

    String prefix = Config.get("prefix");
    String message = event.getMessage().getContentRaw();
    if (message.startsWith(prefix)) {
      manager.handle(event);
    }
  }
}
