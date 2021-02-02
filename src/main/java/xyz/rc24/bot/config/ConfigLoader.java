package xyz.rc24.bot.config;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.bot.Bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static xyz.rc24.bot.RiiConnect24Bot.getLogger;

public class ConfigLoader
{
    public static Config init()
    {
        Yaml yaml = new Yaml();
        File file = new File("config.yml");

        if(!(file.exists()))
        {
            try(InputStream is = Bot.class.getResourceAsStream("/config.yml"))
            {
                Files.copy(is, file.toPath());
                getLogger().info("The config file has been generated, please edit it.");
                System.exit(0);
            }
            catch(IOException e)
            {
                throw new RuntimeException("Failed to copy config file:", e);
            }
        }

        try(InputStream is = new FileInputStream(file))
        {
            return yaml.loadAs(is, Config.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to load config file:", e);
        }
    }
}
