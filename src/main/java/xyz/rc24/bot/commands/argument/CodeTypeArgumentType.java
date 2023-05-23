package xyz.rc24.bot.commands.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.CommandUtils;
import xyz.rc24.bot.commands.exception.ParseExceptions;
import xyz.rc24.bot.core.entities.CodeType;

public class CodeTypeArgumentType implements ArgumentType<CodeType> {

	public static final CodeTypeArgumentType ANY_CODE = of(CodeType.values());
	public static final CodeTypeArgumentType KNOWN_CODES;
	static {
		List<CodeType> types = new ArrayList<>(Arrays.asList(CodeType.values()));
		types.remove(CodeType.UNKNOWN);
		KNOWN_CODES = of(types.toArray(new CodeType[]{}));
	}
	
	private final LinkedHashSet<CodeType> types = new LinkedHashSet<CodeType>();
	
	private CodeTypeArgumentType(CodeType... types) {
		this.types.addAll(Arrays.asList(types));
	}
	
	public static CodeTypeArgumentType of(CodeType... types) {
		return new CodeTypeArgumentType(types);
	}
	
	@Override
	public <S> CodeType parse(S context, StringReader reader) throws CommandSyntaxException {
		CodeType type = CodeType.fromCode(CommandUtils.readString(reader));
		
		if(types.contains(type)) {
			return type;
		}
		
		if(type == CodeType.UNKNOWN) {
			throw ParseExceptions.NONEXISTANT.create("Code Type", type);
		}
		else {
			throw ParseExceptions.INVALID_CHOICE.create(types);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		String typeText = CommandUtils.lastArgOf(builder.getInput());
		for(CodeType t : types) {
			if(t.getName().toLowerCase().startsWith(typeText.toLowerCase())) {
				builder.suggest(t.getName());
			}
		}
		return builder.buildFuture();
	}
	
}
