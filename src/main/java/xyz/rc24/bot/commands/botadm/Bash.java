package xyz.rc24.bot.commands.botadm;

/*
 * Copyright (C) 2017 Spotlight
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs a shell command.
 *
 * @author Spotlight
 */
public class Bash extends Command {
    public Bash() {
        this.name = "bash";
        this.help = "Runs a bash command.";
        this.category = new Category("Admin");
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Cannot execute a empty command!");
            return;
        }

        StringBuilder output = new StringBuilder();
        String finalOutput;
        try {
            ProcessBuilder builder = new ProcessBuilder(event.getArgs().split(" "));
            Process p = builder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String runningLineOutput;
            while ((runningLineOutput = reader.readLine()) != null) {
                output.append(runningLineOutput).append("\n");
            }

            if (output.toString().isEmpty()) {
                event.replySuccess("Done, with no output!");
                return;
            }

            // Remove linebreak
            finalOutput = output.substring(0, output.length() - 1);
            reader.close();
        } catch (IOException e) {
            event.replyError("I wasn't able to find the command `" + event.getArgs() + "`!");
            return;
        } catch (Exception e) {
            SimpleLog.getLog("Bash").warn("An unknown error occurred!");
            e.printStackTrace();
            event.replyError("An unknown error occurred! Check the bot console.");
            return;
        }

        // Actually send
        try {
            event.replySuccess("Input: ```\n" + event.getArgs() + "``` Output: \n```\n" + finalOutput + "```");
        } catch (IllegalArgumentException e) {
            SimpleLog.getLog("Bash").info("Input: " + event.getArgs() + "\nOutput: " + finalOutput);
            event.replySuccess("Command output too long! Output sent in console.");
        }
    }
}