package xyz.rc24.bot.listeners;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import com.timgroup.statsd.StatsDClient;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DataDogStatsListener extends ListenerAdapter implements CommandListener
{
    private final StatsDClient statsd;

    public DataDogStatsListener(StatsDClient statsd)
    {
        this.statsd = statsd;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        statsd.recordGaugeValue("guilds", event.getJDA().getGuildCache().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event)
    {
        statsd.recordGaugeValue("guilds", event.getJDA().getGuildCache().size());
    }

    @Override
    public void onCommand(CommandEvent event, Command command)
    {
        statsd.incrementCounter("commands");
    }
}
