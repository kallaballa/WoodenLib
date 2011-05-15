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

import java.util.StringTokenizer;

public class PathPattern {

  public static PathPattern compile(String pattern) {
    StringTokenizer p = new StringTokenizer(pattern, "*", true);
    String patternTokens[] = new String[p.countTokens()];
    for (int i = 0; i < patternTokens.length; i++)
      patternTokens[i] = p.nextToken();

    return new PathPattern(patternTokens);
  }

  private String tokens[];

  private static final char wildcard = 42;

  private PathPattern(String patternTokens[]) {
    this.tokens = patternTokens;
  }

  public boolean matches(String path) {
    int index = 0;
    boolean search = false;
    for (int i = 0; i < this.tokens.length; i++) {
      if (this.tokens[i].charAt(0) == '*') {
        if (i == this.tokens.length - 1)
          return true;
        search = true;
        continue;
      }
      if (search) {
        int iT = path.indexOf(this.tokens[i], index);
        if (iT < 0)
          return false;
        index = iT + this.tokens[i].length();
        search = false;
        continue;
      }
      int newIndex = index + this.tokens[i].length();
      try {
        if (path.substring(index, newIndex).equals(this.tokens[i])) {
          index = newIndex;
          continue;
        }
      } catch (Exception ex) {
        return false;
      }
      return false;
    }

    return true;
  }
}
