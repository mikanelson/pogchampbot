package dev.unworthiness.discordbot;

import dev.unworthiness.discordbot.database.SQLiteDataSource;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class PogchampBot {
  private PogchampBot() throws LoginException, InterruptedException, SQLException {
    SQLiteDataSource.getConnection();
    JDABuilder.createDefault(Config.get("token"),
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_EMOJIS)
        .disableCache(CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
        .addEventListeners(new Listener())
        .setActivity(Activity.watching("videos on how to end it"))
        .build()
        .awaitReady();
  }

  public static void main(String[] args) {
    try {
      new PogchampBot();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
