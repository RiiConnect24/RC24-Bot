package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.rc24.bot.commands.Categories;

public class Invite extends Command
{
    public Invite()
    {
        this.name = "invite";
        this.category = Categories.TOOLS;
        this.help = "Invite me to your server?";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reply("Aw, you want to invite me? <3\n" +
                "Invite me here: " + event.getJDA().asBot().getInviteUrl());
    }
}
