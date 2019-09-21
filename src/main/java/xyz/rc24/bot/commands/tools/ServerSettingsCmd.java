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

package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.core.entities.LogType;
import xyz.rc24.bot.database.GuildSettingsDataManager;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

/**
 * @author Artuto
 */

public class ServerSettingsCmd extends Command
{
    private GuildSettingsDataManager dataManager;

    public ServerSettingsCmd(Bot bot)
    {
        this.dataManager = bot.getGuildSettingsDataManager();
        this.name = "serversettings";
        this.aliases = new String[]{"settings", "serverconfig", "config"};
        this.help = "Change important bot settings.";
        this.children = new Command[]{new SetChannelCmd(), new DefaultAddCmd()};
        this.category = Categories.TOOLS;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.replyError("Please enter a valid option for the command.\n" +
                "Valid subcommands are: `setchannel`, `defaultadd`.");
    }

    private class SetChannelCmd extends Command
    {
        SetChannelCmd()
        {
            this.name = "setchannel";
            this.help = "Changes the channel for the given log.";
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            String[] arguments = event.getArgs().split("\\s+");

            if(!(arguments.length > 1))
            {
                event.replyError("Invalid syntax!\n The correct format is " +
                        "`" + gs.getFirstPrefix() + "serversettings setchannel <type> <#channel-mention/off>`.");
                return;
            }

            String channelType = arguments[0];
            LogType type = LogType.fromCode(channelType);

            if(type == LogType.UNKNOWN)
            {
                event.replyError(FormatUtil.getChannelTypes());
                return;
            }

            String channelName = arguments[1];
            if(channelName.equalsIgnoreCase("off"))
            {
                if(dataManager.disableLog(type, event.getGuild().getIdLong()))
                    event.replySuccess("Channel successfully removed as a log.");
                else
                    event.replyError("Error whilst removing a log! Please contact a developer.");

                return;
            }

            TextChannel channel = SearcherUtil.findTextChannel(event, channelName);
            if(channel == null)
                return;

            if(dataManager.setLog(type, event.getGuild().getIdLong(), channel.getIdLong()))
                event.replySuccess("Successfully set " + channel.getAsMention() + " as `" + channelType + "`!");
            else
                event.replyError("Error whilst updating a log channel! Please contact a developer.");
        }
    }

    private class DefaultAddCmd extends Command
    {
        DefaultAddCmd()
        {
            this.name = "defaultadd";
            this.help = "Changes the default `add` command's type.";
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            CodeType type = CodeType.fromCode(event.getArgs());
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            if(dataManager.setDefaultAddType(type, event.getGuild().getIdLong()))
                event.replySuccess("Successfully set `" + type.getName() + "` as default `add` type!");
            else
                event.replyError("Error whilst updating the default add type! Please contact a developer.");
        }
    }
}
