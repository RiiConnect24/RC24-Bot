package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import xyz.rc24.bot.commands.Categories;

public class MailParseCommand extends Command {
    public MailParseCommand() {
        this.name = "patch";
        this.help = "Patches your `nwc24msg.cfg` for use with RiiConnect24.";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    public void execute(CommandEvent event) {
        event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage("Drag and drop your `nwc24msg.cfg` here, and I'll patch it!").queue(
                (success) -> event.reactSuccess(),
                (failure) -> event.replyError("Hey, " + event.getMember().getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled.")
        ));
    }
}
