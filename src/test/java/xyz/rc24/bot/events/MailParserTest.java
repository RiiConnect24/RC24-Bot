package xyz.rc24.bot.events;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MailParserTest {
    @Test
    public void testLength() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/incorrect_length.cfg");
        byte[] file = new MailParser().patchMail(stream);
        assertEquals(file.length, 1);
        // 0x21 - incorrect length detected
        assertEquals(file[0], 0x21);
    }

    @Test
    public void testMagic() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/incorrect_magic.cfg");
        byte[] file = new MailParser().patchMail(stream);
        assertEquals(file.length, 1);
        // 0x69 - incorrect magic detected
        assertEquals(file[0], 0x69);
    }

    @Test
    public void testActualPatch() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/correct_orig.cfg");
        byte[] patchedFile = new MailParser().patchMail(stream);

        InputStream correctStream = getClass().getResourceAsStream("/correct_patched.cfg");
        byte[] correctFile = IOUtils.toByteArray(correctStream);

        assertArrayEquals(patchedFile, correctFile);
    }
}
