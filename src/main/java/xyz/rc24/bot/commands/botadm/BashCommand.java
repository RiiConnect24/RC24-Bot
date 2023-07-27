/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.commands.botadm;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs a shell command.
 *
 * @author Spotlight
 */
public class BashCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(BashCommand.class);

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        String bashCommand = event.getOption("command").getAsString();

        if (bashCommand.isEmpty()) {
            event.reply("Command cannot be empty!").setEphemeral(true).queue();
            return;
        }

        StringBuilder output = new StringBuilder();
        String finalOutput;

        try {

            ProcessBuilder builder = new ProcessBuilder(bashCommand.split(" "));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String runningLineOutput;

            while (!((runningLineOutput = reader.readLine()) == null)) {
                output.append(runningLineOutput).append("\n");
            }

            if (output.toString().isEmpty()) {
                event.reply("Executed command without output!").queue();
                return;
            }

            // Remove linebreak
            finalOutput = output.substring(0, output.length() - 1);
            reader.close();

        } catch (IOException e) {
            event.reply("I wasn't able to find the command `" + bashCommand + "`!").setEphemeral(true).queue();
            return;
        } catch (Exception e) {
            logger.error("An error occurred", e);
            event.replyFormat("An error occurred: %s - Check the bot console.", e.getMessage()).setEphemeral(true).queue();
            return;
        }

        event.replyFormat("Input:\n```%s```\nOutput:\n```%s```", bashCommand, finalOutput).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("bash", "Executes a bash command")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .addOption(OptionType.STRING, "command", "Bash command to run", true);
    }
}