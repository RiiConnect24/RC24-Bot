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

import com.thegamecommunity.discord.command.argument.DiscordUserArgumentType;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;
import xyz.rc24.bot.commands.argument.CodeTypeArgumentType;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.utils.FormatUtil;

import java.awt.Color;
import java.util.Map;

/**
 * @author Gamebuster
 */

public class CodeCmd
{
	private static final Bot BOT = RiiConnect24Bot.getInstance();
	private static final BotCore CORE = RiiConnect24Bot.getInstance().getCore();
	private static final CodeDataManager CODE_MANAGER = BOT.getCodeDataManager();
	
	private static final String HELP = "**__Using the bot__**\n\n" +
			"**Adding Wii:**\n" + "`/code add wii Wii Name Goes here 1234-5678-9012-3456`\n" +
			"**Adding games:**\n `/code add game Game Name 1234-5678-9012`\n" +
				"and many more types! Run `/code add` " +
				"to see all supported code types right now, such as the 3DS, PlayStation 4 and Switch.\n\n" +
			"**Editing codes**\n" + "`/code edit type Name 1234-5678-9012-3456`\n\n" +
			"**Removing codes**\n" + "`/code remove type Name`\n\n" +
			"**Looking up codes**\n" + "`/code lookup @user`\n\n" +
			"**Adding a user's Wii**\n" + "`/add @user`\n" + "This will send you their wii, and then DM them your Wii/game wii.";
	
	@SuppressWarnings("unused")
	public static void register(Dispatcher dispatcher) {
		dispatcher.register(Commands.base("code", "Manages your friend codes.", null).requires((context) -> context.isDiscordContext(), RiiContext.requiresDiscordContext)
			.then(Commands.suggestableString("add")
				.then(Commands.argument("type", CodeTypeArgumentType.KNOWN_CODES)
					.then(Commands.anyString("name")
						.then(Commands.anyStringGreedy("code")
							.executes((context) -> {
								addCode(context.getSource(), context.getArgument("type", CodeType.class),
										context.getArgument("name", String.class), context.getArgument("code", String.class));
								return 1;
							})
						)
					)
				)
			)
			.then(Commands.suggestableString("edit")
				.then(Commands.argument("type", CodeTypeArgumentType.KNOWN_CODES)
					.then(Commands.anyString("name") //TODO: SUGGEST USER'S CODES (create a new KnownCodeArgumentType)
						.then(Commands.anyStringGreedy("newCode")
							.executes((context) -> {
								editCode(context.getSource(), context.getArgument("type", CodeType.class),
										context.getArgument("name", String.class), context.getArgument("newCode", String.class));
								return 1;
							})	
						)	
					)
				)
			)
			.then(Commands.suggestableString("remove")
				.then(Commands.argument("type", CodeTypeArgumentType.KNOWN_CODES)
					.then(Commands.anyString("name") //TODO: SUGGEST USER'S CODES (create a new KnownCodeArgumentType)
						.executes((context) -> {
							deleteCode(context.getSource(), context.getArgument("type", CodeType.class),
									   context.getArgument("name", String.class));
							return 1;
						})	
					)	
				)
			)
			.then(Commands.suggestableString("lookup")
				.executes((context) -> {
					lookupCodes(context.getSource(), context.getSource().getUser());
					return 1;
				})
				.then(Commands.argument("user", new DiscordUserArgumentType())
					.executes((context) -> {
						lookupCodes(context.getSource(), context.getArgument("user", User.class));
						return 1;
					})
				)
			)
			.then(Commands.suggestableString("help")
				.executes((context) -> {
					sendHelp(context.getSource());
					return 1;
				})
			)
		);
	}
	
	@SuppressWarnings("rawtypes")
	private static void addCode(RiiContext context, CodeType type, String name, String code) {
		final long author = context.getUser().getIdLong();
		
		Map<String, String> codeTypes = CORE.getCodesForType(type, author);
		if(codeTypes.containsKey(code)) {
			context.queueMessage("You already added this code!", true, false);
			return;
		}

		if(CODE_MANAGER.addCode(type, context.getUser().getIdLong(), code, name))
			context.queueMessage("Added a code for " + type.getDisplayName() + ". \n\nName:" + name + "\nCode:`" + code + "`", true, false);
		else
			context.queueMessage("Error whilst adding a code! Please contact a developer.", true, false);
	}
	
	@SuppressWarnings("rawtypes")
	private static void editCode(RiiContext context, CodeType type, String name, String newCode) {
		final long author = context.getUser().getIdLong();
		
		Map<String, String> codeTypes = CORE.getCodesForType(type, author);
		if(!(codeTypes.containsKey(name)))
		{
			context.queueMessage("A code for `" + name + "` is not registered.", true, false);
			return;
		}

		if(CODE_MANAGER.editCode(type, author, newCode, name))
			context.queueMessage("Edited the code for `" + name + "`", true, false);
		else
			context.queueMessage("Error whilst editing a code! Please contact a developer.", true, false);
	}

	@SuppressWarnings("rawtypes")
	private static void deleteCode(RiiContext context, CodeType type, String name) {
		final long author = context.getUser().getIdLong();

		Map<String, String> codeTypes = CORE.getCodesForType(type, author);
		if(!(codeTypes.containsKey(name)))
		{
			context.queueMessage("A code for `" + name + "` is not registered.", true, false);
			return;
		}

		if(CODE_MANAGER.removeCode(type, author, name))
			context.queueMessage("Removed the code for `" + name + "`", true, false);
		else
			context.queueMessage("Error whilst removing a code! Please contact a developer.", true, false);
	}
	
	@SuppressWarnings("rawtypes")
	private static void lookupCodes(RiiContext context, User user) {
		String flag = CORE.getFlag(user.getIdLong());
		boolean hasFlag = !(flag.isEmpty());
		
		Member member = null;
		String name = user.getAsTag();
		Color color = Color.GRAY;
		
		if(context.isDiscordContext()) {
			if(context.isGuildContext()) {
				Guild guild = context.getServer();
				member = guild.getMember(user);
			}
		}
		
		EmbedBuilder embed = context.getEmbed();
		
		if(member != null) {
			name = member.getEffectiveName();
			color = member.getColor();
		}

		embed.setAuthor("Profile for " + name,
				null, user.getEffectiveAvatarUrl())
		.setColor(color);
		
		if(hasFlag)
			embed.setTitle("Country: " + flag);

		Map<CodeType, Map<String, String>> userCodes = CORE.getAllCodes(member.getUser().getIdLong());
		for(Map.Entry<CodeType, Map<String, String>> typeData : userCodes.entrySet())
		{
			Map<String, String> codes = typeData.getValue();
			if(!(codes.isEmpty()))
			{
				embed.addField(typeData.getKey().getFormattedName(),
						FormatUtil.getCodeLayout(codes), true);
				if(!context.isDiscordContext()) { //for the console
					context.queueMessage(typeData.getKey().getName() + ": " + FormatUtil.getCodeLayout(codes).replace("`", ""));
				}
			}
		}
		if(embed.getFields().isEmpty())
			context.queueMessage("**" + member.getEffectiveName() + "** has not added any codes!", true, false);
		else if (context.isDiscordContext()) //do not try to send embed to the console
			context.queueMessage(MessageCreateData.fromEmbeds(embed.build()));
	}
	
	@SuppressWarnings("rawtypes")
	private static void sendHelp(RiiContext context) {
		context.queueMessage(HELP, true, false);
	}
	
}
