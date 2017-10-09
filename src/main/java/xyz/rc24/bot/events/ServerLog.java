package xyz.rc24.bot.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.mangers.LogManager;
import xyz.rc24.bot.mangers.LogManager.LogType;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class ServerLog extends ListenerAdapter {
    private LogManager manager;
    private static final Logger logger = LoggerFactory.getLogger(ServerLog.class);

    public ServerLog(JedisPool pool) {
        this.manager = new LogManager(pool);
        logger.info("Tracking :eyes:");
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        EmbedBuilder builder = getEmbed("A user was banned from the server!",
                "#D32F2F",
                event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        EmbedBuilder builder = getEmbed("A user was unbanned from the server!",
                "#4CAF50",
                event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        try {
            if (!event.getGuild().getBans().complete().contains(event.getUser())) {
                EmbedBuilder builder = getEmbed("A user left the server!",
                        "#FFEB3B",
                        event.getUser());

                sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
            }
        } catch (InsufficientPermissionException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Warning!");
            builder.setDescription("I don't have the Ban Members permission. I won't ban anyone, but without it I can't tell if anyone's leaving, or if it was a ban.");
            builder.setFooter("Error generated at", null);
            builder.setTimestamp(Instant.now());
            sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
        }
        // Don't do anything if banned.
        // Bans send both a leave event + ban event.
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        EmbedBuilder builder = getEmbed("A user just joined the server!",
                "#00C853",
                event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
    }

    private EmbedBuilder getEmbed(String title, String color, User user) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription("User: " + user.getAsMention() + " | " + user.getName() + "#" + user.getDiscriminator() + "\n" +
                "Account creation date: `" + user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`");
        builder.setColor(Color.decode(color));
        builder.setFooter("Current time: ", null);
        builder.setTimestamp(Instant.now());
        return builder;
    }

    private void sendEmbed(LogType[] logTypes, EmbedBuilder embed, GenericGuildEvent guildEvent) {
        Long guildID = guildEvent.getGuild().getIdLong();

        for (LogType logType : logTypes) {
            if (manager.isLogEnabled(logType, guildID)) {
                try {
                    guildEvent.getJDA()
                            .getTextChannelById(manager.getLog(logType, guildID))
                            .sendMessage(embed.build()).complete();
                } catch (InsufficientPermissionException e) {
                    // Remove the log from the config, since it's invalid.
                    manager.disableLog(logType, guildID);
                }
            }
        }
    }
}
