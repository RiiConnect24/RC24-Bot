package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class Invite extends Command {
    public Invite() {
        this.name = "invite";
        this.category = new Command.Category("Wii-related");
        this.help = "Invite me to your server?";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Aw, you want to invite me? <3\n" +
                "Invite me here: " + event.getJDA().asBot().getInviteUrl());
    }
}
