/*******************************************************************************
 * Copyright (C) 2009-2011 Amir Hassan <amir@viel-zu.org>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

package org.wooden.util;

import edu.buffalo.fs7.mathlib.*;
import java.io.PrintStream;
import java.text.ParseException;

public class MathExpression
{

    public MathExpression(String expression)
        throws ParseException
    {
        try
        {
            value = Double.parseDouble(Parse.parseExpression(expression).toString());
        }
        catch(IllegalArgumentException ex)
        {
            StringBuffer sb = new StringBuffer();
            String arrMessages[] = LOG.getMessages();
            for(int i = 0; i < arrMessages.length; i++)
            {
                int index = arrMessages[i].indexOf('^') - arrMessages[i].indexOf(";\n") - 2;
                arrMessages[i] = arrMessages[i].substring(0, arrMessages[i].indexOf(";\n"));
                arrMessages[i] = arrMessages[i].replace('\n', ' ');
                sb.append(arrMessages[i]).append((new StringBuilder("; at:")).append(index).toString());
            }

            throw new ParseException(sb.toString(), 0);
        }
    }

    public boolean booleanValue()
    {
        return value > 0.0D;
    }

    public double value()
    {
        return value;
    }

    public static void printUsage()
    {
        String newLine = System.getProperty("line.separator", "\n");
        char tab = '\t';
        StringBuffer usage = (new StringBuffer("Usage: MathExpression [expression] (optional:) [float]")).append(newLine).append(tab).append("expression").append(tab).append("A valid arithmetilk expression").append(newLine).append(tab).append("float").append(tab).append(tab).append("A boolean flag. Set true to return a floating point number");
        System.err.println(usage);
    }

    public static void main(String args[])
    {
        try
        {
            boolean returnFloat = false;
            if(args.length > 2)
            {
                System.err.println("ERROR: Wrong number of arguments!");
                printUsage();
                System.exit(1);
            } else
            if(args.length == 2)
                try
                {
                    returnFloat = Boolean.getBoolean(args[1]);
                }
                catch(Exception ex)
                {
                    System.err.println("ERROR: Illegal boolean flag");
                    printUsage();
                    System.exit(1);
                }
            if(returnFloat)
                System.out.println((new MathExpression(args[0])).value());
            else
                System.out.println((int)(new MathExpression(args[0])).value());
        }
        catch(ParseException ex1)
        {
            System.err.println(ex1.getMessage());
            printUsage();
        }
        catch(Exception ex1)
        {
            ex1.printStackTrace();
        }
    }

    private double value;
}
