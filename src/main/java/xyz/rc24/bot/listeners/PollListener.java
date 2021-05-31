/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.rc24.bot.managers.PollManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class PollListener extends ListenerAdapter
{
    private final PollManager manager;
    private final ScheduledExecutorService threadPool;

    public PollListener(PollManager manager)
    {
        this.manager = manager;
        this.threadPool = manager.getThreadPool();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event)
    {
        if(!(preCheck(event)))
            return;

        threadPool.submit(() ->
        {
            Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
            MessageEmbed embed = message.getEmbeds().stream().findFirst().orElse(null);
            if(embed == null)
                return;

            MessageEmbed.Field field;
            EmbedBuilder newEmbed;

            switch(event.getReactionEmote().getName())
            {
                case "\uD83C\uDD70":
                {
                    field = embed.getFields().get(0);
                    if(embed.getFields().get(1).getValue().contains(event.getUser().getAsTag()))
                        return;

                    String users = event.getReaction().retrieveUsers()
                            .stream()
                            .filter(user -> !(user.isBot()))
                            .map(User::getAsTag)
                            .collect(Collectors.joining(", "));

                    newEmbed = new EmbedBuilder(embed)
                    {{
                        clearFields();
                        addField(field.getName(), users, false);
                        addField(embed.getFields().get(1));
                    }};
                    break;
                }
                case "\uD83C\uDD71":
                {
                    field = embed.getFields().get(1);
                    if(embed.getFields().get(0).getValue().contains(event.getUser().getAsTag()))
                        return;

                    String users = event.getReaction().retrieveUsers()
                            .stream()
                            .filter(user -> !(user.isBot()))
                            .map(User::getAsTag)
                            .collect(Collectors.joining(", "));

                    newEmbed = new EmbedBuilder(embed)
                    {{
                        clearFields();
                        addField(embed.getFields().get(0));
                        addField(field.getName(), users, false);
                    }};
                    break;
                }
                default:
                    return;
            }

            message.editMessage(newEmbed.build()).queue();
        });
    }

    private boolean preCheck(GenericGuildMessageReactionEvent event)
    {
        // Check if the user is a bot
        if(event.getUser() == null || event.getUser().isBot())
            return false;

        // Check we're tracking that message
        if(!(manager.isTracked(event.getMessageIdLong())))
            return false;

        // Then check the reacted emotes are valid
        MessageReaction.ReactionEmote reaction = event.getReactionEmote();
        if(reaction.isEmote())
            return false;

        String emote = reaction.getName();
        return emote.equals("\uD83C\uDD70") || emote.equals("\uD83C\uDD71");
    }
}
