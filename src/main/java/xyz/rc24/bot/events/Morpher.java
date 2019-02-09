/*
 * MIT License
 *
 * Copyright (c) 2017-2019 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rc24.bot.events;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.Config;
import xyz.rc24.bot.managers.MorpherManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mirror messages from one server to another.
 *
 * @author Spotlight and Artuto
 */
public class Morpher extends ListenerAdapter
{
    private final Long rootID;
    private final Long mirrorID;
    private final Long ownerID;
    private final MorpherManager morpherManager;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Morpher.class);

    public Morpher(Config config)
    {
        this.rootID = config.getMorpherRoot();
        this.mirrorID = config.getMorpherMirror();
        this.ownerID = config.getPrimaryOwner();
        // We have to distinguish this set from others.
        String keyName = "morpher:" + rootID + ":" + mirrorID;
        this.morpherManager = new MorpherManager(keyName);
    }

    private boolean canUseMirror(JDA jda)
    {
        TextChannel mirror = jda.getTextChannelById(mirrorID);

        // Double check that I can still talk.
        if(mirror.canTalk()) return true;

        // Well, looks like we can't talk, or something.
        String message = "I couldn't access the Morpher mirror channel... could you please check it out?";
        jda.getUserById(ownerID).openPrivateChannel().queue(pc -> pc.sendMessage(message).queue());
        return false;
    }

    private MessageEmbed createMirrorEmbed(Message rootMessage)
    {
        EmbedBuilder embed = new EmbedBuilder();
        List<Message.Attachment> attachments = rootMessage.getAttachments();
        StringBuilder descrp = new StringBuilder(rootMessage.getContentRaw());

        if(attachments.size() == 1 && attachments.get(0).isImage()) embed.setImage(attachments.get(0).getUrl());
        else
            attachments.forEach(att -> descrp.append("\n\n:paperclip: **[").append(att.getFileName()).append("](").append(att.getUrl()).append(")**"));

        embed.setTitle("New announcement!");
        embed.setDescription(descrp);
        embed.setColor(Color.decode("#29B7EB"));
        User author = rootMessage.getAuthor();
        embed.setFooter("#" + rootMessage.getChannel().getName(), null);
        embed.setAuthor(author.getName(), "https://rc24.xyz", author.getEffectiveAvatarUrl());
        embed.setTimestamp(rootMessage.getCreationTime());
        return embed.build();
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getChannel().getIdLong() == rootID && canUseMirror(jda))
        {
            // Mirror message, and if successful store it.
            jda.getTextChannelById(mirrorID).sendMessage(createMirrorEmbed(event.getMessage())).queue(message -> morpherManager.setAssociation(event.getMessageIdLong(), message.getIdLong()));
        }
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getMessage().getContentRaw().isEmpty()) return;
        if(event.getChannel().getIdLong() == rootID && canUseMirror(jda))
        {
            long association = morpherManager.getAssociation(event.getMessageIdLong());
            if(! (association == 0L))
            {
                // Create a new embed, and edit the mirrored message to it.
                jda.getTextChannelById(mirrorID).getMessageById(association).queue(mirroredMessage -> mirroredMessage.editMessage(createMirrorEmbed(event.getMessage())).queue());
            }
        }
    }

    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getChannel().getIdLong() == rootID && canUseMirror(jda))
        {
            long association = morpherManager.getAssociation(event.getMessageIdLong());
            if(! (association == 0L))
            {
                // Remove mirrored message.
                jda.getTextChannelById(mirrorID).getMessageById(association).queue(s ->
                {
                    s.delete().queue(s2 -> morpherManager.removeAssociation(event.getMessageIdLong()));
                });
            }
        }
    }

    // For use with eval
    // We set a
    @SuppressWarnings("unused")
    public void syncMessages(JDA jda)
    {
        if(canUseMirror(jda))
        {
            TextChannel root = jda.getTextChannelById(rootID);
            // Set current history
            MessageHistory channelHistory = root.getHistory();
            // Grab current 100 messages, and make the list mutable.
            List<Message> history = new ArrayList<>(channelHistory.retrievePast(100).complete());
            List<Message> retrievedHistory;
            do
            {
                // Get further 100 messages back.
                retrievedHistory = channelHistory.retrievePast(100).complete();
                history.addAll(retrievedHistory);
                logger.info("Downloading another 100 messages for mirroring...");
            }
            while(! (retrievedHistory.size() % 100 == 0));
            // The above detects if it evenly fits into 100 or not.
            // If it doesn't, we're done with messages.

            // Since our current history is from most recent message -> last, we need to reverse.
            Collections.reverse(history);
            for(Message toMirror : history)
            {
                // Time to mirror!
                // Create embed + store in database.
                jda.getTextChannelById(mirrorID).sendMessage(createMirrorEmbed(toMirror)).queue(message -> morpherManager.setAssociation(toMirror.getIdLong(), message.getIdLong()));
            }
        }
    }
}
