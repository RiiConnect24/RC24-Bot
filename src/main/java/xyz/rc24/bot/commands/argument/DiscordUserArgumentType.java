package xyz.rc24.bot.commands.argument;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.concurrent.Task;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.CommandUtils;
import xyz.rc24.bot.commands.exception.ParseExceptions;

public class DiscordUserArgumentType implements ArgumentType<User>{
	
	@Override
	public <S> User parse(S context, StringReader reader) throws CommandSyntaxException {
		int beginIndex = reader.getCursor();
		User user = getUser(reader);
		int endIndex = reader.getCursor();
		if(user != null) {
			return user;
		}
		reader.setCursor(beginIndex);
		int amount = endIndex - beginIndex;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < amount; i++) {
			sb.append(reader.read());
		}
		throw ParseExceptions.DISCORD_NOT_FOUND.create(sb);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		CommandContext<?> c = (CommandContext<?>) context.getSource();
		Guild server = c.getServer();
		System.err.println(CommandUtils.lastArgOf(builder.getInput()));
		
		Thread searchThread = new Thread(() -> {
			Thread t = Thread.currentThread();
			synchronized(t) {

				server.findMembers((member) -> {
					return (
							(member.getIdLong() + "").startsWith(CommandUtils.lastArgOf(builder.getInput())) ||
							(member.getEffectiveName().toLowerCase() + "#" + member.getUser().getDiscriminator()).startsWith(CommandUtils.lastArgOf(builder.getInput().toLowerCase().replace("@", "")))
					);
				}).onSuccess((foundMembers) -> {
					for(Member member : foundMembers) {
						builder.suggest(toSuggestionString(member.getUser()));
						System.out.println("Found " + member);
					}
					synchronized(t) {
						t.notifyAll();
					}
				});
				try {
					t.wait(1500);
				} catch (InterruptedException e) {}
				
			}
		});
		
		searchThread.start();
		
		try {
			searchThread.join(1500);
		} catch (InterruptedException e) {}
		
		System.out.println("Suggestions: ");
		for(Suggestion suggestion : builder.build().getList()) {
			System.out.println(suggestion.getText());
		}
		
		return builder.buildFuture();
	}
	
	@Nullable
	public User getUser(StringReader s) {
		long id;
		int cursor = s.getCursor();
		StringBuilder sb = new StringBuilder();
		if(s.canRead(2)) {
			sb.append(s.read());
			sb.append(s.read());
			if(sb.toString().equals("<@")) {
				while(s.canRead()) {
					char c = s.read();
					sb.append(c);
					if(c == '>') {
						try {
							String userString = sb.toString();
							id = Long.parseLong(userString.substring(StringUtils.lastIndexOf(userString, '(') + 1, userString.length() - 2));
							return RiiConnect24Bot.getInstance().getJDA().retrieveUserById(id).complete();
						}
						catch(ErrorResponseException | NumberFormatException e) {
							
						}
					}
				}
			}
		}
		return null;
	}
	
	
	private static String toSuggestionString(User user) {
		return "<@" + user.getName() + "#" + user.getDiscriminator() + ":(" + user.getIdLong() + ")>";
	}
	
	private static final class Lock {
		private boolean locked = false;
		private Lock() {};
		
		private void lock() {
			this.locked = true;
		}
		
		private void unlock() {
			this.locked = false;
		}
		
		private boolean isLocked() {
			return locked;
		}
	}
	
}