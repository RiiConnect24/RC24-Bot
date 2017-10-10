package xyz.rc24.bot.commands.botadm;

import com.google.gson.Gson;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.mangers.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MassMessage extends Command {
    private JedisPool pool;

    public MassMessage(JedisPool pool) {
        this.pool = pool;
        this.name = "super_secret_server_message";
        this.help = "Sends a message to _every_ log on the bot. USE WITH CAUTION!";
        this.category = Categories.ADMIN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Jedis conn = pool.getResource()) {
            Map<String, String> logConfigs = conn.hgetAll("logs");
            List<TextChannel> serverList = new ArrayList<>();
            Gson gson = new Gson();

            // For every channel we have:
            // If we can speak, add it to the growing total.
            for (String serverJson : logConfigs.values()) {
                LogManager.StorageFormat format = gson.fromJson(serverJson, LogManager.StorageFormat.class);
                if (format.serverLog != null) {
                    TextChannel serverChannel = event.getJDA().getTextChannelById(format.serverLog);
                    try {
                        if (serverChannel.canTalk()) {
                            serverList.add(serverChannel);
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
                if (format.modLog != null) {
                    TextChannel modChannel = event.getJDA().getTextChannelById(format.modLog);
                    try {
                        if (modChannel.canTalk()) {
                            serverList.add(modChannel);
                        }
                    } catch (NullPointerException ignored) {

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
