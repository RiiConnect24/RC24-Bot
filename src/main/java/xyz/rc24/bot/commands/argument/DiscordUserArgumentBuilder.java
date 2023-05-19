package xyz.rc24.bot.commands.argument;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import net.dv8tion.jda.api.entities.User;

public class DiscordUserArgumentBuilder<S> extends ArgumentBuilder<S, DiscordUserArgumentBuilder<S>>{
	
	private final String name;
	private final Predicate<User> searchRequirements;
	
	private DiscordUserArgumentBuilder(final String name, final Predicate<User> searchRequirements) {
		this.name = name;
		this.searchRequirements = searchRequirements;
	}
	
	@Override
	protected DiscordUserArgumentBuilder<S> getThis() {
		return this;
	}

	@Override
	public DiscordUserCommandNode<S> build() {
		DiscordUserCommandNode<S> result = new DiscordUserCommandNode<S>(name, searchRequirements, new DiscordUserArgumentType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), null);
		
		for(final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		
		return result;
	}
	
	public static final class DiscordUserCommandNode<S> extends ArgumentCommandNode<S, User> {

		private final Predicate<User> searchRequirements;
		
		public DiscordUserCommandNode(final String name, final Predicate<User> searchRequirements, final ArgumentType<User> type, final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final RedirectModifier<S> modifier, final boolean forks, final SuggestionProvider<S> customSuggestions) {
			super(name, type, command, requirement, redirect, modifier, forks, customSuggestions);
			this.searchRequirements = searchRequirements;
		}
		
		public Predicate<User> getSearchRequirements() {
			return searchRequirements;
		}
		
		@Override
		public String toString() {
			return "<user " + getName() + ">";
		}
		
	}
}
