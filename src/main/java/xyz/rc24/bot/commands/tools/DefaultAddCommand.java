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

package xyz.rc24.bot.commands.tools;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;
import xyz.rc24.bot.core.entities.CodeType;

/**
 * @author Artuto, Gamebuster
 */
public class DefaultAddCommand implements Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        Bot bot = RiiConnect24Bot.getInstance();
        CodeType newDefaultCodeType = CodeType.fromCode(event.getOption("type").getAsString());

        if (bot.getGuildSettingsDataManager().setDefaultAddType(newDefaultCodeType, event.getGuild().getIdLong())) {
            event.reply("Successfully set `" + newDefaultCodeType.getName() + "` as default `add` type!").queue();
        } else {
            event.reply("Error whilst updating the default add type! Please contact a developer.").setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("defaultadd", "Set the default type for the add command")
                .addOption(OptionType.STRING, "type", "Type used for new default code", true, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER));
    }
}
