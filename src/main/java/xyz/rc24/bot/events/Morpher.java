package xyz.rc24.bot.events;

import com.google.cloud.datastore.Datastore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.loader.Config;
import xyz.rc24.bot.mangers.MorpherManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Mirror messages from one server to another.
 */
public class Morpher extends ListenerAdapter {
    private final Long rootID;
    private final Long mirrorID;
    private final Long ownerID;
    private TextChannel mirror = null;
    private final MorpherManager morpherManager;
    private static final Logger logger = LoggerFactory.getLogger(Morpher.class);

    public Morpher(Config config, Datastore datastore) {
        this.rootID = config.getMorpherRoot();
        this.mirrorID = config.getMorpherMirror();
        this.ownerID = config.getPrimaryOwner();
        // We have to distinguish this set from others.
        String keyName = "morpher:" + rootID + ":" + mirrorID;
        this.morpherManager = new MorpherManager(keyName, datastore);
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
        jda.getUserById(ownerID).openPrivateChannel().queue(pc -> pc.sendMessage(message).complete());
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

    // For use with eval
    // We set a
    @SuppressWarnings("unused")
    public void syncMessages(JDA jda) {
        if (canUseMirror(jda)) {
            TextChannel root = jda.getTextChannelById(rootID);
            // Set current history
            MessageHistory channelHistory = root.getHistory();
            // Grab current 100 messages, and make the list mutable.
            List<Message> history = new ArrayList<>(channelHistory.retrievePast(100).complete());
            List<Message> retrievedHistory;
            do {
                // Get further 100 messages back.
                retrievedHistory = channelHistory.retrievePast(100).complete();
                history.addAll(retrievedHistory);
                logger.info("Downloading another 100 messages for mirroring...");
            } while (retrievedHistory.size() % 100 != 0);
            // The above detects if it evenly fits into 100 or not.
            // If it doesn't, we're done with messages.

            // Since our current history is from most recent message -> last, we need to reverse.
            Collections.reverse(history);
            for (Message toMirror : history) {
                // Time to mirror!
                // Create embed + store in database.
                mirror.sendMessage(createMirrorEmbed(toMirror)).queue(
                        message -> morpherManager.setAssociation(toMirror.getIdLong(), message.getIdLong()
                ));
            }
        }
    }
}
