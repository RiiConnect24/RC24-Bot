package xyz.rc24.bot.commands.argument;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class GlobalLiteralArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

	protected GlobalLiteralArgumentBuilder(String literal) {
		super(literal);
	}
	
	public static <S> GlobalLiteralArgumentBuilder<S> literal(final String name) {
		return new GlobalLiteralArgumentBuilder<>(name);
	}

	@Override
	public GlobalLiteralCommandNode<S> build() {
		final GlobalLiteralCommandNode<S> result = new GlobalLiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());
		
		for(final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		
		return result;
	}
	
	public static final class GlobalLiteralCommandNode<S> extends LiteralCommandNode<S> implements GlobalNode {

		public GlobalLiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
			super(literal, command, requirement, redirect, modifier, forks);
		}
		
	}
	
}
