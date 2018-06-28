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
        if(!(file.exists()))
            return;
        lines.remove(id);
        if(lines.isEmpty())
        {
            file.delete();
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Writer w = new FileWriter(fileName, true);
        while(!((currentLine = reader.readLine())==null))
        {
            if(currentLine.trim().equals(id))
                continue;
            w.append(id);
        }
        w.close();
        reader.close();
        tempFile.renameTo(file);
    }

    public void updateList() throws IOException
    {
        File file = new File(fileName);
        if(file.exists())
            this.lines = Files.readAllLines(Paths.get(fileName));
        else
            lines.clear();
    }
}
