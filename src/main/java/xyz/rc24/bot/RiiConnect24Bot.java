/*
 * The MIT License
 *
 * Copyright 2017 Spotlight and Artu.
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

package xyz.rc24.bot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;
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

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Add all commands, and start all events.
 *
 * @author Spotlight and Artu
 */

public class RiiConnect24Bot extends ListenerAdapter {

    private static Config config;
    private static JedisPool pool;
    private static String prefix;

    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException, InterruptedException {
        try {
            config = new Config();
        } catch (Exception e) {
            SimpleLog.getLog("Config").fatal(e);
            return;
        }

        // Register commands
        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setGame(Game.of("Loading..."));
        client.setOwnerId("" + config.getPrimaryOwner());

        // Convert Long[] of secondary owners to String[] so we can set later
        Long[] owners = config.getSecondaryOwners();
        String[] ownersString = new String[owners.length];

        for (int i = 0; i < owners.length; i++) {
            ownersString[i] = String.valueOf(owners[i]);
        }

        // Set all co-owners
        client.setCoOwnerIds(ownersString);
        client.setEmojis(Const.DONE_E, Const.WARN_E, Const.FAIL_E);
        prefix = config.getPrefix();
        client.setPrefix(prefix);

        // Create JedisPool for usage elsewhere
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        client.addCommands(
                // Bot administration
                new Bash(),
                new Eval(pool, config),
                new MassMessage(pool),
                new Shutdown(),

                // Tools
                new BotConfig(pool),
                new UserInfo(),
                new Invite(),
                new MailParseCommand(),

                // Wii-related
                new Codes(pool),
                new Add(pool),
                new SetBirthday(pool),
                new ErrorInfo(config.isDebug()),
                new DNS(),
                new Wads()
        );

        //JDA Connection
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .setStatus(config.getStatus())
                .setGame(Game.of(Const.GAME_0))
                .addEventListener(waiter)
                .addEventListener(client.build())
                .addEventListener(new RiiConnect24Bot())
                .addEventListener(new ServerLog(pool))
                .addEventListener(new MailParseListener());
        if (config.isMorpherEnabled()) {
            builder.addEventListener(new Morpher(config));
        }
        builder.buildBlocking();
    }

    @Override
    public void onReady(ReadyEvent event) {
        SimpleLog.getLog("Bot").info("Done loading!");
        // Check if we need to set a game
        if (config.getPlaying().isEmpty()) {
            event.getJDA().getPresence().setGame(Game.of("Type " + prefix + "help"));
        } else {
            event.getJDA().getPresence().setGame(Game.of(config.getPlaying()));
        }

        // It'll default to Type <prefix>help, per using the default game above.
        if (config.birthdaysAreEnabled()) {
            // Set up birthday routine
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);

            // Every day at midnight
            // And yes, we're assuming the channel exists. :fingers_crossed:
            Timer timer = new Timer();
            timer.schedule(new BirthdayEvent(config.getBirthdayChannel(), pool, event.getJDA()), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        pool.destroy();
    }
}
