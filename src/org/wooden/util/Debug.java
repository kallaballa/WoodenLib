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

public class Debug {
  public static void createInstance(int threadPad, int classPad, int msgPad) {
    Debug.instance = new Debug(threadPad, classPad, msgPad);
  }

  public static void debug(String s) {
    System.err
        .println("\t# " + instance.createID() + '\t' + instance.pad(2, s));
  }

  public static void log(String s) {
    System.out.println(s);
  }

  private String[] colpadding = new String[3];

  private static Debug instance;

  static {
    createInstance(15, 15, 20);
  }

  private Debug(int threadPad, int classPad, int msgPad) {
    this.colpadding[0] = this.padding(threadPad);
    this.colpadding[1] = this.padding(classPad);
    this.colpadding[2] = this.padding(msgPad);
  }

  private String createID() {
    try {
      throw new RuntimeException();
    } catch (Exception e) {
      StackTraceElement caller = e.getStackTrace()[2];
      String threadName = Thread.currentThread().getName();
      String className = caller.getClassName();

      return this.pad(0, threadName) + '\t' + this.pad(1, className);
    }
  }

  private String pad(int col, String s) {
    String p = this.colpadding[col];
    int sLen = s.length();
    int pLen = p.length();
    if (sLen < pLen) {
      return s + p.substring(sLen);
    } else {
      return s.substring(0, pLen);
    }
  }

  private String padding(int len) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++)
      sb.append(' ');

    return sb.toString();
  }
}
