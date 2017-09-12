package xyz.rc24.bot.utils;

/**
 * Think of it as the old Ruby yaml helper, but 1000x less painful.
 */
public class RoleManager {
    public enum Type {
        OWNER("Dummy Entry", false),
        DEVELOPER("RiiConnect24 Developers", true),
        BOT_HELPERS("Bot Helpers", false),
        MODERATORS("Server Moderators", true),
        HELPERS("Helpers", false),
        DONATORS("Donators", false),
        ADMINS("Server Administrators", false),
        TRANSLATORS("Translators", false);

        private final String roleName;
        private final Boolean shouldAlert;
        Type(String roleName, Boolean shouldAlert) {
            this.roleName = roleName;
            this.shouldAlert = shouldAlert;
        }
    }

}
