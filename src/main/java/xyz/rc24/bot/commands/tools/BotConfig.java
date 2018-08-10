package xyz.rc24.bot.commands.tools;

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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.managers.CodeManager;
import xyz.rc24.bot.managers.ServerConfigManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artu, Spotlight
 */

public class BotConfig extends Command
{
    private ServerConfigManager manager;

    public BotConfig(RiiConnect24Bot bot)
    {
        this.manager = bot.scm;
        this.children = new Command[]{new ChannelConfig(), new AddConfig()};
        this.name = "config";
        this.help = "Change important bot settings.";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.replyError("Please enter a valid option for the command.\n" +
                "Valid commands are: `setchannel`, `defaultadd`.");
    }

    private class ChannelConfig extends Command
    {
        ChannelConfig()
        {
            this.name = "setchannel";
            this.help = "Changes the channel for the given log.";
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String[] arguments = event.getArgs().split(" ");
            if(!(arguments.length == 2))
            {
                event.replyError("Invalid syntax!\n The correct format is " +
                        "`" + event.getClient().getPrefix() + "config setchannel <type> <#channel-mention/off>`.");
                return;
            }
            // I'm assuming way too much here.
            // TODO: Better checking of arguments?
            String channelType = arguments[0];
            ServerConfigManager.LogType type = channelTypes.get(channelType);

            String channelName = arguments[1];
            if(channelName.equals("off"))
            {
                manager.disableLog(type, event.getGuild().getIdLong());
                event.replySuccess("Channel successfully removed as a log.");
                return;
            }

            // Set (potentially) obtained channel
            Long channelID = getChannelId(channelName, event.getGuild());

            if(channelID==0L)
                event.replyError("I wasn't able to find that channel on this server! No changes have been made to your server's config.");
            else
            {
                if (type==null)
                    event.replyError(Const.getChannelTypes());
                else
                {
                    manager.setLog(event.getGuild().getIdLong(), type, channelID);
                    event.replySuccess("Successfully set " + event.getJDA().getTextChannelById(channelID).getAsMention() + " as " + channelType + "!");
                }
            }
        }

        final Map<String, ServerConfigManager.LogType> channelTypes = new HashMap<String, ServerConfigManager.LogType>()
        {{
            put("mod", ServerConfigManager.LogType.MOD);
            put("mod-log", ServerConfigManager.LogType.MOD);

            put("srv", ServerConfigManager.LogType.SERVER);
            put("server", ServerConfigManager.LogType.SERVER);
            put("server-log", ServerConfigManager.LogType.SERVER);
        }};

        private Long getChannelId(String name, Guild currentGuild)
        {
            List<TextChannel> potentialChannels = FinderUtil.findTextChannels(name, currentGuild);
            if(potentialChannels.isEmpty())
                return 0L;
            else
                // Grab the first text channel's Long, and return.
                return potentialChannels.get(0).getIdLong();
        }
    }

    private class AddConfig extends Command
    {
        AddConfig()
        {
            this.name = "defaultadd";
            this.help = "Changes the default `add` command's type.";
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String channelType = event.getArgs();
            try
            {
                CodeManager.Type addType = Const.namesToType.get(channelType);
                manager.setDefaultAddType(event.getGuild().getIdLong(), addType);
                event.replySuccess("Successfully set " + Const.typesToProductName.get(addType) + " as default `add` type!");
            }
            catch(NullPointerException unused)
            {
                event.replyError(Const.getCodeTypes());
            }
        }
    }
}
