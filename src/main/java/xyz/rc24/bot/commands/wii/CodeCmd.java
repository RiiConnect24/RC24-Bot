/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.exceptions.PermissionException;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Artuto
 */

public class CodeCmd extends SlashCommand
{
    private final Bot bot;
    private final CodeDataManager dataManager;
    private final EventWaiter waiter;

    private final Pattern FULL_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)\\s+((?:\\d{4}|SW)[-\\s]\\d{4}[-\\s]\\d{4}(?:[-\\s]\\d{4})?|\\w+)$", Pattern.MULTILINE); // thanks Dismissed
    private final Pattern REMOVE_PATTERN = Pattern.compile("(\\w+)\\s+(.+?)$", Pattern.MULTILINE);

    public CodeCmd(Bot bot)
    {
        this.bot = bot;
        this.dataManager = bot.getCodeDataManager();
        this.waiter = bot.waiter;
        this.name = "code";
        this.help = "Manages friend codes for the user.";
        this.category = Categories.WII;
        this.guildOnly = false;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "cmd", "A valid subcommand (add, edit, remove, lookup, help).").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "type", "A platform type."));
        data.add(new OptionData(OptionType.STRING, "name", "A name for the code."));
        data.add(new OptionData(OptionType.STRING, "code", "A friend code."));
        data.add(new OptionData(OptionType.USER, "user", "A user."));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        System.out.println(event.getOption("cmd").getAsString());
        if (event.getOption("cmd").getAsString().equals("add"))
        {
            GuildSettings gs = getClient().getSettingsFor(event.getGuild());

            CodeType type = CodeType.fromCode(event.getOption("type").getAsString());
            if(type == CodeType.UNKNOWN)
            {
                event.reply(FormatUtil.getCodeTypes()).setEphemeral(true).queue();
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong());
            if(codeTypes.containsKey(event.getOption("code").getAsString()))
            {
                event.reply("You already added this code!").setEphemeral(true).queue();
                return;
            }

            if(dataManager.addCode(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong(), event.getOption("code").getAsString(), event.getOption("name").getAsString()))
                event.reply("Added a code for `" + event.getOption("name").getAsString() + "`").setEphemeral(true).queue();
            else
                event.reply("Error whilst adding a code! Please contact a developer.").setEphemeral(true).queue();
        }
        else if (event.getOption("cmd").getAsString().equals("edit"))
        {
            GuildSettings gs = getClient().getSettingsFor(event.getGuild());

            CodeType type = CodeType.fromCode(event.getOption("type").getAsString());
            if(type == CodeType.UNKNOWN)
            {
                event.reply(FormatUtil.getCodeTypes()).setEphemeral(true).queue();
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong());
            if(!(codeTypes.containsKey(event.getOption("name").getAsString())))
            {
                event.reply("A code for `" + event.getOption("name").getAsString() + "` is not registered.").setEphemeral(true).queue();
                return;
            }

            if(dataManager.editCode(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong(), event.getOption("code").getAsString(), event.getOption("name").getAsString()))
                event.reply("Edited the code for `" + event.getOption("name").getAsString() + "`").setEphemeral(true).queue();
            else
                event.reply("Error whilst editing a code! Please contact a developer.").setEphemeral(true).queue();
        }
        else if (event.getOption("cmd").getAsString().equals("remove"))
        {
            GuildSettings gs = getClient().getSettingsFor(event.getGuild());

            CodeType type = CodeType.fromCode(event.getOption("code").getAsString());
            if(type == CodeType.UNKNOWN)
            {
                event.reply(FormatUtil.getCodeTypes()).setEphemeral(true).queue();
                return;
            }

            Map<String, String> codeTypes = bot.getCore().getCodesForType(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong());
            if(!(codeTypes.containsKey(event.getOption("name").getAsString())))
            {
                event.reply("A code for `" + event.getOption("name").getAsString() + "` is not registered.").setEphemeral(true).queue();
                return;
            }

            if(dataManager.removeCode(type, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong(), event.getOption("name").getAsString()))
                event.reply("Removed the code for `" + event.getOption("name").getAsString() + "`").setEphemeral(true).queue();
            else
                event.reply("Error whilst removing a code! Please contact a developer.").setEphemeral(true).queue();
        }
        else if (event.getOption("cmd").getAsString().equals("lookup"))
        {
            Member member = event.getOption("user").getAsMember();
            if(member == null)
                return;

            String flag = bot.getCore().getFlag(member.getUser().getIdLong());
            boolean hasFlag = !(flag.isEmpty());
            EmbedBuilder codeEmbed = new EmbedBuilder()
                    .setAuthor("Profile for " + member.getEffectiveName(),
                            null, member.getUser().getEffectiveAvatarUrl())
                    .setColor(member.getColor());

            if(hasFlag)
                codeEmbed.setTitle("Country: " + flag);

            Map<CodeType, Map<String, String>> userCodes = bot.getCore().getAllCodes(member.getUser().getIdLong());
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
                event.reply("**" + member.getEffectiveName() + "** has not added any codes!").setEphemeral(true).queue();
            else
                event.replyEmbeds(codeEmbed.build()).queue();
        }
        else if (event.getOption("cmd").getAsString().equals("help"))
        {
            String help = "**__Using the bot__**\n\n" +
                            "**Adding Wii:**\n" + "`/code add wii Wii Name Goes here 1234-5678-9012-3456`\n" +
                            "**Adding games:**\n `/code add game Game Name 1234-5678-9012`\n" +
                        "and many more types! Run `/code add` " +
                            "to see all supported code types right now, such as the 3DS, PlayStation 4 and Switch.\n\n" +
                            "**Editing codes**\n" + "`/code edit type Name 1234-5678-9012-3456`\n\n" +
                            "**Removing codes**\n" + "`/code remove type Name`\n\n" +
                            "**Looking up codes**\n" + "`/code lookup @user`\n\n" +
                            "**Adding a user's Wii**\n" + "`/add @user`\n" + "This will send you their wii, and then DM them your Wii/game wii.";


            event.reply(help).queue();
        }
        else {
            event.reply("Please enter a valid option for the command.\n" +
                "Valid subcommands: `add`, `edit`, `remove`, `lookup`, `help`.").setEphemeral(true).queue();
        }
    }
}
