package xyz.rc24.bot.managers;

import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xyz.rc24.bot.mangers.CodeManager;

import static org.junit.Assert.assertEquals;

public class CodeManagerTest {

    private JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    @Test
    public void testEdit() {
        for (CodeManager.Type type : CodeManager.Type.values()) {
            CodeManager test = new CodeManager(pool);
            test.addCode(1L, type, "This is a test.", "Did it work?");
            // Edit code
            assertEquals(
                    test.editCode(1L, type, "This is a test.", "Still a test."),
                    true
            );
            assertEquals(
                    test.getAllCodes(1L).get(type).get("This is a test."),
                    "Still a test."
            );
        }
    }

    @Test
    public void testDeletion() {
        for (CodeManager.Type type : CodeManager.Type.values()) {
            CodeManager test = new CodeManager(pool);
            // Delete code
            assertEquals(
                    test.removeCode(1L, type, "This is a test."),
                    true
            );
        }
    }

    @Test
    public void testDeletionOfNonExistent() {
        for (CodeManager.Type type : CodeManager.Type.values()) {
            CodeManager test = new CodeManager(pool);
            // The deleted code doesn't exist, make sure it detects that.
            assertEquals(
                    test.removeCode(1L, type, "I don't exist."),
                    false
            );
        }
    }

    @Test
    public void testEditOfNonExistent() {
        for (CodeManager.Type type : CodeManager.Type.values()) {
            CodeManager test = new CodeManager(pool);
            // Attempt to edit the deleted key.
            assertEquals(
                    test.editCode(1L, type, "I don't exist.", "Am I here?"),
                    false
            );
        }
    }
}
