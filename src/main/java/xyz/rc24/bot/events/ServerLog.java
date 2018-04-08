package xyz.rc24.bot.events;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.managers.ServerConfigManager;
import xyz.rc24.bot.managers.ServerConfigManager.LogType;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;


/**
 * @author Spotlight
 */
public class ServerLog extends ListenerAdapter
{
    private ServerConfigManager manager;
    private static final Logger logger = (Logger)LoggerFactory.getLogger(ServerLog.class);

    public ServerLog()
    {
        this.manager = new ServerConfigManager();
        logger.info("Tracking :eyes:");
    }

    @Override
    public void onGuildBan(GuildBanEvent event)
    {
        EmbedBuilder builder = getEmbed("A user was banned from the server!",
                "#D32F2F", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event)
    {
        EmbedBuilder builder = getEmbed("A user was unbanned from the server!",
                "#4CAF50", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        try
        {
            if (!(event.getGuild().getBanList().complete().contains(event.getUser())))
            {
                EmbedBuilder builder = getEmbed("A user left the server!",
                        "#FFEB3B", event.getUser());

                sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
            }
        }
        catch(InsufficientPermissionException e)
        {
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
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        EmbedBuilder builder = getEmbed("A user just joined the server!",
                "#00C853", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
    }

    private EmbedBuilder getEmbed(String title, String color, User user)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription("User: " + user.getAsMention() + " | " + user.getName() + "#" + user.getDiscriminator() + "\n" +
                "Account creation date: `" + user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`");
        builder.setColor(Color.decode(color));
        builder.setFooter("Current time: ", null);
        builder.setTimestamp(Instant.now());

        return builder;
    }

    private void sendEmbed(LogType[] logTypes, EmbedBuilder embed, GenericGuildEvent guildEvent)
    {
        Long guildID = guildEvent.getGuild().getIdLong();

        for(LogType logType : logTypes)
        {
            try
            {
                Long channelID = manager.getLog(logType, guildID);
                if(channelID==null) return;
                guildEvent.getJDA().getTextChannelById(channelID).sendMessage(embed.build()).queue();
            }
            catch (InsufficientPermissionException e)
            {
                // Remove the log from the config, since it's invalid.
                manager.disableLog(logType, guildID);
            }
        }
    }
}
