package xyz.rc24.bot.loader;

import net.dv8tion.jda.core.OnlineStatus;

import java.util.List;

// We have to keep this public due to Jackson.
@SuppressWarnings("WeakerAccess")
public class ConfigFormat {
    public String token;
    public String prefix;
    public String playing;
    public Long primary_owner;
    public Long[] secondary_owners;
    public OnlineStatus status;
    public Boolean debug;
    public Long root_server;
    public Boolean patch_mail;
    public Boolean morpher_enabled;
    public Long morpher_root;
    public Long morpher_mirror;
    public List<Long> logged_servers;
    public List<Long> ignore_ids;
}
