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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.Categories;

/**
 * @author Spotlight
 */

public class Shutdown extends Command
{
    public Shutdown()
    {
        this.name = "shutdown";
        this.help = "Turns the bot off.";
        this.category = Categories.ADMIN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.getTextChannel().sendMessage("Done! Cya \uD83D\uDC4B").complete();
        event.getJDA().shutdown();
        System.exit(0);
    }
}
