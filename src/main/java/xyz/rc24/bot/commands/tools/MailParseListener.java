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

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.IOUtil;
import okhttp3.*;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.loader.Config;

import java.io.*;

/**
 * @author Spotlight - original patching code
 * @author Artuto - updated patching code (wrapper)
 */

public class MailParseListener extends ListenerAdapter
{
    private static final Logger logger = (Logger)LoggerFactory.getLogger(MailParseListener.class);
    private final Config config;

    public MailParseListener(Config config)
    {
        this.config = config;
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if(!(config.isMailPatchEnabled())) return;

        Message message = event.getMessage();
        // Make sure we're not patching our own uploaded file again.
        if(!(message.getAuthor().isBot()))
        {
            for(Message.Attachment att : message.getAttachments())
            {
                // nwc24msg.cfg and nwc24msg.cbk are meant to be covered.
                if(att.getFileName().contains("nwc24msg.c"))
                {
                    // Let's begin! :D
                    try
                    {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("uploaded_config",
                                null, RequestBody.create(MediaType.parse("application/octet-stream"), IOUtil.readFully(att.getInputStream()))).build();
                        Request request = new Request.Builder().url(Const.PATCHING_URL).build();
                        Response response = client.newCall(request).execute();

                        if(response.code()==400)
                            throw new IOException("Invalid file! Make sure you sent the correct file!");
                        if(response.code()==503)
                            throw new IOException("The server is now currently under maintenance. Please wait some time and try again.");
                        if(!(response.isSuccessful()))
                            throw new IOException();

                        String content = response.body().string();
                        Writer output = new BufferedWriter(new FileWriter("nwc24msg.cfg", true));
                        output.append(content).close();
                        File file = new File("nwc24msg.cfg");

                        event.getChannel().sendFile(file, att.getFileName(),
                                new MessageBuilder().append("Here's your patched mail file, deleted from our server:").build()).queue(s -> file.delete(), e -> file.delete());
                    }
                    catch(IOException e)
                    {
                        if(e.getMessage()==null)
                        {
                            event.getChannel().sendMessage(Const.FAIL_E+" Uh oh, I messed up and couldn't patch. Please ask one of my owners to check the console.").queue();
                            e.printStackTrace();
                        }
                        else event.getChannel().sendMessage(Const.FAIL_E+" "+e.getMessage()).queue();
                    }

                    /*try
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
                    }*/
                }
            }
        }
    }
}
