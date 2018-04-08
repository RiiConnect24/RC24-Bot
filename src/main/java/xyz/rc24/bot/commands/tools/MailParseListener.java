package xyz.rc24.bot.commands.tools;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.events.MailParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Spotlight
 */

public class MailParseListener extends ListenerAdapter
{
    private static final Logger logger = (Logger)LoggerFactory.getLogger(MailParseListener.class);

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        Message message = event.getMessage();
        // Make sure we're not patching our own uploaded file again.
        if(!(message.getAuthor().isBot()))
        {
            for(Message.Attachment test : message.getAttachments())
            {
                // nwc24msg.cfg and nwc24msg.cbk are meant to be covered.
                if(test.getFileName().contains("nwc24msg.c"))
                {
                    // Let's begin! :D
                    try
                    {
                        String url = test.getUrl();
                        logger.debug("Downloaded from: " + url);
                        // Thanks, Discord, for requiring a user agent.
                        URLConnection connection = new URL(url).openConnection();
                        connection.addRequestProperty("User-Agent", "RiiConnect24/2.0.3.1");
                        InputStream inputStream = connection.getInputStream();
                        byte[] file = new MailParser().patchMail(inputStream);
                        if(file.length == 1)
                        {
                            // Uh oh, something failed.
                            // Error is set as the first byte.

                            // An enum would normally be used for this, but we can't
                            // as they're not constants.
                            // 0x21 - File size incorrect
                            // 0x69 - File magic incorrect
                            switch(file[0])
                            {
                                case 0x21:
                                    event.getChannel().sendMessage("Hm, that file doesn't seem the right size. Are you sure it's right?").queue();
                                    break;
                                case 0x69:
                                    event.getChannel().sendMessage("That doesn't look like the right file type. Are you sure it's right?").queue();
                                    break;
                                default:
                                    event.getChannel().sendMessage("Uh oh, something went wrong. Are you sure you're using the right file?").queue();
                                    break;
                            }
                            return;
                        }

                        // Upload patched file with caption
                        event.getChannel().sendFile(file, test.getFileName(),
                                new MessageBuilder().append("Here's your patched mail file, deleted from our server:").build()).queue();
                    }
                    catch(IOException e)
                    {
                        event.getChannel().sendMessage("Uh oh, I messed up and couldn't patch. Please ask one of my owners to check the console.").queue();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
