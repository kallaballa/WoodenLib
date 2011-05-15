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
import java.util.HashMap;

public class OperationTimer {

  public static final int MILLI_SECONDS = 1;

  public static final int SECONDS = 1000;

  public static final int MINUTES = 60000;

  public static double timeUnit = 1.0D;

  private static final HashMap timesMap = new HashMap();

  private static PrintStream out;
  static {
    out = System.out;
  }

  public static final synchronized void operationEnd(String s) {}

  public static final synchronized void operationStart(String s) {}

  public static final synchronized void setPrintStream(PrintStream out) {
    out = out;
  }

  public static final synchronized void setTimeUnit(int timeUnit) {
    timeUnit = timeUnit;
  }

  public OperationTimer() {}
}
