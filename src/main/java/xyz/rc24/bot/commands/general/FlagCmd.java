package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.Flag;

public class FlagCmd extends Command
{
    private final Bot bot;

    public FlagCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "flag";
        this.help = "Sets your flag to show in your code lookup.";
        this.aliases = new String[]{"setflag", "setcountry", "country"};
        this.category = Categories.GENERAL;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        long id = event.getAuthor().getIdLong();
        String args = event.getArgs();

        if(args.isEmpty())
        {
            event.replyError("You must provide a country!");
            return;
        }

        Flag flag = Flag.fromName(args);
        if(flag == Flag.UNKNOWN)
        {
            event.replyError("Unknown country!");
            return;
        }

        boolean success = bot.getCodeDataManager().setFlag(id, flag.getEmote());

        if(success)
            event.replySuccess("Updated successfully!");
        else
            event.replyError("Error whilst updating your flag! Please contact a developer.");
    }
}
