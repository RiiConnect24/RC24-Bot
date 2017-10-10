package xyz.rc24.bot.commands.tools;

import com.google.cloud.datastore.Datastore;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.mangers.LogManager;

import java.util.List;

public class BotConfig extends Command {
    private LogManager manager;

    public BotConfig(LogManager manager) {
        this.manager = manager;
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
            LogManager.LogType type = Const.channelTypes.get(channelType);

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
                } else {
                    manager.setLog(event.getGuild().getIdLong(), type, channelID);
                    event.replySuccess("Successfully set!");
                }
            }
        }


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
