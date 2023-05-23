package xyz.rc24.bot.commands.argument.suggestion;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.argument.suggestion.builder.RiiSuggestionsBuilder;

public class AnyStringSuggestionProvider<S> implements SuggestionProvider<S> {
	
	private final String name;
	private final boolean greedy;
	
	public AnyStringSuggestionProvider(String name) {
		this(name, false);
	}
	
	public AnyStringSuggestionProvider(String name, boolean greedy) {
		this.name = name;
		this.greedy = greedy;
	}
	
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		int i = context.getNodes().size() - 1 ;
		RiiSuggestionsBuilder b = new RiiSuggestionsBuilder(builder);
		String arg = builder.getRemaining();
		if(builder.getRemaining().isBlank()) {
			b.suggestAnyString(name);
		}
		else {
			if(!greedy) {
				b.suggestAsMatchable(b.getRemaining());
			}
			else {
				//NO-OP
			}
		}

		builder.add(b);
		return builder.buildFuture();
	}

}
