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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.*;

public class LogicalExpression
{

    public LogicalExpression(String expression)
    {
        result = test(expression);
    }

    public static boolean test(String expression)
    {
        if(expression == null)
            throw new IllegalArgumentException("The logical expression can't be null");
        String arrExpression[] = getLogicalTokens(expression);
        String mod = null;
        boolean r = false;
        boolean last_r = true;
        for(int i = 0; i < arrExpression.length; i++)
            if(arrExpression[i].equals("||") || arrExpression[i].equals("&&"))
            {
                mod = arrExpression[i];
            } else
            {
                r = testExpression(arrExpression[i]);
                if(mod != null)
                    if(mod.equals("&&"))
                        r = last_r && r;
                    else
                    if(mod.equals("||"))
                        r = last_r || r;
                    else
                        throw new IllegalArgumentException((new StringBuilder("Unknown modifier: ")).append(mod).toString());
                last_r = r;
            }

        return r;
    }

    private static boolean testExpression(String expression)
    {
        boolean r;
        try
        {
            if(expression.indexOf("==") > 0)
                r = compare(expression, 0);
            else
            if(expression.indexOf("=") > 0)
                r = compare(expression, 1);
            else
            if(expression.indexOf(">>") > 0)
                r = compare(expression, 3);
            else
                r = compare(expression, 2);
        }
        catch(Exception ex)
        {
            r = false;
        }
        return r;
    }

    private static String[] getLogicalTokens(String expression)
    {
        Vector indeces = new Vector();
        Vector t = new Vector();
        int off = 0;
        int i;
        while((i = expression.indexOf("&&", off)) > -1) 
        {
            off = i + 2;
            indeces.add(new Integer(i));
        }
        while((i = expression.indexOf("||", off)) > -1) 
        {
            off = i + 2;
            indeces.add(new Integer(i));
        }
        int arrIndeces[] = new int[indeces.size()];
        for(int ii = 0; ii < arrIndeces.length; ii++)
            arrIndeces[ii] = ((Integer)indeces.get(ii)).intValue();

        Arrays.sort(arrIndeces);
        int last = 0;
        for(int ii = 0; ii < arrIndeces.length; ii++)
        {
            t.add(expression.substring(last, arrIndeces[ii]));
            t.add(expression.substring(arrIndeces[ii], arrIndeces[ii] + 2));
            last = arrIndeces[ii] + 2;
        }

        String rest = expression.substring(last);
        if(rest.trim().length() > 0)
            t.add(rest);
        return (String[])t.toArray(new String[0]);
    }

    private static boolean compare(String expression, int mode)
    {
        switch(mode)
        {
        case 0: // '\0'
            return compareExact(expression);

        case 1: // '\001'
            return compareMath(expression);

        case 2: // '\002'
            return compareValue(expression);

        case 3: // '\003'
            return compareContains(expression);
        }
        return false;
    }

    private static boolean compareValue(String expression)
    {
    	try
    	{
        return (new MathExpression(expression)).booleanValue();
    	}catch(ParseException ex)
    	{
        return false;}
    }

    private static boolean compareMath(String expression)
    {
        boolean res = true;
        MathExpression lastexp = null;
        StringTokenizer st = new StringTokenizer(expression, "=");
        if(st.countTokens() < 2)
            return false;
        while(st.hasMoreTokens()) 
        {
            MathExpression exp;
            try
            {
                exp = new MathExpression(st.nextToken());
            }
            catch(ParseException ex)
            {
                exp = null;
            }
            if(lastexp != null)
                res = res && lastexp.value() == exp.value();
            lastexp = exp;
        }
        return res;
    }

    private static boolean compareContains(String expression)
    {
        boolean res = true;
        String lasttoken = null;
        StringTokenizer st = new StringTokenizer(expression, ">>");
        if(st.countTokens() < 2)
            return false;
        while(st.hasMoreTokens()) 
        {
            String token = st.nextToken();
            if(lasttoken != null)
                res = res && token.indexOf(lasttoken.trim()) > -1;
            lasttoken = token;
        }
        return res;
    }

    private static boolean compareExact(String expression)
    {
        boolean res = true;
        String lasttoken = null;
        StringTokenizer st = new StringTokenizer(expression, "==");
        if(st.countTokens() < 2)
            return false;
        while(st.hasMoreTokens()) 
        {
            String token = st.nextToken();
            if(lasttoken != null)
                res = res && lasttoken.equals(token);
            lasttoken = token;
        }
        return res;
    }

    public boolean result()
    {
        return result;
    }

    public static void main(String args[])
    {
        LogicalExpression le = new LogicalExpression("Amir==amir&&Amir==Amir||0");
        System.out.println(le.result());
        le = new LogicalExpression("1*5=5");
        System.out.println(le.result());
        le = new LogicalExpression("8-9");
        System.out.println(le.result());
    }

    private static final int COMPARE_EXACT = 0;
    private static final int COMPARE_MATH = 1;
    private static final int COMPARE_VALUE = 2;
    private static final int COMPARE_CONTAINS = 3;
    private boolean result;
}
