package xyz.rc24.bot.mangers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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
        this.pool = new JedisPool(new JedisPoolConfig(), "localhost");
        this.keyName = keyName;
    }

    public void setAssociation(Long rootMessageID, Long mirroredMessageID) {
        try (Jedis conn = pool.getResource()) {
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
