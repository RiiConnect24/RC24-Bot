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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artuto
 */

public class AddCmd extends SlashCommand
{
    private final BotCore core;

    public AddCmd(Bot bot)
    {
        this.core = bot.getCore();
        this.name = "add";
        this.help = "Sends your friend code to another user.";
        this.category = Categories.WII;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "The user you want to add."));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
            Member member = event.getOption("user").getAsMember();
            if(member == null)
                return;

            GuildSettings gs = getClient().getSettingsFor(event.getGuild());
            CodeType defaultAddType = gs.getDefaultAddType();

            if(member.equals(event.getMember()))
            {
                event.reply("You can't add yourself!").setEphemeral(true).queue();
                return;
            }
            if(member.getUser().isBot())
            {
                event.reply("You can't add bots!").setEphemeral(true).queue();
                return;
            }

            Map<String, String> authorTypeCodes = core.getCodesForType(defaultAddType, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong());
            if(authorTypeCodes.isEmpty())
            {
                event.reply("**" + event.getMember().getEffectiveName() + "** has not added any friend codes!").setEphemeral(true).queue();
                return;
            }

            Map<String, String> targetTypeCodes = core.getCodesForType(defaultAddType, member.getUser().getIdLong());
            if(targetTypeCodes.isEmpty())
            {
                event.reply("**" + member.getEffectiveName() + "** has not added any friend codes!").setEphemeral(true).queue();
                return;
            }

            // Send target's code to author
            event.reply(getAddMessageHeader(defaultAddType, event.getMember(),
                    false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes))
                    .queue(null, (failure) -> event.reply("Hey, " + member.getAsMention() +
                    ": I couldn't DM you. Make sure your DMs are enabled.").setEphemeral(true));

            // Send author's code to target
            member.getUser().openPrivateChannel().queue(pc ->
                    pc.sendMessage(getAddMessageHeader(defaultAddType, event.getMember(),
                            false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes))
                            .queue(null, (failure) -> event.reply("Hey, " + member.getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled.").setEphemeral(true)));
    }

    private String getAddMessageHeader(CodeType type, Member member, boolean isCommandRunner)
    {
        if(!(isCommandRunner))
            return "**" + member.getEffectiveName() + "** has requested to add your " + type.getDisplayName() + " friend code(s)!";
        else
            return "You have requested to add **" + member.getEffectiveName() + "**'s " + type.getDisplayName() + " friend code(s).";
    }
}
