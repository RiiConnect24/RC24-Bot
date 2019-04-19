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

package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Artuto
 */

public class CodeCmd extends Command
{
    private final BotCore core;
    private final CodeDataManager dataManager;

    private final Pattern FULL_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)\\s+(\\d{4}[-\\s]\\d{4}[-\\s]\\d{4}" +
            "(?:[-\\s]\\d{4})|\\w+)$", Pattern.MULTILINE); // thanks Dismissed
    private final Pattern REMOVE_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)$", Pattern.MULTILINE);

    public CodeCmd(Bot bot)
    {
        this.core = bot.getCore();
        this.dataManager = bot.getCodeDataManager();
        this.name = "code";
        this.help = "Manages friend codes for the user.";
        this.children = new Command[]{new AddCmd(), new EditCmd(), new LookupCmd(), new RemoveCmd()};
        this.category = Categories.WII;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.replyError("Please enter a valid option for the command.\n" +
                "Valid subcommands: `add`, `edit`, `remove`, `lookup`, `help`.");
    }

    private class AddCmd extends Command
    {
        AddCmd()
        {
            this.name = "add";
            this.help = "Adds a code.";
            this.category = Categories.WII;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(FULL_PATTERN, event.getArgs());
            if(args.size() < 3)
            {
                event.replyError("Wrong format! Correct one is `" + gs.getFirstPrefix() + "code add <type> <name> <code>`");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = core.getCodesForType(type, event.getAuthor().getIdLong());
            if(codeTypes.containsKey(args.get(1)))
            {
                event.replyWarning("You already added this code!");
                return;
            }

            if(dataManager.addCode(type, event.getAuthor().getIdLong(), args.get(2), args.get(1)))
                event.replySuccess("Added a code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst adding a code! Please contact a developer.");
        }
    }

    private class EditCmd extends Command
    {
        EditCmd()
        {
            this.name = "edit";
            this.help = "Edits codes.";
            this.category = Categories.WII;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(FULL_PATTERN, event.getArgs());
            if(args.size() < 3)
            {
                event.replyError("Wrong format! Correct one is `" + gs.getFirstPrefix() + "code edit <type> <name> <code>");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = core.getCodesForType(type, event.getAuthor().getIdLong());
            if(!(codeTypes.containsKey(args.get(1))))
            {
                event.replyWarning("A code for `" + args.get(1) + "` is not registered.");
                return;
            }

            if(dataManager.editCode(type, event.getAuthor().getIdLong(), args.get(2), args.get(1)))
                event.replySuccess("Edited the code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst editing a code! Please contact a developer.");
        }
    }

    private class LookupCmd extends Command
    {
        LookupCmd()
        {
            this.name = "lookup";
            this.help = "Displays codes for the user.";
            this.category = Categories.WII;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event)
        {
            Member member = SearcherUtil.findMember(event, event.getArgs());
            if(member == null)
                return;

            EmbedBuilder codeEmbed = new EmbedBuilder().setAuthor("Profile for " + member.getEffectiveName(),
                    null, member.getUser().getEffectiveAvatarUrl());

            Map<CodeType, Map<String, String>> userCodes = core.getAllCodes(member.getUser().getIdLong());
            for(Map.Entry<CodeType, Map<String, String>> typeData : userCodes.entrySet())
            {
                Map<String, String> codes = typeData.getValue();
                if(!(codes.isEmpty()))
                {
                    codeEmbed.addField(typeData.getKey().getFormattedName(),
                            FormatUtil.getCodeLayout(codes), true);
                }
            }

            if(codeEmbed.getFields().isEmpty())
                event.replyError("**" + member.getEffectiveName() + "** has not added any codes!");
            else
                event.reply(codeEmbed.build());
        }
    }

    private class RemoveCmd extends Command
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a code.";
            this.category = Categories.WII;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
            List<String> args = parseArgs(REMOVE_PATTERN, event.getArgs());
            if(args.size() < 2)
            {
                event.replyError("Wrong format! Correct one is `" + gs.getFirstPrefix() + "code remove <type> <name>");
                return;
            }

            CodeType type = CodeType.fromCode(args.get(0));
            if(type == CodeType.UNKNOWN)
            {
                event.replyError(FormatUtil.getCodeTypes());
                return;
            }

            Map<String, String> codeTypes = core.getCodesForType(type, event.getAuthor().getIdLong());
            if(!(codeTypes.containsKey(args.get(1))))
            {
                event.replyWarning("A code for `" + args.get(1) + "` is not registered.");
                return;
            }

            if(dataManager.removeCode(type, event.getAuthor().getIdLong(), args.get(1)))
                event.replySuccess("Removed the code for `" + args.get(1) + "`");
            else
                event.replyError("Error whilst removing a code! Please contact a developer.");
        }
    }

    private List<String> parseArgs(Pattern pattern, String args)
    {
        Matcher m = pattern.matcher(args);
        List<String> list = new LinkedList<>();

        while(m.find())
        {
            for(int i = 0; i <= m.groupCount(); i++)
            {
                if(!(m.group(i).trim().equals(args)))
                    list.add(m.group(i).trim());
            }
        }

        return list;
    }
}
