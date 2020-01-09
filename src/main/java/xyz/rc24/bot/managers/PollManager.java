/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.managers;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.bot.core.entities.Poll;
import xyz.rc24.bot.core.entities.impl.UKPollImpl;
import xyz.rc24.bot.core.entities.impl.USPollImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PollManager
{
    private Set<Long> current;
    private List<Poll> polls;
    private Random random;

    public PollManager() throws IOException
    {
        File usFile = new File("polls_049.yml");
        File ukFile = new File("polls_110.yml");

        if(!(usFile.exists()))
            downloadFile("https://raw.githubusercontent.com/RiiConnect24/Site/master/_data/votes/results_049.yml", usFile);

        if(!(ukFile.exists()))
            downloadFile("https://raw.githubusercontent.com/RiiConnect24/Site/master/_data/votes/results_110.yml", ukFile);

        Yaml yaml = new Yaml();
        this.polls = new ArrayList<>();
        this.current = new HashSet<>();

        populateList(yaml.load(new FileReader(usFile)), yaml.load(new FileReader(ukFile)));

        this.random = new Random();

        // Free up memory after this mess
        System.gc();
    }

    public Poll getRandomPoll()
    {
        return polls.get(random.nextInt(polls.size()));
    }

    public void trackId(long id)
    {
        current.add(id);
    }

    public boolean isTracked(long id)
    {
        return current.contains(id);
    }

    private void downloadFile(String downloadUrl, File file) throws IOException
    {
        URL url = new URL(downloadUrl);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    private void populateList(List<Map<String, Object>> us, List<Map<String, Object>> uk)
    {
        for(Map<String, Object> data : us)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new USPollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }

        for(Map<String, Object> data : uk)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new UKPollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }
    }

    @SuppressWarnings("unchecked")
    private String getPart(Map<String, Object> poll, String key)
    {
        return (String) ((Map<String, Object>) poll.get(key)).get("english");
    }
}
