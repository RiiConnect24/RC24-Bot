package xyz.rc24.bot.core.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Flag
{
    // North America
    MEXICO("\uD83C\uDDF2\uD83C\uDDFD", "Mexico"), // best country
    USA("\uD83C\uDDFA\uD83C\uDDF8", "United States"),
    CANADA("\uD83C\uDDE8\uD83C\uDDE6", "Canada"),

    // South America
    COLOMBIA("\uD83C\uDDE8\uD83C\uDDF4", "Colombia"),
    ARGENTINA("\uD83C\uDDE6\uD83C\uDDF7", "Argentina"),
    VENEZUELA("\uD83C\uDDFB\uD83C\uDDEA", "Venezuela"),
    BRAZIL("\uD83C\uDDE7\uD83C\uDDF7", "Brazil"),
    CHILE("\uD83C\uDDE8\uD83C\uDDF1", "Chile"),
    PERU("\uD83C\uDDF5\uD83C\uDDEA", "Peru"),
    URUGUAY("\uD83C\uDDFA\uD83C\uDDFE", "Uruguay"),
    PARAGUAY("\uD83C\uDDF5\uD83C\uDDE6", "Paraguay"),
    ECUADOR("\uD83C\uDDEA\uD83C\uDDE8", "Ecuador"),

    // Europe
    FRANCE("\uD83C\uDDEB\uD83C\uDDF7", "France"),
    SPAIN("\uD83C\uDDEA\uD83C\uDDF8", "Spain"),
    GERMANY("\uD83C\uDDE9\uD83C\uDDEA", "Germany"),
    POLAND("\uD83C\uDDF5\uD83C\uDDF1", "Poland"),
    ITALY("\uD83C\uDDEE\uD83C\uDDF9", "Italy"),
    UK("\uD83C\uDDEC\uD83C\uDDE7", "United Kingdom"),
    SWITZERLAND("\uD83C\uDDE8\uD83C\uDDED", "Switzerland"),
    NETHERLANDS("\uD83C\uDDF3\uD83C\uDDF1", "Netherlands"),
    NORWAY("\uD83C\uDDF3\uD83C\uDDF4", "Norway"),
    SWEDEN("\uD83C\uDDF8\uD83C\uDDEA", "Sweden"),
    IRELAND("\uD83C\uDDEE\uD83C\uDDEA", "Ireland"),
    RUSSIA("\uD83C\uDDF7\uD83C\uDDFA", "Russia"),
    TURKEY("\uD83C\uDDF9\uD83C\uDDF7", "Turkey"),
    GREECE("\uD83C\uDDEC\uD83C\uDDF7", "Greece"),

    // Asia
    JAPAN("\uD83C\uDDEF\uD83C\uDDF5", "Japan"),
    CHINA("\uD83C\uDDE8\uD83C\uDDF3", "China"),
    KOREA("\uD83C\uDDF0\uD83C\uDDF7", "Korea"),
    ISRAEL("\uD83C\uDDEE\uD83C\uDDF1", "Israel"),

    // Nowhere
    AUSTRALIA("\uD83C\uDDE6\uD83C\uDDFA", "Australia"),
    NEW_ZEALAND("\uD83C\uDDF3\uD83C\uDDFF", "New Zealand"),

    UNKNOWN(null, null);

    private final String emote, name;

    Flag(String emote, String name)
    {
        this.emote = emote;
        this.name = name;
    }

    @Nullable
    public String getEmote()
    {
        return emote;
    }

    @Nullable
    public String getName()
    {
        return name;
    }

    @NotNull
    public static Flag fromName(@NotNull String name)
    {
        for(Flag flag : values())
        {
            if(!(flag.getName() == null) && name.toLowerCase().equals(flag.getName().toLowerCase()))
                return flag;
        }

        return UNKNOWN;
    }
}
