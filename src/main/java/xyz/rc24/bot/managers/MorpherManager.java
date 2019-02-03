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

package xyz.rc24.bot.managers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class MorpherManager
{
    /**
     * Redis for configuration use.
     */
    private final JedisPool pool;
    private final String keyName;

    public MorpherManager(String keyName)
    {
        this.pool = new JedisPool(new JedisPoolConfig(), URI.create("redis://localhost:6379/2"));
        this.keyName = keyName;
    }

    public void setAssociation(Long rootMessageID, Long mirroredMessageID)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.select(2);
            conn.hset(keyName, "" + rootMessageID, "" + mirroredMessageID);
        }
    }

    public void removeAssociation(Long rootMessageID)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.hdel(keyName, "" + rootMessageID);
        }
    }

    public void deleteAllAssociations(Long serverID)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.del(keyName);
        }
    }

    public long getAssociation(Long rootMessageID)
    {
        try(Jedis conn = pool.getResource())
        {
            try
            {
                return Long.parseLong(conn.hget(keyName, "" + rootMessageID));
            }
            catch(NumberFormatException e)
            {
                return 0L;
            }
        }
    }
}
