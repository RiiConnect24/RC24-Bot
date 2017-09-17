package xyz.rc24.bot.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class MorpherManager {
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;
    private String keyName;

    public MorpherManager(String keyName) {
        this.pool = new JedisPool(new JedisPoolConfig(), "localhost");
        this.keyName = keyName;
    }

    public void setAssociation(Long rootMessageID, Long mirroredMessageID) {
        Jedis conn = pool.getResource();
        conn.hset(keyName, "" + rootMessageID, "" + mirroredMessageID);
    }

    public void removeAssociation(Long rootMessageID) {
        Jedis conn = pool.getResource();
        // Make sure there's something to delete.
        conn.hdel(keyName, "" + rootMessageID);
    }

    public void deleteAllAssociations(Long serverID) {
        Jedis conn = pool.getResource();
        conn.del(keyName);
    }

    public Long getAssociation(Long rootMessageID) {
        Jedis conn = pool.getResource();
        try {
            return Long.parseLong(conn.hget(keyName, "" + rootMessageID));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Terminates Jedis connection.
     */
    public void destroy() {
        pool.destroy();
    }
}
