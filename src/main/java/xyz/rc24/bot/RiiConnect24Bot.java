package xyz.rc24.bot;

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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.sentry.Sentry;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.botadm.Bash;
import xyz.rc24.bot.commands.botadm.Eval;
import xyz.rc24.bot.commands.botadm.MassMessage;
import xyz.rc24.bot.commands.botadm.Shutdown;
import xyz.rc24.bot.commands.tools.*;
import xyz.rc24.bot.commands.wii.*;
import xyz.rc24.bot.events.Morpher;
import xyz.rc24.bot.events.ServerLog;
import xyz.rc24.bot.loader.Config;
import xyz.rc24.bot.managers.BlacklistManager;
import xyz.rc24.bot.managers.ServerConfigManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Add all commands, and start all events.
 *
 * @author Spotlight and Artuto
 */

public class RiiConnect24Bot extends ListenerAdapter
{
    public BlacklistManager bManager;
    public Config config;

    public JedisPool pool;
    public ServerConfigManager scm;

    private ScheduledExecutorService bdaysScheduler;
    private ScheduledExecutorService musicNightScheduler;

    private final Logger LOGGER = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private final Logger logger = (Logger)LoggerFactory.getLogger("RiiConnect24 Bot");

    public static void main(String[] args) throws LoginException, IOException
    {
        new RiiConnect24Bot().run();
    }

    private void run() throws IOException, LoginException
    {
        LOGGER.setLevel(Level.INFO);

        try
        {
            config = new Config();
        }
        catch(Exception e)
        {
            logger.error(e.toString());
            return;
        }

        bManager = new BlacklistManager();
        bdaysScheduler = new ScheduledThreadPoolExecutor(2);
        musicNightScheduler = new ScheduledThreadPoolExecutor(2);

        // Start Sentry (if enabled)
        if(config.isSentryEnabled() && !(config.getSentryDSN()==null || config.getSentryDSN().isEmpty()))
            Sentry.init(config.getSentryDSN());

        // Register commands
        EventWaiter waiter = new EventWaiter();
        new Categories(bManager);

        CommandClientBuilder client = new CommandClientBuilder();
        client.setGame(Game.playing(config.getPlaying()));
        client.setStatus(config.getStatus());
        client.setEmojis(Const.DONE_E, Const.WARN_E, Const.FAIL_E);
        client.setOwnerId("" + config.getPrimaryOwner());

        // Convert Long[] of secondary owners to String[] so we can set later
        Long[] owners = config.getSecondaryOwners();
        String[] ownersString = new String[owners.length];

        for (int i = 0; i < owners.length; i++)
            ownersString[i] = String.valueOf(owners[i]);

        // Set all co-owners
        client.setCoOwnerIds(ownersString);
        client.setPrefix(config.getPrefix());
        client.setServerInvite("https://discord.gg/5rw6Tur");

        // Create JedisPool for usage elsewhere
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        scm = new ServerConfigManager(pool);
        client.addCommands(
                // Bot administration
                new Bash(),
                new Eval(this),
                new MassMessage(pool),
                new Shutdown(),

                // Tools
                new BotConfig(this),
                new UserInfo(),
                new Invite(),
                new MailParseCommand(config),
                new Ping(),

                // Wii-related
                new Codes(pool),
                new Add(this),
                new SetBirthday(pool),
                new ErrorInfo(config.isDebug()),
                new DNS(),
                new Wads(),
                new WiiWare()
        );

        //JDA Connection
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing(Const.GAME_0))
                .addEventListener(waiter)
                .addEventListener(client.build())
                .addEventListener(this)
                .addEventListener(new ServerLog(this))
                .addEventListener(new MailParseListener(this));
        if(config.isMorpherEnabled())
            builder.addEventListener(new Morpher(config));
        builder.build();
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        logger.info("Done loading!");
        // Check if we need to set a game
        if(config.getPlaying().isEmpty())
            event.getJDA().getPresence().setGame(Game.playing("Type " + config.getPrefix() + "help"));
        /*else
            event.getJDA().getPresence().setGame(Game.playing(config.getPlaying()));*/

        // It'll default to Type <prefix>help, per using the default game above.
        if(config.birthdaysAreEnabled())
        {
            // Every day at 8AM
            // And yes, we're assuming the channel exists. :fingers_crossed:
            ZonedDateTime localNow = OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC-6"));
            ZoneId currentZone = ZoneId.of("UTC-6");
            ZonedDateTime zonedNow = ZonedDateTime.of(localNow.toLocalDateTime(), currentZone);
            ZonedDateTime zonedNext8 = zonedNow.withHour(8).withMinute(0).withSecond(0);
            if(zonedNow.compareTo(zonedNext8) > 0)
                zonedNext8 = zonedNext8.plusDays(1);
            Duration duration = Duration.between(zonedNow, zonedNext8);
            long initialDelay = duration.getSeconds();

            bdaysScheduler.scheduleWithFixedDelay(() -> updateBirthdays(event.getJDA(), config.getBirthdayChannel()), initialDelay,
                    86400, TimeUnit.SECONDS);
        }

        if(config.isMusicNightReminderEnabled())
        {
            ZonedDateTime localNow = OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC-6"));
            ZoneId currentZone = ZoneId.of("UTC-6");
            ZonedDateTime zonedNow = ZonedDateTime.of(localNow.toLocalDateTime(), currentZone);
            ZonedDateTime zonedNext = zonedNow.withHour(18).withMinute(45).withSecond(0);
            if(zonedNow.compareTo(zonedNext) > 0)
                zonedNext = zonedNext.plusDays(1);
            Duration duration = Duration.between(zonedNow, zonedNext);
            long initialDelay = duration.getSeconds();

            musicNightScheduler.scheduleWithFixedDelay(() -> reminderMusicNight(event.getJDA()), initialDelay, 86400, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event)
    {
        bdaysScheduler.shutdown();
        musicNightScheduler.shutdown();
        pool.destroy();
    }

    private void updateBirthdays(JDA jda, long birthdayChannelId)
    {
        try(Jedis conn = pool.getResource())
        {
            TextChannel tc = jda.getTextChannelById(birthdayChannelId);

            conn.select(2);
            Map<String, String> birthdays = conn.hgetAll("birthdays").entrySet().stream().filter(b -> !(tc.getGuild().getMemberById(b.getKey())==null))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Get format of date used in stored dates
            LocalDate date = OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC-6")).toLocalDate();
            String today = date.getMonthValue()+"-"+date.getDayOfMonth();

            // Cycle through all birthdays.
            for (Map.Entry<String, String> userBirthday : birthdays.entrySet())
            {
                String userId = userBirthday.getKey();
                // The map's in the format of <user ID, birth date>
                if(userBirthday.getValue().equals(today))
                {
                    Member birthdayMember = tc.getGuild().getMemberById(userId);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Happy birthday! \uD83C\uDF82");
                    builder.setDescription("Please send them messages wishing them a happy birthday here on Discord and/or birthday mail on their Wii if" +
                            " you've registered them!");
                    builder.setColor(Color.decode("#00a6e9"));
                    builder.setAuthor("It's " + birthdayMember.getEffectiveName() + "'s birthday!",
                            "https://rc24.xyz/", birthdayMember.getUser().getEffectiveAvatarUrl());

                    tc.sendMessage(builder.build()).queue();
                }
                else
                    logger.debug("I considered " + userBirthday.getKey() + ", but their birthday isn't today.");
            }
        }
    }

    private void reminderMusicNight(JDA jda)
    {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CST"));
        c.setTime(new Date());
        if(!(c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY))
        {
            // Not today, m8
            return;
        }

        TextChannel general = jda.getTextChannelById(258999527783137280L);
        if(general==null || !(general.canTalk()))
            return;

        general.sendMessage("\u23F0 <@98938149316599808> **Music night in 15 minutes!**").queue();
    }
}
