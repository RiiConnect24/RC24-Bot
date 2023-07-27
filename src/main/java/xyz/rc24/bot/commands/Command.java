package xyz.rc24.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {
    void onCommand(SlashCommandInteractionEvent event);
    SlashCommandData getCommandData();
}
