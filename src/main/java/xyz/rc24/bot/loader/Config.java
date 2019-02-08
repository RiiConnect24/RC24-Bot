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

package xyz.rc24.bot.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.dv8tion.jda.core.OnlineStatus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Spotlight
 */

public class Config
{
    private static ConfigFormat format;

    public Config() throws Exception
    {
        // TODO: copy sample file
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        format = mapper.readValue(new File("data/config.yml"), ConfigFormat.class);
        // Check config
        for(Field field : format.getClass().getDeclaredFields())
        {
            // For each field defined in the class, check if null.
            if(field.get(format) == null) throw new Exception(field.getName() + " in your config was null!");
        }
    }

    public String getToken()
    {
        return format.token;
    }

    public String getPrefix()
    {
        return format.prefix;
    }

    public String getPlaying()
    {
        return format.playing;
    }

    public Long getPrimaryOwner()
    {
        return format.primary_owner;
    }

    public Long[] getSecondaryOwners()
    {
        return format.secondary_owners;
    }

    public OnlineStatus getStatus()
    {
        return format.status;
    }

    public Boolean isDebug()
    {
        return format.debug;
    }

    public Long getRootServer()
    {
        return format.root_server;
    }

    public Boolean isMailPatchEnabled()
    {
        return format.patch_mail;
    }

    public Boolean isMorpherEnabled()
    {
        return format.morpher_enabled;
    }

    public Long getMorpherRoot()
    {
        return format.morpher_root;
    }

    public Long getMorpherMirror()
    {
        return format.morpher_mirror;
    }

    public Boolean birthdaysAreEnabled()
    {
        return format.birthdays_enabled;
    }

    public Long getBirthdayChannel()
    {
        return format.birthdays_channel;
    }

    public List<Long> getLoggedServers()
    {
        return format.logged_servers;
    }

    public List<Long> getIgnoredIDs()
    {
        return format.ignore_ids;
    }

    public boolean isSentryEnabled()
    {
        return format.sentry_enabled;
    }

    public String getSentryDSN()
    {
        return format.sentry_dsn;
    }

    public Boolean isMusicNightReminderEnabled()
    {
        return format.music_night_reminder;
    }

    // Database
    public String getDatabaseHost()
    {
        return format.dbHost;
    }

    public String getDatabaseUser()
    {
        return format.dbUser;
    }

    public String getDatabasePassword()
    {
        return format.dbPassword;
    }

    public String getDatabase()
    {
        return format.db;
    }
}
