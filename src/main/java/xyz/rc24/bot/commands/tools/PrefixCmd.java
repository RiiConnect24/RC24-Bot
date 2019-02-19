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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.GuildSettingsDataManager;

/**
 * @author Artuto
 */

public class PrefixCmd extends Command
{
    private final GuildSettingsDataManager dataManager;

    public PrefixCmd(GuildSettingsDataManager dataManager)
    {
        this.dataManager = dataManager;
        this.name = "prefix";
        this.help = "Change and display the bot's prefixes.";
        this.children = new Command[]{new AddCmd(), new RemoveCmd()};
        this.category = Categories.TOOLS;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
        event.reply("ℹ The prefixes in this server are: " + formatPrefixes(gs));
    }

    private class AddCmd extends Command
    {
        private AddCmd()
        {
            this.name = "add";
            this.help = "Add a prefix.";
            this.category = Categories.TOOLS;
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            Guild guild = event.getGuild();
            GuildSettings gs = event.getClient().getSettingsFor(guild);
            String prefix = event.getArgs().trim();

            if(gs.getPrefixes().contains(prefix))
            {
                event.replyError("This prefix was already added!");
                return;
            }

            if(dataManager.addPrefix(guild.getIdLong(), prefix))
                event.replySuccess("Successfully added `" + prefix + "` as a prefix!");
            else
                event.replyError("Error whilst adding a prefix! Please contact a developer.");
        }
    }

    private class RemoveCmd extends Command
    {
        private RemoveCmd()
        {
            this.name = "remove";
            this.help = "Remove a prefix.";
            this.category = Categories.TOOLS;
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            Guild guild = event.getGuild();
            GuildSettings gs = event.getClient().getSettingsFor(guild);
            String prefix = event.getArgs().trim();

            if(!(gs.getPrefixes().contains(prefix)))
            {
                event.replyError("This prefix is not added!");
                return;
            }

            if(dataManager.removePrefix(guild.getIdLong(), prefix))
                event.replySuccess("Successfully removed `" + prefix + "` as a prefix!");
            else
                event.replyError("Error whilst removing a prefix! Please contact a developer.");
        }
    }

    private String formatPrefixes(GuildSettings gs)
    {
        if(gs.getPrefixes().isEmpty())
            return "`@mention`";

        StringBuilder sb = new StringBuilder();
        for(String prefix : gs.getPrefixes())
            sb.append("`").append(prefix).append("`, ");

        return sb.substring(0, sb.length() - 2);
    }
}
