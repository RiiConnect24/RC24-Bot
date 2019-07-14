package xyz.rc24.bot.core.entities.impl;

import xyz.rc24.bot.core.entities.Poll;

public class PollImpl implements Poll
{
    private String question, response1, response2;

    public PollImpl(String question, String response1, String response2)
    {
        this.question = question;
        this.response1 = response1;
        this.response2 = response2;
    }

    @Override
    public String getQuestion()
    {
        return question;
    }

    @Override
    public String getResponse1()
    {
        return response1;
    }

    @Override
    public String getResponse2()
    {
        return response2;
    }

    @Override
    public String toString()
    {
        return "Poll(" + getQuestion() + ")";
    }
}
