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

package org.wooden.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class HTMLTextWriter extends BufferedWriter {

  private static StringBuffer appendConverted(StringBuffer sb, char arrC[]) {
    for (char element : arrC)
      sb = appendConverted(sb, element);

    return sb;
  }

  private static StringBuffer appendConverted(StringBuffer sb, char c) {
    int code = c;
    if (code != 13 && code < 32 && code > 126) {
      System.out.println((new StringBuilder(String.valueOf(code))).append(":")
          .append(c).toString());
      if (c == '\n')
        sb.append("<br>");
      else
        sb.append("&#").append(c).append(';');
    } else {
      sb.append(c);
    }
    return sb;
  }

  public static StringBuffer convert(char arrC[]) {
    return appendConverted(new StringBuffer(), arrC);
  }

  public static StringBuffer convert(String s) {
    return convert(s.toCharArray());
  }

  public static StringBuffer convert(StringBuffer sb) {
    return convert(sb.toString().toCharArray());
  }

  public HTMLTextWriter(OutputStream out) throws IOException {
    super(new OutputStreamWriter(out));
  }

  public void writeHTML(char arrC[]) throws IOException {
    this.writeHTML(arrC, 0, arrC.length);
  }

  public void writeHTML(char c) throws IOException {
    this.writeHTML(new char[] { c }, 0, 1);
  }

  public void writeHTML(char arrC[], int index, int len) throws IOException {
    super.write(appendConverted(new StringBuffer(), arrC).toString());
  }
}
