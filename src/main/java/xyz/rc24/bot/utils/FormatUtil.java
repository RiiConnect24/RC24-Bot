/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
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

package xyz.rc24.bot.utils;

import xyz.rc24.bot.core.entities.CodeType;

import java.util.Map;

/**
 * @author Artuto
 */

public class FormatUtil
{

    public static String getCodeTypes()
    {
        StringBuilder response = new StringBuilder("Invalid type! Valid types:\n");
        for(CodeType type : CodeType.values())
        {
            if(type == CodeType.UNKNOWN)
                continue;

            response.append("`").append(type.getName()).append("`, ");
        }

        // Remove leftover comma + space
        return response.substring(0, response.length() - 2);
    }

    public static String getCodeLayout(Map<String, String> codes)
    {
        // Create a human-readable format of the user's Wii wii.
        StringBuilder codesString = new StringBuilder();
        for(Map.Entry<String, String> code : codes.entrySet())
            codesString.append("`").append(code.getKey()).append("`:\n").append(code.getValue()).append("\n");

        return codesString.toString();
    }

}
