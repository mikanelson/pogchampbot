package dev.unworthiness.discordbot;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;

public class PogchampBot {
  private PogchampBot() throws LoginException, InterruptedException {
    new JDABuilder()
    .setToken(Config.get("token"))
    .addEventListeners(new Listener())
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
