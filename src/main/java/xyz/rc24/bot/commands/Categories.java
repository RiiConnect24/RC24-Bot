package xyz.rc24.bot.commands;

import com.jagrosh.jdautilities.commandclient.Command.Category;

public class Categories {

    public static final Category ADMIN = new Category("Bot Administration", event -> {
        if (event.isOwner() || event.isCoOwner())
            return true;
        else {
            event.replyError("You don't have access to this command!");
            return false;
        }
    });

    public static final Category TOOLS = new Category("Tools");
    public static final Category WII = new Category("Wii-related");
}