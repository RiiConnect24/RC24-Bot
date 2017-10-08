package xyz.rc24.bot.commands.tools;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class MailParseListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MailParseListener.class);

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        Message message = event.getMessage();
        // Make sure we're not patching our own uploaded file again.
        if (!message.getAuthor().isBot()) {
            for (Message.Attachment test : message.getAttachments()) {
                // nwc24msg.cfg and nwc24msg.cbk are meant to be covered.
                if (test.getFileName().contains("nwc24msg.c")) {
                    // Let's begin! :D
                    try {
                        String url = test.getUrl();
                        logger.debug("Downloaded from: " + url);
                        // Thanks, Discord, for requiring a user agent.
                        URLConnection connection = new URL(url).openConnection();
                        connection.addRequestProperty("User-Agent", "RiiConnect24/2.0.3.1");
                        InputStream inputStream = connection.getInputStream();
                        // Yes, I used commons-io just for this.
                        // I'm sorry.
                        byte[] file = IOUtils.toByteArray(inputStream);
                        inputStream.close();

                        /*
                         * The following is based off a specific format.
                         * The developer writing this has tried to comment
                         * appropriately given the current task.
                         *
                         * @see https://github.com/RiiConnect24/Kaitai-Files/blob/master/Kaitais/nwc24msg.ksy
                         */


                        System.out.println(DatatypeConverter.printHexBinary(file));

                        // Simple file checks
                        String invalidString = "Hm, that doesn't look like a valid `nwc24msg.cfg`.";

                        logger.debug("Length: " + file.length);
                        if (!(file.length == 1024)) {
                            event.getChannel().sendMessage(invalidString).complete();
                            return;
                        }

                        byte[] expectedMagic = "WcCf".getBytes();
                        byte[] presentMagic = Arrays.copyOfRange(file, 0, 4);
                        logger.debug("Expected magic: " + DatatypeConverter.printHexBinary(expectedMagic));
                        logger.debug("Magic from file: " + DatatypeConverter.printHexBinary(presentMagic));
                        if (!Arrays.equals(presentMagic, expectedMagic)) {
                            event.getChannel().sendMessage(invalidString).complete();
                            return;
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
                        for (String type : engineTypes) {
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

                        // Idek can I just dump this
                        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(file));

                    } catch (IOException e) {
                        event.getChannel().sendMessage("Uh oh, I messed up and couldn't patch. Please ask one of my owners to check the console.").complete();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
