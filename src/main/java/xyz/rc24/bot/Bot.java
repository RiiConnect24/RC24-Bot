/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
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

package xyz.rc24.bot;

import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import xyz.rc24.bot.commands.CommandAutoCompletion;
import xyz.rc24.bot.commands.CommandManager;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.core.entities.impl.BotCoreImpl;
import xyz.rc24.bot.database.BirthdayDataManager;
import xyz.rc24.bot.database.CodeDataManager;
import xyz.rc24.bot.database.Database;
import xyz.rc24.bot.database.GuildSettingsDataManager;
import xyz.rc24.bot.listeners.DataDogStatsListener;
import xyz.rc24.bot.listeners.RaidListener;
import xyz.rc24.bot.managers.BirthdayManager;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Add all commands, and start all listeners.
 *
 * @author Spotlight and Artuto
 */

public class Bot extends ListenerAdapter {

    public BotCore core;
    public Config config;
    public JDA jda;

    // Database & Data Managers
    private Database db;
    private BirthdayDataManager birthdayDataManager;
    private CodeDataManager codeDataManager;
    private GuildSettingsDataManager guildSettingsDataManager;

    // Managers
    private BirthdayManager birthdayManager;

    private final Logger logger = RiiConnect24Bot.getLogger();
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ScheduledExecutorService botScheduler = Executors.newScheduledThreadPool(5);
    private final ScheduledExecutorService birthdaysScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentLinkedDeque<String> consoleCommandsAwaitingProcessing = new ConcurrentLinkedDeque<>();

    private CommandManager commandManager;

    JDABuilder initialize() {
        this.config = new Config();
        this.core = new BotCoreImpl(this);

        // Start Sentry (if enabled)
        if (config.isSentryEnabled() && !(config.getSentryDSN().isEmpty()))
            Sentry.init(config.getSentryDSN());

        // Start database
        this.db = initDatabase();
        this.birthdayDataManager = new BirthdayDataManager(db);
        this.codeDataManager = new CodeDataManager(this);
        this.guildSettingsDataManager = new GuildSettingsDataManager(this);

        // Start managers
        this.birthdayManager = new BirthdayManager(getBirthdayDataManager());

        commandManager = new CommandManager();

        DataDogStatsListener dataDogStatsListener = null;

        if (config.isDatadogEnabled()) {
            NonBlockingStatsDClientBuilder dataDogBuilder = new NonBlockingStatsDClientBuilder()
                    .prefix(config.getDatadogPrefix())
                    .hostname(config.getDatabaseHost())
                    .port(config.getDatadogPort());
            StatsDClient statsd = dataDogBuilder.build();
            dataDogStatsListener = new DataDogStatsListener(statsd);
        }

        // Convert List<Long> of secondary owners to String[] so we can set later
        List<Long> owners = config.getSecondaryOwners();
        String[] coOwners = new String[owners.size()];

        for (int i = 0; i < owners.size(); i++)
            coOwners[i] = String.valueOf(owners.get(i));

        // JDA Connection
        JDABuilder builder = JDABuilder.createLight(config.getToken())
                .setEnabledIntents(RiiConnect24Bot.INTENTS)
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new RaidListener(config.useRaidProtection()))
                .addEventListeners(commandManager)
                .addEventListeners(this)
                .addEventListeners(new CommandAutoCompletion())
                .setStatus("I've been updated to perform nicer and run faster.");


        if (!(dataDogStatsListener == null))
            builder.addEventListeners(dataDogStatsListener);

        return builder;
    }

    void run() {
        JDABuilder jdaBuilder = initialize();
        this.jda = jdaBuilder.build();
        jda.updateCommands().addCommands(commandManager.getCommandDataList()).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Done loading!");

        ZonedDateTime zonedNow = OffsetDateTime.now().toZonedDateTime();
        ZonedDateTime zonedNext;

        if (config.areBirthdaysEnabled()) {
            // Every day at 8AM
            zonedNext = zonedNow.withHour(8).withMinute(0).withSecond(0);
            if (zonedNow.compareTo(zonedNext) > 0)
                zonedNext = zonedNext.plusDays(1);

            Duration duration = Duration.between(zonedNow, zonedNext);
            long initialDelay = duration.getSeconds();

            birthdaysScheduler.scheduleWithFixedDelay(() -> getBirthdayManager()
                    .updateBirthdays(event.getJDA(), config.getBirthdayChannel(),
                            config.getPrimaryOwner()), initialDelay, 86400, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        birthdaysScheduler.shutdown();
        DB.close();
    }

    private Database initDatabase() {
        if (config.getDatabaseUser().isEmpty() || config.getDatabasePassword().isEmpty() ||
                config.getDatabase().isEmpty() || config.getDatabaseHost().isEmpty()) {
            throw new IllegalStateException("You haven't configured database details in the config file!");
        }

        DatabaseOptions options = DatabaseOptions.builder()
                .mysql(config.getDatabaseUser(), config.getDatabasePassword(), config.getDatabase(), config.getDatabaseHost())
                .driverClassName(Driver.class.getName() /*"com.mysql.cj.jdbc.Driver"*/)
                .dataSourceClassName(MysqlDataSource.class.getName() /*"com.mysql.cj.jdbc.MysqlDataSource"*/)
                .build();

        Map<String, Object> props = new HashMap<>() {{
            put("useSSL", config.useSSL());
            put("verifyServerCertificate", config.verifyServerCertificate());
            put("autoReconnect", config.autoReconnect());
            put("characterEncoding", "UTF-8");
        }};

        co.aikar.idb.Database db = PooledDatabaseOptions.builder()
                .dataSourceProperties(props)
                .options(options)
                .createHikariDatabase();

        DB.setGlobalDatabase(db);

        return new Database();
    }

    public BotCore getCore() {
        return core;
    }

    public Config getConfig() {
        return config;
    }

    public Database getDatabase() {
        return db;
    }

    public JDA getJDA() {
        return jda;
    }

    public ScheduledExecutorService getBotScheduler() {
        return botScheduler;
    }

    public BirthdayDataManager getBirthdayDataManager() {
        return birthdayDataManager;
    }

    public CodeDataManager getCodeDataManager() {
        return codeDataManager;
    }

    public GuildSettingsDataManager getGuildSettingsDataManager() {
        return guildSettingsDataManager;
    }

    public GuildSettings getGuildSettings(long snowflakeID) {
        return getGuildSettingsDataManager().getGuildSettings(snowflakeID);
    }

    public GuildSettings getGuildSettings(Guild guild) {
        return getGuildSettingsDataManager().getSettings(guild);
    }

    public BirthdayManager getBirthdayManager() {
        return birthdayManager;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public String getPrefix(Guild guild) {
        return getCore().getGuildSettings(guild).getPrefix();
    }

}
