package xyz.rc24.bot.events;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Happy Birthday :confetti:
 */
public class BirthdayEvent extends TimerTask {
    private static Long birthdayChannelID;
    private static JDA jda;
    private Datastore datastore;
    private Key key;
    private static final Logger logger = LoggerFactory.getLogger(BirthdayEvent.class);

    public BirthdayEvent(Long birthdayChannelID, Datastore datastore, JDA jda) {
        this.datastore = datastore;
        this.key = datastore.newKeyFactory().setKind("birthdays").newKey("birthdays");
        BirthdayEvent.birthdayChannelID = birthdayChannelID;
        BirthdayEvent.jda = jda;
    }

    @Override
    public void run() {
        Map<String, String> birthdays = new HashMap<>();
        Entity storedBirthdays = datastore.get(key);
        try {
            for (String userID : storedBirthdays.getNames()) {
                birthdays.put(userID, storedBirthdays.getString(userID));
            }
        } catch (NullPointerException e) {
            // Guess there aren't any birthdays.
            return;
        }

        TextChannel birthday = jda.getTextChannelById(birthdayChannelID);

        // Get format of date used in stored dates
        LocalDate test = LocalDate.now();
        String today = test.getMonthValue() + "-" + test.getDayOfMonth();

        // Cycle through all birthdays.
        for (Map.Entry<String, String> userBirthday : birthdays.entrySet()) {
            String userID = userBirthday.getKey();
            // The map's in the format of <user ID, birth date>
            if (userBirthday.getValue().equals(today)) {
                // Make sure the user's still on the server.
                Member birthdayMember = birthday.getGuild().getMemberById(userID);
                if (birthdayMember == null) {
                    return;
                }

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Happy birthday! \uD83C\uDF82");
                builder.setDescription("Please send them messages wishing them a happy birthday here on Discord and/or birthday mail on their Wii if you've registered them!");
                builder.setColor(Color.decode("#00a6e9"));
                builder.setAuthor("It's " + birthdayMember.getEffectiveName() + "'s birthday!",
                        "https://rc24.xyz/",
                        birthdayMember.getUser().getEffectiveAvatarUrl());

                jda.getTextChannelById(birthdayChannelID).sendMessage(builder.build()).queue();
            } else {
                logger.debug("I considered " + userBirthday.getKey() + ", but their birthday isn't today.");
            }
        }
    }
}
