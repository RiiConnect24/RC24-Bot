package xyz.rc24.bot.commands.argument;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class GlobalContextArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

	protected GlobalContextArgumentBuilder(String literal) {
		super(literal);
	}
	
	public static <S> GlobalContextArgumentBuilder<S> literal(final String name) {
		return new GlobalContextArgumentBuilder<>(name);
	}
	
	@Override
	public GlobalContextCommandNode<S> build() {
		final GlobalContextCommandNode<S> result = new GlobalContextCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());
		
		for(final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		
		return result;
	}

	public static final class GlobalContextCommandNode<S> extends LiteralCommandNode<S> implements GlobalNode {

		public GlobalContextCommandNode(String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
			super(literal, command, requirement, redirect, modifier, forks);
		}
		
	}
	
}
