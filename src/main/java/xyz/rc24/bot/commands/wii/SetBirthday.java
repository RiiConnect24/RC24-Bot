package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
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
            "Try something like: `" + event.getClient().getPrefix() + "birthday 14/04.");
        }
    }
}
