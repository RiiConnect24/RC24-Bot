package xyz.rc24.bot.core.entities.impl;

public class USPollImpl extends PollImpl
{
    public USPollImpl(String question, String response1, String response2)
    {
        super(question, response1, response2);
    }

    @Override
    public String getCountryFlag()
    {
        return "\uD83C\uDDFA\uD83C\uDDF8";
    }
}
