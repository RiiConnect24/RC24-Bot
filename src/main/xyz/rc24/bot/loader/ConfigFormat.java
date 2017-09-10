package xyz.rc24.bot.loader;

import net.dv8tion.jda.core.OnlineStatus;

import java.util.List;

public class ConfigFormat {
    public String token;
    public String prefix;
    public String playing;
    public String primary_owner;
    public String[] secondary_owners;
    public OnlineStatus status;
    public Boolean debug;
    public Long root_server;
    public Boolean patch_mail;
    public Boolean morpher_enabled;
    public Long morpher_server;
    public List<Long> logged_servers;
    public List<Long> ignore_ids;
}
