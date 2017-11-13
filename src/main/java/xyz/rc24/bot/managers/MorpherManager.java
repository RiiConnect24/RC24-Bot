package xyz.rc24.bot.managers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class MorpherManager {
    /**
     * Redis for configuration use.
     */
    private final JedisPool pool;
    private final String keyName;

    public MorpherManager(String keyName) {
        this.pool = new JedisPool(new JedisPoolConfig(), URI.create("redis://localhost:6379/2"));
        this.keyName = keyName;
    }

    public void setAssociation(Long rootMessageID, Long mirroredMessageID) {
        try (Jedis conn = pool.getResource()) {
            conn.select(2);
            conn.hset(keyName, "" + rootMessageID, "" + mirroredMessageID);
        }
    }

    public void removeAssociation(Long rootMessageID) {
        try (Jedis conn = pool.getResource()) {
            conn.hdel(keyName, "" + rootMessageID);
        }
    }

    public void deleteAllAssociations(Long serverID) {
        try (Jedis conn = pool.getResource()) {
            conn.del(keyName);
        }
    }

    public Long getAssociation(Long rootMessageID) {
        try (Jedis conn = pool.getResource()) {
            try {
                return Long.parseLong(conn.hget(keyName, "" + rootMessageID));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
