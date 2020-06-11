package dev.unworthiness.discordbot;

import dev.unworthiness.discordbot.commands.CommandManager;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import dev.unworthiness.discordbot.database.economy.EconomyHandler;
import java.sql.Connection;
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
    long guildId = event.getGuild().getIdLong();
    String prefix = PrefixHandler.PREFIXES.computeIfAbsent(guildId, this::getPrefix);
    EconomyHandler.MUG_OTHER_COOLDOWNS.computeIfAbsent(guildId, this::getMugOtherCooldown);
    EconomyHandler.MUG_VICTIM_COOLDOWNS.computeIfAbsent(guildId, this::getMugVictimCooldown);
    String message = event.getMessage().getContentRaw();
    if (message.startsWith(prefix)) {
      manager.handle(event, prefix);
    }
  }

  private long getMugVictimCooldown(long guildId) {
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("SELECT mug_victim_cd FROM guild_settings WHERE guild_id = ?")) {
      statement.setString(1, String.valueOf(guildId));
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          return Long.parseLong(set.getString("mug_victim_cd"));
        }
      }
      statement.closeOnCompletion();
      try (PreparedStatement insert = connection
          // language=SQLite
          .prepareStatement("INSERT INTO guild_settings (guild_id) VALUES (?)")) {
        insert.setString(1, String.valueOf(guildId));
        insert.execute();
        insert.closeOnCompletion();
      }
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Long.parseLong(Config.get("cooldown_get_mugged"));
  }

  private long getMugOtherCooldown(long guildId) {
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("SELECT mug_other_cd FROM guild_settings WHERE guild_id = ?")) {
      statement.setString(1, String.valueOf(guildId));
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          return Long.parseLong(set.getString("mug_other_cd"));
        }
      }
      statement.closeOnCompletion();
      try (PreparedStatement insert = connection
          // language=SQLite
          .prepareStatement("INSERT INTO guild_settings (guild_id) VALUES (?)")) {
        insert.setString(1, String.valueOf(guildId));
        insert.execute();
        insert.closeOnCompletion();
      }
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Long.parseLong(Config.get("cooldown_mug_others"));
  }

  private String getPrefix(long guildId) {
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {
      statement.setString(1, String.valueOf(guildId));
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          return set.getString("prefix");
        }
      }
      statement.closeOnCompletion();
      try (PreparedStatement insert = connection
          // language=SQLite
          .prepareStatement("INSERT INTO guild_settings (guild_id) VALUES (?)")) {
        insert.setString(1, String.valueOf(guildId));
        insert.execute();
        insert.closeOnCompletion();
      }
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Config.get("prefix");
  }
}
