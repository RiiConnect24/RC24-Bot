package xyz.rc24.bot.commands.botadm;

import com.google.gson.Gson;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.mangers.LogManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MassMessage extends Command {
    private JedisPool pool;

    public MassMessage(JedisPool pool) {
        this.pool = pool;
        this.name = "super_secret_server_message";
        this.help = "Sends a message to _every_ log on the bot. USE WITH CAUTION!";
        this.category = new Category("Admin");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Jedis conn = pool.getResource()) {
            Map<String, String> logConfigs = conn.hgetAll("logs");
            List<TextChannel> serverList = Collections.emptyList();
            Gson gson = new Gson();

            // For every channel we have:
            // If we can speak, add it to the growing total.
            for (String serverJson : logConfigs.values()) {
                LogManager.StorageFormat format = gson.fromJson(serverJson, LogManager.StorageFormat.class);
                if (format.serverLog != null) {
                    TextChannel serverChannel = event.getJDA().getTextChannelById(format.serverLog);
                    if (serverChannel.canTalk()) {
                        serverList.add(serverChannel);
                    }
                }
                if (format.modLog != null) {
                    TextChannel serverChannel = event.getJDA().getTextChannelById(format.serverLog);
                    if (serverChannel.canTalk()) {
                        serverList.add(serverChannel);
                    }
                }
            }

            // Actually send
            for (TextChannel logChannel : serverList) {
                logChannel.sendMessage(event.getArgs()).complete();
            }
        }
    }
}
