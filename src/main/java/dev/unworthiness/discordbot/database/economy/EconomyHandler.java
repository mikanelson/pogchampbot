package dev.unworthiness.discordbot.database.economy;

import dev.unworthiness.discordbot.database.SQLiteDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EconomyHandler {
  public static Map<Long, Long> MUG_OTHER_COOLDOWNS = new HashMap<>();
  public static Map<Long, Long> MUG_VICTIM_COOLDOWNS = new HashMap<>();
  private static Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);

  public void validateUser(String guildId, String userId) {
    // see if user exists
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("SELECT 1 FROM economy WHERE guild_id = ? AND user_id = ?;")) {
      statement.setString(1, String.valueOf(guildId));
      statement.setString(2, String.valueOf(userId));
      // if not, create user
      try (ResultSet set = statement.executeQuery()) {
        if (!set.next()) {
          try (PreparedStatement insert = connection
              // language=SQLite
              .prepareStatement("INSERT INTO economy (guild_id,user_id) VALUES (?,?);")) {
            insert.setString(1, String.valueOf(guildId));
            insert.setString(2, String.valueOf(userId));
            insert.execute();
            LOGGER.info("User created for user ID: {} in guild: {}", userId, guildId);
          }
        }
      }
      statement.closeOnCompletion();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
