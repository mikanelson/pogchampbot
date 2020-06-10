package dev.unworthiness.discordbot;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
  public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
  }
}
