package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.loader.Config;

public class MailParseCommand extends Command
{
    private final Config config;

    public MailParseCommand(Config config)
    {
        this.config = config;
        this.name = "patch";
        this.help = "Patches your `nwc24msg.cfg` for use with RiiConnect24.";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    public void execute(CommandEvent event)
    {
        if(config.isMailPatchEnabled())
            event.replyInDm("Drag and drop your `nwc24msg.cfg` here, and I'll patch it!", (success) -> event.reactSuccess(),
                    (failure) -> event.replyError("Hey, " + event.getAuthor().getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled."));
        else
            event.replyError("The `patch` command has been disabled by the bot owner!");
    }
}
