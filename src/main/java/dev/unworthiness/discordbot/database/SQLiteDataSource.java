package dev.unworthiness.discordbot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.unworthiness.discordbot.Config;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteDataSource {
  private static Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;

  static {
    try {
      File dbFile = new File("database.db");
      if (!dbFile.exists()) {
        if (dbFile.createNewFile()) {
          LOGGER.info("Created database file.");
        } else {
          LOGGER.error("COULD NOT CREATE DATABASE FILE.");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set up hikari config
    config.setJdbcUrl("jdbc:sqlite:database.db");
    config.setConnectionTestQuery("SELECT 1");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    // create data source
    ds = new HikariDataSource(config);
    String prefix = Config.get("prefix");
    try (Statement statement = getConnection().createStatement()) {
      // language=SQLite
      statement.execute("CREATE TABLE IF NOT EXISTS guild_settings "
          + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
          + "guild_id VARCHAR(20) NOT NULL,"
          + "prefix VARCHAR(255) NOT NULL DEFAULT '" + prefix + "');");
      LOGGER.info("Table initialized.");
    } catch (SQLException s) {
      s.printStackTrace();
    }
  }

  private SQLiteDataSource() {
  }

  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}
