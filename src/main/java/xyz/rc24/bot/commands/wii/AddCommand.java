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

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.utils.FormatUtil;

import java.util.Map;

/**
 * @author Artuto, Gamebuster
 */

public class AddCommand implements Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        BotCore botCore = RiiConnect24Bot.getInstance().getCore();
        GuildSettings guildSettings = botCore.getGuildSettings(event.getGuild().getIdLong());
        CodeType codeType = guildSettings.getDefaultAddType();

        Member member = event.getOption("user").getAsMember();

        if (member.getUser().isBot()) {
            event.reply("You cannot add bots").setEphemeral(true).queue();
            return;
        }

        Map<String, String> authorTypeCodes = botCore.getCodesForType(codeType, event.getUser().getIdLong());

        if (authorTypeCodes.isEmpty()) {
            event.reply(member.getAsMention() + " does not have any friend codes added.").setEphemeral(true).queue();
            return;
        }

        // Send a message to the target
        member.getUser().openPrivateChannel().flatMap(privateChannel -> {
            return privateChannel.sendMessage(event.getMember().getAsMention() + " has requested to add your " + codeType.getDisplayName() + " friend code(s)!\n\n" + FormatUtil.getCodeLayout(authorTypeCodes));
        }).queue();

        Map<String, String> targetTypeCodes = botCore.getCodesForType(codeType, member.getIdLong());

        // Send a message to author
        event.getUser().openPrivateChannel().flatMap(privateChannel -> {
            return privateChannel.sendMessage("You have requested to add " + member.getAsMention() + "s " + codeType.getDisplayName() + " friend code(s).\n\n" + FormatUtil.getCodeLayout(targetTypeCodes));
        }).queue();

    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("add", "Send your friend code to another user.")
                .addOption(OptionType.USER, "user", "User", true);
    }
}
