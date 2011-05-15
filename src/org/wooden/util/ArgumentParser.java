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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

public class ArgumentParser extends Configuration {

  public ArgumentParser() {}

  public ArgumentParser(Configuration defaults) {
    super(defaults);
  }

  public ArgumentParser(String args[]) {
    this.readParameters(args);
  }

  public String getParameter(String name) {
    return (String) this.get(name.toLowerCase());
  }

  public String[] getValueArray() {
    Vector vecArguments = new Vector();
    for (Enumeration enumValues = this.elements(); enumValues.hasMoreElements(); vecArguments
        .addElement(enumValues.nextElement()))
      ;
    String arrArguments[] = new String[vecArguments.size()];
    for (int i = 0; i < arrArguments.length; i++)
      arrArguments[i] = (String) vecArguments.elementAt(i);

    return arrArguments;
  }

  public void printValues(OutputStream out) {
    this.printValues(new PrintStream(out));
  }

  public void printValues(PrintStream out) {
    String arrValues[] = this.getValueArray();
    for (String arrValue : arrValues)
      out.println(arrValue);

  }

  private void putParam(String name, String value) {
    this.put(name.toLowerCase(), value);
  }

  public void readParameters(String args[]) {
    String strParam = null;
    for (int iArgs = 0; iArgs < args.length; iArgs++)
      if (args[iArgs] != null) {
        String strArg = args[iArgs].trim();
        if (strArg.startsWith("-")) {
          strArg = strArg.substring(1);
          if (iArgs < args.length - 1 && args[iArgs + 1].startsWith("-")) {
            this.putParam(strArg, " ");
            strParam = null;
          } else if (args.length == iArgs + 1)
            this.putParam(strArg, " ");
          else
            strParam = strArg;
        } else if (strParam != null) {
          this.putParam(strParam, strArg);
          strParam = null;
        }
      }

  }
}
