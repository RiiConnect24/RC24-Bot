package xyz.rc24.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.Flag;

import java.util.List;
import java.util.stream.Stream;

public class CommandAutoCompletion extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

        if (event.getName().equals("flag") && event.getSubcommandName().equals("set")) {
            if (event.getFocusedOption().getName().equals("flag")) {
                Flag[] flags = Flag.values();
                List<Command.Choice> choices = Stream.of(flags)
                        .filter(flag -> flag.getName().startsWith(event.getFocusedOption().getValue()))
                        .map(flag -> new Command.Choice(flag.getEmote() + " " + flag.getName(), flag.getName()))
                        .toList();
                event.replyChoices(choices.size() > 25 ? List.of(new Command.Choice("Please be more specific", "no value")) : choices).queue();
            }
        }

        if (event.getName().equals("defaultadd")) {
            if (event.getFocusedOption().getName().equals("type")) {
                CodeType[] codeTypes = CodeType.values();
                List<Command.Choice> choices = Stream.of(codeTypes)
                        .map(codeType -> new Command.Choice(codeType.getDisplayName(), codeType.getName()))
                        .toList();
                event.replyChoices(choices).queue();
            }
        }

    }
}
