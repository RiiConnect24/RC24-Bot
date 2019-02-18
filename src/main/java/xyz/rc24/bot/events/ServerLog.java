/*
 * MIT License
 *
 * Copyright (c) 2017-2019 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rc24.bot.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.core.entities.LogType;
import xyz.rc24.bot.database.GuildSettingsDataManager;

import java.awt.Color;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * @author Spotlight and Artuto
 */

public class ServerLog extends ListenerAdapter
{
    private final BotCore core;
    private final GuildSettingsDataManager dataManager;

    public ServerLog(Bot bot)
    {
        this.core = bot.getCore();
        this.dataManager = bot.getGuildSettingsDataManager();
        RiiConnect24Bot.getLogger(ServerLog.class).info("Tracking :eyes:");
    }

    @Override
    public void onGuildBan(GuildBanEvent event)
    {
        EmbedBuilder builder = getEmbed("A user was banned from the server!", "#D32F2F", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event)
    {
        EmbedBuilder builder = getEmbed("A user was unbanned from the server!", "#4CAF50", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER, LogType.MOD}, builder, event);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        EmbedBuilder builder = getEmbed("A user left the server!", "#FFEB3B", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        EmbedBuilder builder = getEmbed("A user just joined the server!", "#00C853", event.getUser());

        sendEmbed(new LogType[]{LogType.SERVER}, builder, event);
    }

    private EmbedBuilder getEmbed(String title, String color, User user)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription("User: " + user.getAsMention() + " | " + user.getName() + "#" + user.getDiscriminator() +
                "\n" + "Account creation date: `" + user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`");
        builder.setColor(Color.decode(color));
        builder.setFooter("Current time: ", null);
        builder.setTimestamp(Instant.now());

        return builder;
    }

    private void sendEmbed(LogType[] logTypes, EmbedBuilder embed, GenericGuildEvent guildEvent)
    {
        long guildId = guildEvent.getGuild().getIdLong();
        GuildSettings gs = core.getGuildSettings(guildId);

        for(LogType logType : logTypes)
        {
            try
            {
                long channelId = gs.getLog(logType);
                TextChannel tc = guildEvent.getGuild().getTextChannelById(channelId);
                if(tc == null)
                {
                    dataManager.disableLog(logType, guildId);
                    return;
                }

                tc.sendMessage(embed.build()).queue();
            }
            catch(InsufficientPermissionException e)
            {
                // Remove the log from the config, since it's invalid.
                dataManager.disableLog(logType, guildId);
            }
        }
    }
}
