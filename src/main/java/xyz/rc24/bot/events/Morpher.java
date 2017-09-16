package xyz.rc24.bot.events;

import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Mirror messages from one server to another.
 */
public class Morpher extends ListenerAdapter {
    private Long root;
    private Long mirror;
    public Morpher(Long root, long mirror) {
        this.root = root;
        this.mirror = mirror;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {}
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {}
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {}
}
