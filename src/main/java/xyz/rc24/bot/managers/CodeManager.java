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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
@Deprecated
public class CodeManager
{
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;

    /**
     * Each int corresponds to the sorted level on Redis.
     */
    public enum Type
    {WII, THREE_DS, NNID, SWITCH, GAME}

    private String getKeyName(Long userID, Type codeType)
    {
        return userID.toString() + ":" + codeType.toString();
    }

    public CodeManager(JedisPool pool)
    {
        this.pool = pool;
    }

    /**
     * Add a code for a user.
     *
     * @param userID   User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param code     Value of the code
     */
    public void addCode(Long userID, Type codeType, String codeName, String code)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.hset(getKeyName(userID, codeType), codeName, code);
        }
    }

    /**
     * Edits the code for a user.
     *
     * @param userID   User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param newCode  Value to edit
     * @return Boolean, true means success, false means code doesn't exist
     */
    public Boolean editCode(Long userID, Type codeType, String codeName, String newCode)
    {
        String keyName = getKeyName(userID, codeType);
        // Since the key'd just be created again with hset, make sure to check
        Boolean result;
        try(Jedis conn = pool.getResource())
        {
            result = conn.hexists(keyName, codeName);
            if(result)
            {
                conn.hset(keyName, codeName, newCode);
            }
        }
        return result;
    }

    public Boolean removeCode(Long userID, Type codeType, String codeName)
    {
        String keyName = getKeyName(userID, codeType);
        Boolean result;
        // Make sure there's something to delete.
        try(Jedis conn = pool.getResource())
        {
            result = conn.hexists(keyName, codeName);
            if(result)
            {
                conn.hdel(keyName, codeName);
            }
        }
        return result;
    }

    public Map<Type, Map<String, String>> getAllCodes(Long userID)
    {
        Map<Type, Map<String, String>> codes = new HashMap<>();
        try(Jedis conn = pool.getResource())
        {
            for(Type currentType : Type.values())
            {
                codes.put(currentType, conn.hgetAll(getKeyName(userID, currentType)));
            }
        }
        return codes;
    }
}
