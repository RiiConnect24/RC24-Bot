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

package xyz.rc24.bot.listeners;

import ch.qos.logback.classic.Logger;
import org.apache.commons.io.IOUtils;
import xyz.rc24.bot.RiiConnect24Bot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Spotlight
 */

@Deprecated
public class MailParser
{
    private final Logger logger = (Logger) RiiConnect24Bot.getLogger(MailParser.class);

    /**
     * The following is based off a specific format.
     * The developer writing this has tried to comment
     * appropriately given the current task.
     *
     * @param stream Stream to manipulate with.
     * @throws IOException Only when a stream cannot be closed/read.
     * @docs https://github.com/RiiConnect24/Kaitai-Files/blob/master/Kaitais/nwc24msg.ksy
     */
    public byte[] patchMail(InputStream stream) throws IOException
    {
        // Yes, I used commons-io just for this.
        // I'm sorry.
        byte[] file = IOUtils.toByteArray(stream);
        stream.close();

        // Simple file checks
        logger.debug("Length: " + file.length);
        if(! (file.length == 1024))
        {
            return new byte[]{0x21};
        }

        byte[] expectedMagic = "WcCf".getBytes();
        byte[] presentMagic = Arrays.copyOfRange(file, 0, 4);
        if(! Arrays.equals(presentMagic, expectedMagic))
        {
            return new byte[]{0x69};
        }

        byte[] mailDomain = new byte[40];
        byte[] mailString = "@rc24.xyz".getBytes();
        // We copy the string into the domain to preserve length.
        System.arraycopy(mailString, 0, mailDomain, 0, mailString.length);
        // Then, we copy the domain into its proper place in the file.
        // The entry for mail is 24 bytes into the file.
        System.arraycopy(mailDomain, 0, file, 24, mailDomain.length);

        // The following list is in order of engine type.
        String[] engineTypes = new String[]{"account", "check", "receive", "delete", "send"};
        // We start the list of offsets 156 bytes off into the file.
        Integer currentPos = 0x9C;
        for(String type : engineTypes)
        {
            // We create a byte[] with the proper length for the engine.
            byte[] sizedEngineURL = new byte[80];
            byte[] engineURL = ("http://mtw.rc24.xyz/cgi-bin/" + type + ".cgi").getBytes();
            // Then, we copy the engine URL (in string) to the proper length, to preserve size.
            System.arraycopy(engineURL, 0, sizedEngineURL, 0, engineURL.length);

            // Now, we copy it into its proper place in the file.
            System.arraycopy(sizedEngineURL, 0, file, currentPos, sizedEngineURL.length);
            // Increase position for next engine.
            currentPos += 0x80;
        }

        // 1024 (length) / 4 (byte groups needed for checksum) = 256
        // We'll use 255 as to skip the current checksum from, well, being in the checksum.
        Integer loops = 255;
        Integer checksum = 0;
        Integer offset = 0;

        do
        {
            byte[] toWorkWith = new byte[4];
            // Copy 4 bytes at offset toWorkWith.
            System.arraycopy(file, offset, toWorkWith, 0, 4);
            // Convert bytes to big endian int.
            // (yes, it's big endian by default, but maybe the jdk will change some day?)
            Integer checksumAddition = ByteBuffer.wrap(toWorkWith).order(ByteOrder.BIG_ENDIAN).getInt();
            checksum += checksumAddition;
            // Increase offset for next usage.
            offset += 4;
            loops--;
        }
        while(loops > 0);

        // Get lower 32 bits
        Integer finalChecksum = checksum & 0xFFFFFFFF;
        byte[] binaryChecksum = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(finalChecksum).array();
        // Copy 4 byte checksum to end of config
        System.arraycopy(binaryChecksum, 0, file, 1020, 4);

        // We're done :tada:
        return file;
    }
}
