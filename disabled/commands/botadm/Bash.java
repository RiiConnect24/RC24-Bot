/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
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

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.commands.Categories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs a shell command.
 *
 * @author Spotlight
 */
public class Bash extends Command
{
    private static Logger logger = (Logger) LoggerFactory.getLogger(Bash.class);

    public Bash()
    {
        this.name = "bash";
        this.help = "Runs a bash command.";
        this.category = Categories.ADMIN;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Cannot execute a empty command!");
            return;
        }

        StringBuilder output = new StringBuilder();
        String finalOutput;
        try
        {
            ProcessBuilder builder = new ProcessBuilder(event.getArgs().split(" "));
            Process p = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String runningLineOutput;
            while(!((runningLineOutput = reader.readLine()) == null))
                output.append(runningLineOutput).append("\n");

            if(output.toString().isEmpty())
            {
                event.replySuccess("Done, with no output!");
                return;
            }

            // Remove linebreak
            finalOutput = output.substring(0, output.length() - 1);
            reader.close();
        }
        catch(IOException e)
        {
            event.replyError("I wasn't able to find the command `" + event.getArgs() + "`!");
            return;
        }
        catch(Exception e)
        {
            logger.warn("An unknown error occurred!");
            e.printStackTrace();
            event.replyError("An unknown error occurred! Check the bot console.");
            return;
        }

        event.replySuccess("Input: ```\n" + event.getArgs() + "``` Output: \n```\n" + finalOutput + "```");
    }
}