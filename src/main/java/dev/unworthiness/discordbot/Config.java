package dev.unworthiness.discordbot;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
  private static Dotenv dotenv = Dotenv.load();

  public static String get(String key) {
    return dotenv.get(key.toUpperCase());
  }
}
