package xyz.rc24.bot.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class CodeManager {
    /**
     * Redis for configuration use.
     */
    private static JedisPool pool;

    /**
     * Each int corresponds to the sorted level on Redis.
     */
    public enum Type {
        WII,
        THREE_DS,
        NNID,
        SWITCH,
        GAME
    }

    private String getKeyName(Long userID, Type codeType) {
        return userID.toString() + ":" + codeType.toString();
    }

    public CodeManager() {
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
    }

    /**
     * Add a code for a user.
     *
     * @param userID User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param code Value of the code
     */
    public void addCode(Long userID, Type codeType, String codeName, String code) {
        Jedis conn = pool.getResource();
        conn.hset(getKeyName(userID, codeType), codeName, code);
        conn.close();
    }

    /**
     * Edits the code for a user.
     *
     * @param userID User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param newCode Value to edit
     * @return Boolean, true means success, false means code doesn't exist
     */
    public Boolean editCode(Long userID, Type codeType, String codeName, String newCode) {
        String keyName = getKeyName(userID, codeType);
        Jedis conn = pool.getResource();
        // Since the key'd just be created again with hset, make sure to check
        if (conn.hexists(keyName, codeName)) {
            conn.hset(keyName, codeName, newCode);
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteCode(Long userID, Type codeType, String codeName) {
        String keyName = getKeyName(userID, codeType);
        Jedis conn = pool.getResource();
        // Since the key'd just be created again with hset, make sure to check
        if (conn.hexists(keyName, codeName)) {
            conn.hdel(keyName, codeName);
            return true;
        } else {
            return false;
        }
    }

    public Map<Type, Map<String, String>> getAllCodes(Long userID) {
        Map<Type, Map<String, String>> codes = new HashMap<>();
        Jedis conn = pool.getResource();
        for (Type currentType : Type.values()) {
            codes.put(currentType, conn.hgetAll(getKeyName(userID, currentType)));
        }
        return codes;
    }

    /**
     * Terminates Jedis connection.
     */
    public void destroy() {
        pool.destroy();
    }
}
