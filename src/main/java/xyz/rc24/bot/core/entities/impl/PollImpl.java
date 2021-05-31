/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.core.entities.impl;

import xyz.rc24.bot.core.entities.Poll;

public abstract class PollImpl implements Poll
{
    private String question, response1, response2;

    PollImpl(String question, String response1, String response2)
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
    public abstract String getCountryFlag();

    @Override
    public String toString()
    {
        return "Poll(" + getQuestion() + ")";
    }
}
