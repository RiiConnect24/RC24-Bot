package xyz.rc24.bot.managers;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.bot.core.entities.Poll;
import xyz.rc24.bot.core.entities.impl.PollImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PollManager
{
    private List<Map<String, Object>> list;
    private List<Poll> polls;
    private Random random;

    public PollManager() throws IOException
    {
        File file = new File("polls.yml");

        if(!(file.exists()))
            downloadFile();

        Yaml yaml = new Yaml();
        this.list = yaml.load(new FileReader(file));
        this.polls = new ArrayList<>();

        populateList();

        this.random = new Random();
    }

    private void downloadFile() throws IOException
    {
        URL url = new URL("https://raw.githubusercontent.com/RiiConnect24/Site/master/_data/votes/results_049.yml");
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream("polls.yml");
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    public Poll getRandomPoll()
    {
        return polls.get(random.nextInt(polls.size()));
    }

    @SuppressWarnings("unchecked")
    private void populateList()
    {
        for(Map<String, Object> data : list)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new PollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }

        // Cleanup list we won't need anymore
        list.clear();
        this.list = null;

        // Free up memory after this mess
        System.gc();
    }

    @SuppressWarnings("unchecked")
    private String getPart(Map<String, Object> poll, String key)
    {
        return (String) ((Map<String, Object>) poll.get(key)).get("english");
    }
}
