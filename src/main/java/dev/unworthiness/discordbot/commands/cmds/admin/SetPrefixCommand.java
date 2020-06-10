package dev.unworthiness.discordbot.commands.cmds.admin;

import dev.unworthiness.discordbot.PrefixHandler;
import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class SetPrefixCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    TextChannel channel = ctx.getChannel();
    List<String> args = ctx.getArgs();
    Member member = ctx.getMember();
    if (!member.hasPermission(Permission.ADMINISTRATOR)) {
      channel.sendMessage("You must have administrator permissions to use this command").queue();
    }
    if (args.isEmpty()) {
      channel.sendMessage("No prefix provided").queue();
    }
    String newPrefix = String.join("", args);
    updatePrefix(ctx.getGuild().getIdLong(), newPrefix);

    channel.sendMessageFormat("Prefix has been set to `%s`", newPrefix).queue();
  }

  @Override
  public String getName() {
    return "setprefix";
  }

  @Override
  public String getHelp() {
    return "Sets the bot's prefix for this server\n";
  }

  private void updatePrefix(long guildId, String newPrefix) {
    PrefixHandler.PREFIXES.put(guildId, newPrefix);
    try (PreparedStatement statement = SQLiteDataSource.getConnection()
        // language=SQLite
        .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {
      statement.setString(1, newPrefix);
      statement.setString(2, String.valueOf(guildId));
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
