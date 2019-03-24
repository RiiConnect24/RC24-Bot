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
import xyz.rc24.bot.Config;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.database.MorpherDataManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mirror messages from one server to another.
 *
 * @author Artuto
 */

public class Morpher extends ListenerAdapter
{
    // IDs
    private final long rootId;
    private final long mirrorId;
    private final long ownerId;

    private final MorpherDataManager dataManager;
    private final Logger logger = RiiConnect24Bot.getLogger(Morpher.class);

    public Morpher(Config config, MorpherDataManager dataManager)
    {
        this.rootId = config.getMorpherRoot();
        this.mirrorId = config.getMorpherMirror();
        this.ownerId = config.getPrimaryOwner();

        this.dataManager = dataManager;
    }

    private boolean canUseMirror(JDA jda)
    {
        TextChannel mirror = jda.getTextChannelById(mirrorId);

        // Double check that I can still talk.
        if(!(mirror == null) && mirror.canTalk())
            return true;

        // Well, looks like we can't talk, or something.
        String message = "I couldn't access the Morpher mirror channel... could you please check it out?";
        jda.getUserById(ownerId).openPrivateChannel().queue(pc -> pc.sendMessage(message).queue(null,
                e -> logger.error("I can't access the Morpher mirror channel!")));
        return false;
    }

    private MessageEmbed createMirrorEmbed(Message rootMessage)
    {
        EmbedBuilder embed = new EmbedBuilder();
        List<Message.Attachment> attachments = new ArrayList<>(rootMessage.getAttachments());
        StringBuilder description = new StringBuilder(rootMessage.getContentRaw());
        StringBuilder attachmentsString = new StringBuilder();
        User author = rootMessage.getAuthor();

        if(attachments.size() == 1)
        {
            Message.Attachment image = attachments.get(0);
            embed.setImage(image.isImage() ? image.getUrl() : null);
        }
        else
        {
            Message.Attachment image = attachments.stream().filter(Message.Attachment::isImage).findFirst().orElse(null);
            if(!(image == null))
            {
                attachments.remove(image);
                embed.setImage(image.getUrl());
            }

            for(Message.Attachment a : attachments)
                attachmentsString.append("**[").append(a.getFileName()).append("](").append(a.getUrl()).append(")**\n");
        }

        // Set meta
        embed.setTitle("New announcement!");
        embed.setColor(Color.decode("#29B7EB"));
        embed.setFooter("#" + rootMessage.getChannel().getName(), null);
        embed.setAuthor(author.getName(), "https://rc24.xyz", author.getEffectiveAvatarUrl());

        // Set actual content
        embed.setDescription(description);
        embed.addField("\uD83D\uDCCE Attachments:", attachmentsString.toString(), false);
        embed.setTimestamp(rootMessage.getCreationTime());

        return embed.build();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getChannel().getIdLong() == rootId && canUseMirror(jda))
        {
            // Mirror message, and if successful store it.
            jda.getTextChannelById(mirrorId).sendMessage(createMirrorEmbed(event.getMessage()))
                    .queue(message -> dataManager.setAssociation(event.getMessageIdLong(), message.getIdLong()));
        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getMessage().getContentRaw().isEmpty())
            return;
        if(event.getChannel().getIdLong() == rootId && canUseMirror(jda))
        {
            long association = dataManager.getAssociation(event.getMessageIdLong());
            if(!(association == 0L))
            {
                // Create a new embed, and edit the mirrored message to it.
                jda.getTextChannelById(mirrorId).getMessageById(association)
                        .queue(mirroredMessage -> mirroredMessage.editMessage(createMirrorEmbed(event.getMessage())).queue());
            }
        }
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        JDA jda = event.getJDA();
        if(event.getChannel().getIdLong() == rootId && canUseMirror(jda))
        {
            long association = dataManager.getAssociation(event.getMessageIdLong());
            if(!(association == 0L))
            {
                // Remove mirrored message.
                jda.getTextChannelById(mirrorId).deleteMessageById(association)
                        .queue(s -> dataManager.removeAssociation(event.getMessageIdLong()));
            }
        }
    }

    @Deprecated
    public void syncMessages(JDA jda)
    {
        if(canUseMirror(jda))
        {
            TextChannel root = jda.getTextChannelById(rootId);
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
                jda.getTextChannelById(mirrorId).sendMessage(createMirrorEmbed(toMirror))
                        .queue(message -> dataManager.setAssociation(toMirror.getIdLong(), message.getIdLong()));
            }
        }
    }
}
