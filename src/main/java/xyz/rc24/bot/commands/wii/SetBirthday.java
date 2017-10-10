package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.commands.Categories;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class SetBirthday extends Command {
    private JedisPool pool;

    public SetBirthday(JedisPool pool) {
        this.pool = pool;
        this.name = "birthday";
        this.help = "Sets your birthday.";
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Jedis conn = pool.getResource()) {
            // We only want the day and the month. We don't want the year, but
            // for user accessibility we'll leave it optional and never use it.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd[/yyyy]");
            LocalDate dateTime = LocalDate.parse(event.getArgs(), formatter);

            String userID = event.getAuthor().getId();

            conn.hset("birthdays", userID, dateTime.getMonthValue() + "-" + dateTime.getDayOfMonth());
            event.replySuccess("Updated successfully!");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            event.replyError("I couldn't parse your date.\n" +
            "Due to a bug that I keep having, I require a year. Please don't give out your full birth year!\n" +
            "Try something like: `" + event.getClient().getPrefix() + "birthday 04/20/1970` or some random year.");
        }
    }
}
