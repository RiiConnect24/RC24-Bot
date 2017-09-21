package xyz.rc24.bot.events;

import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

/**
 * Happy Birthday :confetti:
 */
public class BirthdayEvent extends TimerTask {
    private static Long birthdayChannel;
    private static JDA jda;
    public BirthdayEvent(Long birthdayChannel, JDA jda) {
        BirthdayEvent.birthdayChannel = birthdayChannel;
        BirthdayEvent.jda = jda;
    }

    @Override
    public void run() {
        jda.getTextChannelById(birthdayChannel).sendMessage("Did this work?").queue();
    }
}
