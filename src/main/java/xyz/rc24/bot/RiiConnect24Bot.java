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
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xyz.rc24.bot.commands.botadm.Bash;
import xyz.rc24.bot.commands.botadm.Eval;
import xyz.rc24.bot.commands.botadm.MassMessage;
import xyz.rc24.bot.commands.botadm.Shutdown;
import xyz.rc24.bot.commands.tools.*;
import xyz.rc24.bot.commands.wii.*;
import xyz.rc24.bot.events.BirthdayEvent;
import xyz.rc24.bot.events.Morpher;
import xyz.rc24.bot.events.ServerLog;
import xyz.rc24.bot.loader.Config;

import java.util.Calendar;
import java.util.Timer;

/**
 * Add all commands, and start all events.
 *
 * @author Spotlight and Artuto
 */

public class RiiConnect24Bot extends ListenerAdapter
{
    private static Config config;
    private static JedisPool pool;
    private static String prefix;
    private static Logger LOGGER = (Logger)(Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static Logger logger = (Logger)(Logger)LoggerFactory.getLogger("RiiConnect24 Bot");

    public static void main(String[] args) throws Exception
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

        // Start Sentry (if enabled)
        if(config.isSentryEnabled() && !(config.getSentryDSN()==null || config.getSentryDSN().isEmpty()))
            Sentry.init(config.getSentryDSN());

        // Register commands
        EventWaiter waiter = new EventWaiter();

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
        prefix = config.getPrefix();
        client.setPrefix(prefix);
        client.setServerInvite("https://discord.gg/5rw6Tur");

        // Create JedisPool for usage elsewhere
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        client.addCommands(
                // Bot administration
                new Bash(),
                new Eval(pool, config),
                new MassMessage(pool),
                new Shutdown(),

                // Tools
                new BotConfig(),
                new UserInfo(),
                new Invite(),
                new MailParseCommand(config),
                new Ping(),

                // Wii-related
                new Codes(pool),
                new Add(pool),
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
                .addEventListener(new RiiConnect24Bot())
                .addEventListener(new ServerLog())
                .addEventListener(new MailParseListener(config));
        if(config.isMorpherEnabled())
            builder.addEventListener(new Morpher(config));
        builder.buildBlocking();
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        logger.info("Done loading!");
        // Check if we need to set a game
        if(config.getPlaying().isEmpty())
            event.getJDA().getPresence().setGame(Game.playing("Type " + prefix + "help"));
        /*else
            event.getJDA().getPresence().setGame(Game.playing(config.getPlaying()));*/

        // It'll default to Type <prefix>help, per using the default game above.
        if(config.birthdaysAreEnabled())
        {
            // Every day at midnight
            // And yes, we're assuming the channel exists. :fingers_crossed:
            Calendar today = Calendar.getInstance(); today.set(Calendar.HOUR_OF_DAY, 8); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0);
            Timer bdays = new Timer();
            bdays.scheduleAtFixedRate(
                   new BirthdayEvent(config.getBirthdayChannel(), pool, event.getJDA()),
                    today.getTime(), 86400000);
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event)
    {
        pool.destroy();
    }
}
