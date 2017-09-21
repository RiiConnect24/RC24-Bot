package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class SetBirthday extends Command {
    private JedisPool pool;

    public SetBirthday(JedisPool pool) {
        this.pool = pool;
        this.name = "birthday";
        this.help = "Sets your birthday.";
        this.category = new Command.Category("Admins");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Jedis conn = pool.getResource()) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("dd/MM")
                    .appendPattern("dd/MM/yy")
                    .appendPattern("dd/MM/yyyy")
                    .toFormatter();

            LocalDateTime testTime = LocalDateTime.parse(event.getArgs(), formatter);
            String userID = event.getAuthor().getId();

            conn.hset("birthdays", userID, testTime.getMonth() + " " + testTime.getDayOfMonth());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            event.replyError("I couldn't parse your date. Try something like 4/20.");
        }
    }
}
