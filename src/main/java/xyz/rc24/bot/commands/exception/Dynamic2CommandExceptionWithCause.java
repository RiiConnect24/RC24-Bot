package xyz.rc24.bot.commands.exception;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class Dynamic2CommandExceptionWithCause implements CommandExceptionType {

    private final Function function;

    public Dynamic2CommandExceptionWithCause(final Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object a, final Object b, final Throwable cause) {
        return new CommandSyntaxException(this, function.apply(a, b, cause));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object a, final Object b, final Throwable cause) {
        return new CommandSyntaxException(this, function.apply(a, b, cause), reader.getString(), reader.getCursor());
    }

    public interface Function {
        Message apply(Object a, Object b, Throwable cause);
    }
	
}
