package dev.unworthiness.discordbot;

import dev.unworthiness.discordbot.commands.CommandManager;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    /*
    String prefix = Config.get("prefix");
     */
    long guildId = event.getGuild().getIdLong();
    String prefix = PrefixHandler.PREFIXES.computeIfAbsent(guildId, this::getPrefix);
    String message = event.getMessage().getContentRaw();
    if (message.startsWith(prefix)) {
      manager.handle(event, prefix);
    }
  }

  private String getPrefix(long guildId) {
    try (PreparedStatement statement = SQLiteDataSource.getConnection()
        // language=SQLite
        .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {
      statement.setString(1, String.valueOf(guildId));
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          return set.getString("prefix");
        }
      }
      try (PreparedStatement insert = SQLiteDataSource.getConnection()
          // language=SQLite
          .prepareStatement("INSERT INTO guild_settings (guild_id) VALUES (?)")) {
        insert.setString(1, String.valueOf(guildId));
        insert.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Config.get("prefix");
  }
}
