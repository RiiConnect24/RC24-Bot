package xyz.rc24.bot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.rc24.bot.managers.PollManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class PollListener extends ListenerAdapter
{
    private final PollManager manager;
    private final ScheduledExecutorService threadPool;

    public PollListener(PollManager manager)
    {
        this.manager = manager;
        this.threadPool = Executors.newSingleThreadScheduledExecutor();
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
        if(event.getUser().isBot())
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
