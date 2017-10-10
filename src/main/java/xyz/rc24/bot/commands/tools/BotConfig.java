package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.mangers.LogManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotConfig extends Command {
    private LogManager manager;

    public BotConfig(JedisPool pool) {
        this.manager = new LogManager(pool);
        this.children = new Command[]{new ChannelConfig()};
        this.name = "config";
        this.help = "Change important bot settings.";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyError("Please enter a valid option for the command.\n" +
                            "Valid commands are: `setchannel`.");
    }

    private class ChannelConfig extends Command {
        ChannelConfig() {
            this.name = "setchannel";
            this.help = "Changes the channel for the given log.";
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] arguments = event.getArgs().split(" ");
            if (!(arguments.length == 2)) {
                event.replyError("Invalid syntax!\n The correct format is " +
                        "`" + event.getClient().getPrefix() + "config setchannel <type> <#channel-mention/off>`.");
                return;
            }
            // I'm assuming way too much here.
            // TODO: Better checking of arguments?
            String channelType = arguments[0];
            LogManager.LogType type = channelTypes.get(channelType);

            String channelName = arguments[1];
            if (channelName.equals("off")) {
                manager.disableLog(type, event.getGuild().getIdLong());
                event.replySuccess("Channel succesfully removed as a log.");
                return;
            }

            // Set (potentially) obtained channel
            Long channelID = getChannelId(channelName, event.getGuild());

            if (channelID == 0L) {
                event.replyError("I wasn't able to find that channel on this server! No changes have been made to your server's config.");
            } else {
                if (type == null) {
                    event.replyError(Const.getChannelTypes());
                }  else {
                    manager.setLog(event.getGuild().getIdLong(), type, channelID);
                    event.replySuccess("Successfully set!");
                }
            }
        }

        final Map<String, LogManager.LogType> channelTypes = new HashMap<String, LogManager.LogType>() {{
            put("mod", LogManager.LogType.MOD);
            put("mod-log", LogManager.LogType.MOD);

            put("srv", LogManager.LogType.SERVER);
            put("server", LogManager.LogType.SERVER);
            put("server-log", LogManager.LogType.SERVER);
        }};

        private Long getChannelId(String name, Guild currentGuild) {
            List<TextChannel> potentialChannels = FinderUtil.findTextChannels(name, currentGuild);
            if (potentialChannels.isEmpty()) {
                return 0L;
            } else {
                // Grab the first text channel's Long, and return.
                return potentialChannels.get(0).getIdLong();
            }
        }
    }
}
