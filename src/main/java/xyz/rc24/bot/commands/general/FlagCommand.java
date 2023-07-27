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

package xyz.rc24.bot.commands.general;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;
import xyz.rc24.bot.core.entities.Flag;

public class FlagCommand implements Command {

	@Override
	public void onCommand(SlashCommandInteractionEvent event) {

		switch (event.getSubcommandName()) {

			case "clear" -> {
				boolean success = RiiConnect24Bot.getInstance().getCodeDataManager().setFlag(event.getUser().getIdLong(), null);
				event.reply(success ? "Updated successfully!" : "Error clearing your flag! Please contact a developer.").setEphemeral(true).queue();
			}

			case "set" -> {
				Flag flag = Flag.fromName(event.getOption("flag").getAsString());
				if (flag == null) {
					event.reply("Not a valid country, please try again.").setEphemeral(true).queue();
					return;
				}
				boolean success = RiiConnect24Bot.getInstance().getCodeDataManager().setFlag(event.getUser().getIdLong(), flag.getName());
				event.reply(success ? "Updated successfully!" : "Error clearing your flag! Please contact a developer.").setEphemeral(true).queue();
			}
		}
	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("flag", "Sets the flag in your code lookup")
				.addSubcommands(new SubcommandData("clear", "Clear your flag"))
				.addSubcommands(new SubcommandData("set", "Set your flag")
						.addOption(OptionType.STRING, "flag", "Flag", true, true));
	}

}
