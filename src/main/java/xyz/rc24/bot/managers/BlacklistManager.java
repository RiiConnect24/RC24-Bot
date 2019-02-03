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

package xyz.rc24.bot.managers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Artuto
 */

public class BlacklistManager
{
    private List<String> lines;
    private final String fileName = "blacklist.txt";

    public BlacklistManager() throws IOException
    {
        this.lines = Files.readAllLines(Paths.get(fileName));
    }

    public boolean isBlacklisted(String id)
    {
        return lines.contains(id);
    }

    public void addBlacklist(String id) throws IOException
    {
        Writer w = new FileWriter(fileName, true);
        w.append(id).close();
        lines.add(id);
    }

    public void removeBlacklist(String id) throws IOException
    {
        File file = new File(fileName);
        File tempFile = new File("tempBl.txt");
        String currentLine;
        if(! (file.exists())) return;
        lines.remove(id);
        if(lines.isEmpty())
        {
            file.delete();
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Writer w = new FileWriter(fileName, true);
        while(! ((currentLine = reader.readLine()) == null))
        {
            if(currentLine.trim().equals(id)) continue;
            w.append(id);
        }
        w.close();
        reader.close();
        tempFile.renameTo(file);
    }

    public void updateList() throws IOException
    {
        File file = new File(fileName);
        if(file.exists()) this.lines = Files.readAllLines(Paths.get(fileName));
        else lines.clear();
    }
}
