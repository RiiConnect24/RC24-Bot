package xyz.rc24.bot.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.rc24.bot.utils.MorpherManager;

import java.awt.*;

/**
 * Mirror messages from one server to another.
 */
public class Morpher extends ListenerAdapter {
    private Long rootID;
    private Long mirrorID;
    private Long ownerID;
    private String keyName;
    private TextChannel mirror = null;
    private MorpherManager morpherManager;

    public Morpher(Long rootID, Long mirrorID, Long ownerID) {
        this.rootID = rootID;
        this.mirrorID = mirrorID;
        this.ownerID = ownerID;
        // We have to distinguish this set from others.
        this.keyName = "morpher:" + rootID + ":" + mirrorID;
        this.morpherManager = new MorpherManager(keyName);
    }

    private Boolean canUseMirror(JDA jda) {
        if (mirror == null) {
            // Maybe we can set it up
            mirror = jda.getTextChannelById(mirrorID);
        }

        // Double check that I can still talk.
        if (mirror.canTalk()) {
            return true;
        }

        // Well, looks like we can't talk, or something.
        String message = "I couldn't access the Morpher mirror channel... could you please check it out?";
        jda.getUserById(ownerID).openPrivateChannel().queue(pc -> pc.sendMessage(message));
        return false;
    }

    private MessageEmbed createMirrorEmbed(Message rootMessage) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("New announcement!");
        embed.setDescription(rootMessage.getContent());
        embed.setColor(Color.decode("#FFEB3B"));
        User author = rootMessage.getAuthor();
        embed.setFooter("#" + rootMessage.getChannel().getName(), null);
        embed.setAuthor(author.getName(), "https://rc24.xyz", author.getEffectiveAvatarUrl());
        embed.setTimestamp(rootMessage.getCreationTime());
        return embed.build();
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == rootID && canUseMirror(event.getJDA())) {
            // Mirror message, and if successful store it.
            mirror.sendMessage(createMirrorEmbed(event.getMessage())).queue(
                    message -> morpherManager.setAssociation(event.getMessageIdLong(), message.getIdLong()
                    ));
        }
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (event.getMessage().getContent().isEmpty()) return;
        if (event.getChannel().getIdLong() == rootID && canUseMirror(event.getJDA())) {
            Long association = morpherManager.getAssociation(event.getMessageIdLong());
            if (association != null) {
                // Create a new embed, and edit the mirrored message to it.
                mirror.getMessageById(association).queue(
                        mirroredMessage -> mirroredMessage.editMessage(createMirrorEmbed(event.getMessage())).queue()
                );
            }
        }
    }

    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        if (event.getChannel().getIdLong() == rootID && canUseMirror(event.getJDA())) {
            Long association = morpherManager.getAssociation(event.getMessageIdLong());
            if (association != null) {
                // Remove mirrored message.
                mirror.getMessageById(association).complete().delete().queue(
                        success -> morpherManager.removeAssociation(event.getMessageIdLong())
                );
            }
        }
    }

    public void onShutdown(ShutdownEvent event) {
        morpherManager.destroy();
    }
}
