package xyz.rc24.bot.commands.argument;

@FunctionalInterface
public interface Choice<T> {

	public T[] getChoices(Class<T> type);
	
}
