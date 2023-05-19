package xyz.rc24.bot.commands.argument;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.CommandUtils;
import xyz.rc24.bot.commands.exception.ParseExceptions;

public class DiscordUserArgumentType implements ArgumentType<User>{
	
	@Override
	public <S> User parse(S context, StringReader reader) throws CommandSyntaxException {
		String s = CommandUtils.readString(reader);
		long id;
			if(s.length() > 3) {
				if(s.startsWith("<@")) {
					id = Long.parseLong(s.substring(2, s.length() - 1));
					return RiiConnect24Bot.getInstance().jda.retrieveUserById(id).complete();
				}
				else {
					id = Long.parseLong(s);
					return RiiConnect24Bot.getInstance().jda.retrieveUserById(id).complete();
				}
			}
		throw ParseExceptions.NONEXISTANT.create("Discord User", s);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		CommandContext<?> c = (CommandContext<?>) context.getSource();
		Guild server = c.getServer();
		System.err.println(CommandUtils.lastArgOf(builder.getInput()));
		if(server != null) {
			server.findMembers((member) -> {
				return (member.getEffectiveName().toLowerCase() + "#" + member.getUser().getDiscriminator()).startsWith(CommandUtils.lastArgOf(builder.getInput().toLowerCase()));
			}).onSuccess((foundMembers) -> {
				for(Member member : foundMembers) {
					builder.suggest(member.getAsMention());
				}
			});
		}
		return builder.buildFuture();
	}
	
}
