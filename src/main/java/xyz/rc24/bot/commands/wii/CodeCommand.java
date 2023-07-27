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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.Flag;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.utils.FormatUtil;

import java.util.Map;

/**
 * @author Gamebuster
 */

public class CodeCommand implements Command {

	private static final Bot BOT = RiiConnect24Bot.getInstance();
	private static final BotCore CORE = RiiConnect24Bot.getInstance().getCore();
	private static final CodeDataManager CODE_MANAGER = BOT.getCodeDataManager();

	@Override
	public void onCommand(SlashCommandInteractionEvent event) {

		String subcommandGroup = event.getSubcommandGroup();

		if (subcommandGroup == null) {
			if (event.getSubcommandName().equals("lookup")) {

				User user = event.getOption("user").getAsUser();
				Flag flag = CORE.getFlag(user.getIdLong());
				boolean hasFlag = flag != null;
				String name = user.getEffectiveName();

				EmbedBuilder embed = new EmbedBuilder();
				embed.setAuthor("Profile for " + name, null, user.getEffectiveAvatarUrl());

				if (hasFlag) embed.setTitle("Country: " + flag);

				Map<CodeType, Map<String, String>> userCodes = CORE.getAllCodes(user.getIdLong());
				for (Map.Entry<CodeType, Map<String, String>> typeData : userCodes.entrySet()) {
					Map<String, String> codes = typeData.getValue();
					if (!(codes.isEmpty())) {
						embed.addField(typeData.getKey().getFormattedName(), FormatUtil.getCodeLayout(codes), true);
					}
				}
				if (embed.getFields().isEmpty()) {
					event.reply("**" + name + "** has not added any codes!").setEphemeral(true).queue();
					return;
				}

				event.replyEmbeds(embed.build()).setEphemeral(true).queue();

			}
			return;
		}

		switch (subcommandGroup) {

			case "add" -> {

				String subcommand = event.getSubcommandName();
				CodeType codeType = CodeType.fromCode(subcommand);
				long authorId = event.getMember().getIdLong();
				Map<String, String> codeTypes = CORE.getCodesForType(codeType, authorId);
				String code = event.getOption("code").getAsString();

				if (codeTypes.containsKey(code)) {
					event.reply("You already added this code!").setEphemeral(true).queue();
					return;
				}

				String name = event.getOption("name").getAsString();

				if (CODE_MANAGER.addCode(codeType, authorId, code, name)) {
					event.replyFormat("Added a code for %s. \n\nName:%s\nCode:`%s`", codeType.getDisplayName(), name, code).queue();
				} else {
					event.reply("Error whilst adding a code! Please contact a developer.").setEphemeral(true).queue();
				}

			}
			case "edit" -> {

				String subcommand = event.getSubcommandName();
				CodeType codeType = CodeType.fromCode(subcommand);
				long authorId = event.getMember().getIdLong();
				Map<String, String> codeTypes = CORE.getCodesForType(codeType, authorId);
				String code = event.getOption("code").getAsString();
				String name = event.getOption("name").getAsString();

				if (!(codeTypes.containsKey(name))) {
					event.reply("A code for `" + name + "` is not registered.").setEphemeral(true).queue();
					return;
				}

				if (CODE_MANAGER.editCode(codeType, authorId, code, name)) {
					event.reply("Edited the code for `" + name + "`").setEphemeral(true).queue();
				} else {
					event.reply("Error whilst editing a code! Please contact a developer.").setEphemeral(true).queue();
				}

			}

			case "remove" -> {

				String subcommand = event.getSubcommandName();
				CodeType codeType = CodeType.fromCode(subcommand);
				long authorId = event.getMember().getIdLong();
				Map<String, String> codeTypes = CORE.getCodesForType(codeType, authorId);
				String name = event.getOption("name").getAsString();

				if (!(codeTypes.containsKey(name))) {
					event.reply("A code for `" + name + "` is not registered.").setEphemeral(true).queue();
					return;
				}

				if (CODE_MANAGER.removeCode(codeType, authorId, name)) {
					event.reply("Removed the code for `" + name + "`").setEphemeral(true).queue();
				} else {
					event.reply("Error whilst removing a code! Please contact a developer.").setEphemeral(true).queue();
				}

			}
		}
	}

	@Override
	public SlashCommandData getCommandData() {

		SubcommandGroupData subcommandGroupAdd = new SubcommandGroupData("add", "Add a code");
		SubcommandGroupData subcommandGroupEdit = new SubcommandGroupData("edit", "Edit a code");

		for (CodeType codeType : CodeType.values()) {
			if (codeType != null) {
				subcommandGroupAdd.addSubcommands(new SubcommandData(codeType.getName(), codeType.getDisplayName())
						.addOption(OptionType.STRING, "name", "Name of the " + codeType.getDisplayName(), true)
						.addOption(OptionType.STRING, "code", "Code of the " + codeType.getDisplayName(), true));
				subcommandGroupEdit.addSubcommands(new SubcommandData(codeType.getName(), codeType.getDisplayName())
						.addOption(OptionType.STRING, "name", "Name of the " + codeType.getDisplayName(), true)
						.addOption(OptionType.STRING, "code", "Code of the " + codeType.getDisplayName(), true));
			}
		}

		SubcommandGroupData subcommandGroupRemove = new SubcommandGroupData("remove", "Remove a code");
		for (CodeType codeType : CodeType.values()) {
			if (codeType != null) {
				subcommandGroupRemove.addSubcommands(new SubcommandData(codeType.getName(), codeType.getDisplayName())
						.addOption(OptionType.STRING, "name", "Name of the " + codeType.getDisplayName(), true));
			}
		}

		SubcommandData subcommandLookup = new SubcommandData("lookup", "Look up an user's codes")
				.addOption(OptionType.USER, "user", "User", true);

		return Commands.slash("code", "Manages your friend codes").addSubcommandGroups(subcommandGroupAdd, subcommandGroupRemove, subcommandGroupEdit).addSubcommands(subcommandLookup);
	}

}
