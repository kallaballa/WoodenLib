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

public class BooleanExpression
{

    public BooleanExpression(String expression)
        throws ParseException
    {
        booleanValue = false;
        rawExpression = expression;
        booleanValue = parseExpression(expression);
    }

    private boolean parseExpression(String expression)
        throws ParseException
    {
        if(expression == null)
            throw new IllegalArgumentException("The boolean expression can't be null");
        String arrExpression[] = getBooleanTokens(expression);
        String exp = null;
        String operator = null;
        Boolean result = null;
        Boolean lastResult = null;
        Boolean paritialResult = null;
        for(int i = 0; i < arrExpression.length; i++)
        {
            exp = arrExpression[i];
            if(exp.equals("||") || exp.equals("&&"))
            {
                operator = exp;
            } else
            {
                paritialResult = new Boolean(resolveValue(exp));
                if(lastResult == null || operator == null)
                {
                    result = lastResult = paritialResult;
                } else
                {
                    if(operator.equals("&&"))
                        result = and(lastResult, paritialResult);
                    else
                    if(operator.equals("||"))
                        result = or(lastResult, paritialResult);
                    else
                        throw new ParseException((new StringBuilder("Unknown operator: ")).append(operator).toString(), rawExpression.indexOf(operator));
                    lastResult = result;
                }
            }
        }

        return result.booleanValue();
    }

    private Boolean and(Boolean b1, Boolean b2)
    {
        return new Boolean(b1.booleanValue() && b2.booleanValue());
    }

    private Boolean or(Boolean b1, Boolean b2)
    {
        return new Boolean(b1.booleanValue() || b2.booleanValue());
    }

    private boolean resolveValue(String expression)
        throws ParseException
    {
        boolean r = false;
        if(expression.indexOf("==") > 0)
            r = resolveValue(expression, 0);
        else
        if(expression.indexOf("=") > 0)
            r = resolveValue(expression, 1);
        else
        if(expression.indexOf(">>") > 0)
            r = resolveValue(expression, 3);
        else
            r = resolveValue(expression, 2);
        return r;
    }

    private Collection findOperator(String expression, String operator)
        throws ParseException
    {
        Vector indeces = new Vector();
        int operatorOff = 0;

        try
        {
        int operatorIndex;
        while((operatorIndex = expression.indexOf(operator, operatorOff)) > -1) 
        {
            operatorOff = operatorIndex + 2;
            indeces.add(new Integer(operatorIndex));
        }
        return indeces;
        }
        catch(Exception ex)
        {
        	throw new ParseException(ex.getMessage(), operatorOff);
        }
    }

    private String[] getBooleanTokens(String expression)
        throws ParseException
    {
        Vector indeces = new Vector();
        Vector t = new Vector();
        int operatorOff = 0;
        try
        {
        indeces.addAll(findOperator(expression, "&&"));
        indeces.addAll(findOperator(expression, "||"));
        int arrOperatorIndeces[] = new int[indeces.size()];
        for(int i = 0; i < arrOperatorIndeces.length; i++)
            arrOperatorIndeces[i] = ((Integer)indeces.get(i)).intValue();

        Arrays.sort(arrOperatorIndeces);
        operatorOff = 0;
        for(int i = 0; i < arrOperatorIndeces.length; i++)
        {
            t.add(expression.substring(operatorOff, arrOperatorIndeces[i]));
            t.add(expression.substring(arrOperatorIndeces[i], arrOperatorIndeces[i] + 2));
            operatorOff = arrOperatorIndeces[i] + 2;
        }

        String rest = expression.substring(operatorOff);
        if(rest.trim().length() > 0)
            t.add(rest);
        return (String[])t.toArray(new String[0]);
        }
        	catch(ParseException ex)
        {
        throw ex;
        }
        catch(Exception ex)
        {
        	throw new ParseException(ex.getMessage(), operatorOff);
        }
    }

    private boolean resolveValue(String expression, int mode)
        throws ParseException
    {
        boolean result = false;
        switch(mode)
        {
        case 0: // '\0'
            result = resolveExact(expression);
            break;

        case 1: // '\001'
            result = resolveEquals(expression);
            break;

        case 2: // '\002'
            result = resolveMath(expression);
            break;

        case 3: // '\003'
            result = resolveContains(expression);
            break;
        }
        return result;
    }

    private boolean resolveMath(String expression)
        throws ParseException
    {
        try
        {
    	return (new MathExpression(expression)).booleanValue();
        }
        catch(Exception ex)
        {
        	throw new ParseException(ex.getMessage(), rawExpression.indexOf(expression));
        }
    }

    private boolean resolveEquals(String expression)
        throws ParseException
    {
        boolean res;
        MathExpression lastexp;
        res = true;
        lastexp = null;
        try
        {

        StringTokenizer st = new StringTokenizer(expression, "=");
        if(st.countTokens() < 2)
            return false;
        while(st.hasMoreTokens()) 
        {
            MathExpression exp = new MathExpression(st.nextToken());
            if(lastexp != null)
                res = res && lastexp.value() == exp.value();
            lastexp = exp;
        }
        return res;
        }
        catch(Exception ex)
        {
        	throw new ParseException(ex.getMessage(), rawExpression.indexOf(expression));
        }
    }

    private boolean resolveContains(String expression)
        throws ParseException
    {
        boolean res;
        String lasttoken;
        res = true;
        lasttoken = null;
        try
        {
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
    catch(Exception ex)
    {
    	throw new ParseException(ex.getMessage(), rawExpression.indexOf(expression));
    }
    }

    private boolean resolveExact(String expression)
        throws ParseException
    {
        boolean res;
        String lasttoken;
        res = true;
        lasttoken = null;
        try
        {
        	StringTokenizer st = new StringTokenizer(expression, "==");
        if(st.countTokens() < 2)
            return false;
        while(st.hasMoreTokens()) 
        {
            String token = st.nextToken().trim();
            if(lasttoken != null)
                res = res && lasttoken.equals(token);
            lasttoken = token;
        }
        return res;
    	}
        catch(Exception ex)
        {
        	throw new ParseException(ex.getMessage(), rawExpression.indexOf(expression));
        }
    }

    public boolean booleanValue()
    {
        return booleanValue;
    }

    public static void main(String args[])
    {
        try
        {
            BooleanExpression le = new BooleanExpression("Amir==Amir && (10*a)+24=104 && AMIR >> hasAMIRsan || 0");
            System.out.println((new StringBuilder(String.valueOf(le.booleanValue()))).append("\n").toString());
            le = new BooleanExpression("1*5=5");
            System.out.println((new StringBuilder(String.valueOf(le.booleanValue()))).append("\n").toString());
            le = new BooleanExpression("8-9");
            System.out.println((new StringBuilder(String.valueOf(le.booleanValue()))).append("\n").toString());
        }
        catch(ParseException ex)
        {
            ex.printStackTrace();
        }
    }

    private static final int RESOLVE_EXACT = 0;
    private static final int RESOLVE_EQUALS = 1;
    private static final int RESOLVE_MATH = 2;
    private static final int RESOLVE_CONTAINS = 3;
    private static final String OPERATOR_EXACT = "==";
    private static final String OPERATOR_EQUALS = "=";
    private static final String OPERATOR_CONTAINS = ">>";
    private static final String OPERATOR_AND = "&&";
    private static final String OPERATOR_OR = "||";
    private boolean booleanValue;
    private String rawExpression;
}
