package xyz.rc24.bot.events;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.TimerTask;

/**
 * Happy Birthday :confetti:
 *
 * @author Spotlight, Artu
 */
public class BirthdayEvent extends TimerTask {
    private static Long birthdayChannelID;
    private static JDA jda;
    private static JedisPool pool;
    private static final Logger logger = (Logger)LoggerFactory.getLogger(BirthdayEvent.class);

    public BirthdayEvent(Long birthdayChannelID, JedisPool pool, JDA jda) {
        BirthdayEvent.pool = pool;
        BirthdayEvent.birthdayChannelID = birthdayChannelID;
        BirthdayEvent.jda = jda;
    }

    @Override
    public void run() {
        try (Jedis conn = pool.getResource()) {
            conn.select(2);
            Map<String, String> birthdays = conn.hgetAll("birthdays");

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
}
