package dev.unworthiness.discordbot.commands.cmds.economy.admin;

import dev.unworthiness.discordbot.commands.CommandContext;
import dev.unworthiness.discordbot.commands.ICommand;
import dev.unworthiness.discordbot.database.SQLiteDataSource;
import dev.unworthiness.discordbot.database.economy.EconomyHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class SetGetMuggedCdCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    TextChannel channel = ctx.getChannel();
    List<String> args = ctx.getArgs();
    Member member = ctx.getMember();
    if (!member.hasPermission(Permission.ADMINISTRATOR)) {
      channel.sendMessage("You must have administrator permissions to use this command").queue();
    }
    if (args.isEmpty()) {
      channel.sendMessage("No cooldown provided").queue();
    }
    long newCd = Long.parseLong(args.get(0));
    updateMuggedCd(ctx.getGuild().getIdLong(), newCd);

    channel.sendMessageFormat("Mug victim cooldown has been set to `%s`", newCd).queue();
  }

  @Override
  public String getName() {
    return "setmuggedcd";
  }

  @Override
  public String getHelp() {
    return "Sets cooldown, in seconds, for how often users can be mugged";
  }

  private void updateMuggedCd(long guildId, long cd) {
    EconomyHandler.MUG_VICTIM_COOLDOWNS.put(guildId, cd);
    Connection connection = null;
    try {
      connection = SQLiteDataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement statement =
        connection
            // language=SQLite
            .prepareStatement("UPDATE guild_settings SET mug_victim_cd = ? WHERE guild_id = ?")) {
      statement.setLong(1, cd);
      statement.setString(2, String.valueOf(guildId));
      statement.executeUpdate();
      statement.closeOnCompletion();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
