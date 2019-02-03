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

import net.dv8tion.jda.core.OnlineStatus;

import java.util.List;

// We have to keep this public due to Jackson.
@SuppressWarnings("WeakerAccess")
public class ConfigFormat
{
    public String token;
    public String prefix;
    public String playing;
    public Long primary_owner;
    public Long[] secondary_owners;
    public OnlineStatus status;
    public Boolean debug;
    public Long root_server;
    public Boolean patch_mail;
    public Boolean morpher_enabled;
    public Long morpher_root;
    public Long morpher_mirror;
    public Boolean birthdays_enabled;
    public Long birthdays_channel;
    public List<Long> logged_servers;
    public List<Long> ignore_ids;
    public String sentry_dsn;
    public boolean sentry_enabled;
    public boolean music_night_reminder;
}
