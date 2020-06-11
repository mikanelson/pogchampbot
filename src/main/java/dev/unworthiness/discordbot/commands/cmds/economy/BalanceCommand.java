package dev.unworthiness.discordbot.commands.cmds.economy;

import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import dev.unworthiness.discordbot.database.economy.EconomyHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;

public class BalanceCommand implements ICommand {
  EconomyHandler eco = new EconomyHandler();

  @Override
  public void handle(CommandContext ctx) {
    String guildId = ctx.getGuild().getId();
    String userId = ctx.getAuthor().getId();
    TextChannel channel = ctx.getChannel();
    eco.validateUser(guildId, userId);
    try (PreparedStatement statement = SQLiteDataSource.getConnection()
        // language=SQLite
        .prepareStatement("SELECT pogchamps,weirdchamps FROM economy WHERE guild_id = ? AND user_id = ?")) {
      statement.setString(1, String.valueOf(guildId));
      statement.setString(2, String.valueOf(userId));
      Double pogchamps = 0.0;
      Double weirdchamps = 0.0;
      try (ResultSet set = statement.executeQuery()) {
        if (set.next()) {
          pogchamps = Double.valueOf(set.getString("pogchamps"));
          weirdchamps = Double.valueOf(set.getString("weirdchamps"));
        }
      }
      channel.sendMessageFormat("<:pogchamp:720433080447533107>: " + pogchamps + "\n<:weirdchamp:720433062973800488>: " + weirdchamps +
          "\n<:pogchamp:720433080447533107> to <:weirdchamp:720433062973800488> ratio: " + (pogchamps / weirdchamps)).queue();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getName() {
    return "balance";
  }

  @Override
  public String getHelp() {
    return "Shows balances for user";
  }

  @Override
  public List<String> getAliases() {
    return Arrays.asList("balance", "bal", "eco", "pogs", "pogchamps");
  }
}
