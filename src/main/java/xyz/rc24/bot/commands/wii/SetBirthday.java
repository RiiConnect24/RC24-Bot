package xyz.rc24.bot.commands.wii;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.commands.Categories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SetBirthday extends Command
{
    private JedisPool pool;

    public SetBirthday(JedisPool pool)
    {
        this.pool = pool;
        this.name = "birthday";
        this.help = "Sets your birthday.";
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent event)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.select(2);
            // We only want the day and the month. We don't want the year, but
            // for user accessibility we'll leave it optional and never use it.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM[/yyyy]");
            LocalDate dateTime;

            if(event.getArgs().endsWith("/\\d{4}/"))
                dateTime = LocalDate.parse(event.getArgs(), formatter);
            else
                dateTime = LocalDate.parse(event.getArgs()+"/2018", formatter);

            conn.hset("birthdays", event.getAuthor().getId(), dateTime.getMonthValue() + "-" + dateTime.getDayOfMonth());
            event.replySuccess("Updated successfully!");
        }
        catch(DateTimeParseException e)
        {
            event.replyError("I couldn't parse your date.\n" +
            "Try something like: `" + event.getClient().getPrefix() + "birthday 14/04`.");
        }
    }
}
