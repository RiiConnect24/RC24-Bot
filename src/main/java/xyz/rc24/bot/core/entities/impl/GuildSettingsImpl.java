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

package xyz.rc24.bot.core.entities.impl;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.core.entities.LogType;

import java.util.Set;

public class GuildSettingsImpl implements GuildSettings, GuildSettingsProvider
{
    private CodeType defaultAddType;
    private long birthdaysId, guildId, modlogId, serverlogId;
    private Set<String> prefixes;

    public GuildSettingsImpl(CodeType defAddType, long bId, long gId, long mId, long sId, Set<String> prefixes)
    {
        this.defaultAddType = defAddType;
        this.birthdaysId = bId;
        this.guildId = gId;
        this.modlogId = mId;
        this.serverlogId = sId;
        this.prefixes = prefixes;
    }

    @Override
    public CodeType getDefaultAddType()
    {
        return defaultAddType;
    }

    @Override
    public long getBirthdaysChannelId()
    {
        return birthdaysId;
    }

    @Override
    public long getGuildId()
    {
        return guildId;
    }

    @Override
    public long getModlogChannelId()
    {
        return modlogId;
    }

    @Override
    public long getServerlogChannelId()
    {
        return serverlogId;
    }

    @Override
    public Set<String> getPrefixes()
    {
        return prefixes;
    }

    @Override
    public String getFirstPrefix()
    {
        return getPrefixes().stream().findFirst().orElse("@mention");
    }

    @Override
    public long getLog(LogType type)
    {
        switch(type)
        {
            case MOD:
                return getModlogChannelId();
            case SERVER:
                return getServerlogChannelId();
            default:
                return 0L;
        }
    }

    public void setModlogId(long id)
    {
        this.modlogId = id;
    }

    public void setServerlogId(long id)
    {
        this.serverlogId = id;
    }

    public void setDefaultAddType(CodeType type)
    {
        this.defaultAddType = type;
    }

    public void setPrefixes(Set<String> prefixes)
    {
        this.prefixes = prefixes;
    }
}
