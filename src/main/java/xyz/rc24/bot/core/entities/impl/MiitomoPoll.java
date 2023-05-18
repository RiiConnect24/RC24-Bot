package xyz.rc24.bot.core.entities.impl;

public class MiitomoPoll extends PollImpl
{
    public MiitomoPoll(String question)
    {
        super(question, null, null);
    }

    @Override
    public String getResponse1()
    {
        throw new UnsupportedOperationException("Miitomo polls don't have responses!");
    }

    @Override
    public String getResponse2()
    {
        throw new UnsupportedOperationException("Miitomo polls don't have responses!");
    }

    @Override
    public String getCountryFlag()
    {
        throw new UnsupportedOperationException("Miitomo polls don't have country flag!");
    }
}
