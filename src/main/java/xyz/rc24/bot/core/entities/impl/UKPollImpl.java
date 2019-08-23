package xyz.rc24.bot.core.entities.impl;

public class UKPollImpl extends PollImpl
{
    public UKPollImpl(String question, String response1, String response2)
    {
        super(question, response1, response2);
    }

    @Override
    public String getCountryFlag()
    {
        return "\uD83C\uDDEC\uD83C\uDDE7";
    }
}
