package xyz.rc24.bot.commands.tools;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.events.MailParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
                        byte[] file = new MailParser().patchMail(inputStream);
                        if (file.length == 1) {
                            // Uh oh, something failed.
                            // Error is set as the first byte.

                            // An enum would normally be used for this, but we can't
                            // as they're not constants.
                            // 0x21 - File size incorrect
                            // 0x69 - File magic incorrect
                            switch (file[0]) {
                                case 0x21:
                                    event.getChannel().sendMessage("Hm, that file doesn't seem the right size. Are you sure it's right?").complete();
                                    break;
                                case 0x69:
                                    event.getChannel().sendMessage("That doesn't look like the right file type. Are you sure it's right?").complete();
                                    break;
                                default:
                                    event.getChannel().sendMessage("Uh oh, something went wrong. Are you sure you're using the right file?").complete();
                                    break;
                            }
                            return;
                        }

                        // Upload patched file with caption
                        event.getChannel().sendFile(file, test.getFileName(),
                                new MessageBuilder().append("Here's your patched mail file, deleted from our server:").build()).complete();
                    } catch (IOException e) {
                        event.getChannel().sendMessage("Uh oh, I messed up and couldn't patch. Please ask one of my owners to check the console.").complete();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
