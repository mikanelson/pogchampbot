package dev.unworthiness.discordbot.database.economy;

import dev.unworthiness.discordbot.Config;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EconomyHandler {
  private static Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);

  public void validateUser(String guildId, String userId) {
    // see if user exists
    try (PreparedStatement statement = SQLiteDataSource.getConnection()
        // language=SQLite
        .prepareStatement("SELECT 1 FROM economy WHERE guild_id = ? AND user_id = ?;")) {
      statement.setString(1, String.valueOf(guildId));
      statement.setString(2, String.valueOf(userId));
      // if not, create user
      try (ResultSet set = statement.executeQuery()) {
        if (!set.next()) {
          try (PreparedStatement insert = SQLiteDataSource.getConnection()
              // language=SQLite
              .prepareStatement("INSERT INTO economy (guild_id,user_id) VALUES (?,?);")) {
            insert.setString(1, String.valueOf(guildId));
            insert.setString(2, String.valueOf(userId));
            insert.execute();
            LOGGER.info("User created for user ID: {} in guild: {}", userId, guildId);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
