/*
 * MIT License
 *
 * Copyright (c) 2017-2019 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rc24.bot;

import javax.annotation.Nonnull;
import net.dv8tion.jda.core.OnlineStatus;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Artuto
 */

public class Config
{
    private Map<String, Object> config;
    private Map<String, Object> database;

    @SuppressWarnings("unchecked")
    Config()
    {
        Yaml yaml = new Yaml();
        File file = new File("config.yml");

        if(!(file.exists()))
        {
            try(InputStream is = Bot.class.getResourceAsStream("/config.yml"))
            {Files.copy(is, file.toPath());}
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        try(InputStream is = new FileInputStream(file))
        {
            config = yaml.load(is);
            database = (Map<String, Object>) config.get("database"); // database section
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getToken()
    {
        return getString("token");
    }

    public String getPrefix()
    {
        return getString("prefix");
    }

    public String getPlaying()
    {
        return getString("playing");
    }

    public long getPrimaryOwner()
    {
        return getLong("owner");
    }

    @SuppressWarnings("unchecked")
    public List<Long> getSecondaryOwners()
    {
        return (List) config.getOrDefault("secondary_owners", Collections.emptyList());
    }

    public OnlineStatus getStatus()
    {
        return OnlineStatus.fromKey(getString("online_status"));
    }

    public boolean isDebug()
    {
        return getBoolean("debug");
    }

    public boolean isMailPatchEnabled()
    {
        return getBoolean("patch_mail");
    }

    public boolean isMorpherEnabled()
    {
        return getBoolean("morpher_enabled");
    }

    public long getMorpherRoot()
    {
        return getLong("morpher_root");
    }

    public long getMorpherMirror()
    {
        return getLong("morpher_mirror");
    }

    public boolean birthdaysAreEnabled()
    {
        return getBoolean("birthdays_enabled");
    }

    public long getBirthdayChannel()
    {
        return getLong("birthdays_channel");
    }

    public boolean isSentryEnabled()
    {
        return getBoolean("sentry_enabled");
    }

    public String getSentryDSN()
    {
        return getString("sentry_dsn");
    }

    public boolean isMusicNightReminderEnabled()
    {
        return getBoolean("music_night_reminder");
    }

    // Database
    public String getDatabaseHost()
    {
        return getString("host", database);
    }

    public String getDatabaseUser()
    {
        return getString("user", database);
    }

    public String getDatabasePassword()
    {
        return getString("password", database);
    }

    public String getDatabase()
    {
        return getString("database", database);
    }

    boolean useSSL()
    {
        return getBoolean("useSSL", database);
    }

    boolean verifyServerCertificate()
    {
        return getBoolean("verifyServerCertificate", database);
    }

    boolean autoReconnect()
    {
        return getBoolean("autoReconnect", database);
    }

    // Util methods
    private boolean getBoolean(String key)
    {
        return (boolean) config.getOrDefault(key, false);
    }

    private boolean getBoolean(String key, @Nonnull Map<String, Object> section)
    {
        return (boolean) section.getOrDefault(key, false);
    }

    private long getLong(String key)
    {
        return (long) config.getOrDefault(key, 0L);
    }

    private String getString(String key)
    {
        return getString(key, config);
    }

    private String getString(String key, @Nonnull Map<String, Object> section)
    {
        return (String) section.getOrDefault(key, "");
    }
}
