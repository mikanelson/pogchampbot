package dev.unworthiness.discordbot.commands.cmds.economy;

import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import dev.unworthiness.discordbot.database.economy.EconomyHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.entities.TextChannel;

public class MugCommand implements ICommand {
  EconomyHandler eco = new EconomyHandler();

  @Override
  public void handle(CommandContext ctx) {
    String authorId = ctx.getAuthor().getId();
    String victimId = ctx.getMessage().getMentionedMembers().get(0).getId();
    TextChannel channel = ctx.getChannel();
    if (authorId.equals(victimId)) {
      channel.sendMessage("You can't mug yourself").queue();
      return;
    }
    if (ctx.getMessage().getMentionedMembers().get(0).getUser().isBot()) {
      channel.sendMessage("You can't mug bots").queue();
      return;
    }
    String guildId = ctx.getGuild().getId();
    eco.validateUser(guildId, authorId);
    eco.validateUser(guildId, victimId);
    if (validTarget(guildId, victimId)) {
      mug(guildId, authorId, victimId, channel);
    } else {
      channel.sendMessage("Invalid target").queue();
    }
  }

  private void mug(String guildId, String authorId, String victimId, TextChannel channel) {
    Random rng = new Random();
    int roll = rng.nextInt(1000);
    if (roll <= 600) {
      executeMugging(guildId, authorId, victimId, channel);
    } else if (roll > 600 && roll <= 900) {
      channel.sendMessage("Nothing happened").queue();
    } else {
      executeMugging(guildId, victimId, authorId, channel);
    }
  }

  private boolean validTarget(String guildId, String victimId) {
    double pogchamps = getBalance(guildId, victimId);
    if (pogchamps > 0) {
      return true;
    }
    return false;
  }

  private double getBalance(String guildId, String userId) {
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    double pogchamps = 0.0;
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("SELECT pogchamps FROM economy WHERE guild_id = ? AND user_id = ?")) {
      statement.setString(1, guildId);
      statement.setString(2, userId);
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          pogchamps = Double.valueOf(set.getString("pogchamps"));
        }
      }
      statement.closeOnCompletion();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return pogchamps;
  }

  private void changeBalance(String guildId, String userId, double amount) {
    double balance = getBalance(guildId, userId);
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement = connection
        // language=SQLite
        .prepareStatement("UPDATE economy SET pogchamps = ? WHERE guild_id = ? AND user_id = ?")) {
      statement.setDouble(1, (balance + amount));
      statement.setString(2, guildId);
      statement.setString(3, userId);
      statement.executeUpdate();
      statement.closeOnCompletion();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void executeMugging(String guildId, String userId, String victimId, TextChannel channel) {
    double victimBalance = getBalance(guildId, victimId);
    double amount = Math.min(Math.random(), victimBalance);
    changeBalance(guildId, userId, amount);
    changeBalance(guildId, victimId, (amount * -1));
    channel.sendMessage("<@" + userId + "> has stolen " + amount + " from <@" + victimId + ">").queue();
  }

  @Override
  public String getName() {
    return "mug";
  }

  @Override
  public String getHelp() {
    return "Attempt to steal another user's pogchamps";
  }

  @Override
  public List<String> getAliases() {
    return Arrays.asList("mug", "steal", "catchlacking", "rob");
  }
}
