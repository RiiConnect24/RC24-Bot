package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.Poll;
import xyz.rc24.bot.managers.PollManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ReviveCmd extends Command
{
    private final PollManager manager;
    private final Set<Long> current;

    public ReviveCmd(PollManager manager)
    {
        this.name = "revive";
        this.help = "Revives the chat by sending a EVC poll for users to vote in.";
        this.category = Categories.GENERAL;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.manager = manager;
        this.current = new HashSet<>();

        EventWaiter waiter = new EventWaiter();
        waiter.waitForEvent(GenericGuildMessageReactionEvent.class, this::checkCondition, this::runAction);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        // Get a random poll
        Poll poll = manager.getRandomPoll();

        // Now we need to build the embed
        EmbedBuilder embed = new EmbedBuilder()
        {{
            setTitle("<:EverybodyVotesChannel:317090360449040388> " + poll.getQuestion());
            setDescription("\uD83C\uDD70 " + poll.getResponse1() + "\n" +
                    "_ _\n" + // Line separator
                    "\uD83C\uDD71 " + poll.getResponse2());
            addField("Users who reacted  \uD83C\uDD70:", "", false);
            addField("Users who reacted \uD83C\uDD71:", "", false);
        }};

        // Send embed to chat
        event.reply(embed.build(), s ->
        {
            // Add message ID to tracked list
            current.add(s.getIdLong());

            // Add reactions
            s.addReaction("\uD83C\uDD70").queue();
            s.addReaction("\uD83C\uDD71").queue();

            // Schedule to stop tracking it after 10 minutes
            event.getClient().getScheduleExecutor().schedule(() -> current.remove(s.getIdLong()), 10, TimeUnit.MINUTES);
        });
    }

    private boolean checkCondition(GenericGuildMessageReactionEvent event)
    {
        // First check if the message is being tracked by us
        if(!(current.contains(event.getMessageIdLong())))
            return false;

        // Then check the reacted emotes are valid
        MessageReaction.ReactionEmote reaction = event.getReactionEmote();
        if(reaction.isEmote())
            return false;

        String emote = reaction.getName();
        return emote.equals("\uD83C\uDD70") || emote.equals("\uD83C\uDD71");
    }

    private void runAction(GenericGuildMessageReactionEvent genericEvent)
    {
        if(genericEvent instanceof GuildMessageReactionAddEvent)
        {
            GuildMessageReactionAddEvent event = (GuildMessageReactionAddEvent) genericEvent;
            event.getChannel().getMessageById(event.getMessageIdLong()).queue(message ->
            {
                MessageEmbed embed = message.getEmbeds().stream().findFirst().orElse(null);
                if(embed == null)
                    return;

                MessageEmbed.Field field = embed.getFields().get(0);
                String users = field.getValue().equals("\u200E") ? event.getUser().getAsTag() :
                        (field.getValue() + ", " + event.getUser().getAsTag());

                EmbedBuilder newEmbed = new EmbedBuilder(embed)
                {{
                   clearFields();
                   addField(field.getName(), users, false);
                   addField(embed.getFields().get(1));
                }};

                message.editMessage(newEmbed.build()).queue();
            }, e -> {});
        }
        else if(genericEvent instanceof GuildMessageReactionRemoveEvent)
        {
            GuildMessageReactionRemoveEvent event = (GuildMessageReactionRemoveEvent) genericEvent;
            event.getChannel().getMessageById(event.getMessageIdLong()).queue(message ->
            {
                MessageEmbed embed = message.getEmbeds().stream().findFirst().orElse(null);
                if(embed == null)
                    return;

                MessageEmbed.Field field = embed.getFields().get(1);
                String users = field.getValue().equals("\u200E") ? event.getUser().getAsTag() :
                        (field.getValue() + ", " + event.getUser().getAsTag());

                EmbedBuilder newEmbed = new EmbedBuilder(embed)
                {{
                    clearFields();
                    addField(embed.getFields().get(0));
                    addField(field.getName(), users, false);
                }};

                message.editMessage(newEmbed.build()).queue();
            });
        }
    }
}
