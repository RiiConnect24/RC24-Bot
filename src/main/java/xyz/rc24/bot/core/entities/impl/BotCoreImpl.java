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

import com.google.common.cache.Cache;
import net.dv8tion.jda.core.entities.Guild;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.SimpleCacheBuilder;
import xyz.rc24.bot.core.entities.EntityBuilder;
import xyz.rc24.bot.core.entities.GuildSettings;

import java.util.concurrent.ExecutionException;

/**
 * @author Artuto
 */

public class BotCoreImpl implements BotCore
{
    // Caches
    private final Cache<Long, GuildSettings> gsCache = new SimpleCacheBuilder<>().build();

    private final Bot bot;
    private final EntityBuilder entityBuilder;

    public BotCoreImpl(Bot bot)
    {
        this.bot = bot;
        this.entityBuilder = new EntityBuilder();
    }

    @Override
    public GuildSettings getGuildSettings(Guild guild) throws ExecutionException
    {
        return getGuildSettings(guild.getIdLong());
    }

    @Override
    public GuildSettings getGuildSettings(long guild) throws ExecutionException
    {
        return gsCache.get(guild, () -> bot.getGuildSettingsDataManager().getGuildSettings(guild));
    }

    public EntityBuilder getEntityBuilder()
    {
        return entityBuilder;
    }
}
