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

public class SearchPattern {
  public static final int METHOD_AND = 0;
  public static final int METHOD_OR = 1;
  public static final int METHOD_EXACT = 2;
  private String patternTokens[];
  private String rawPattern;
  private int method;

  public SearchPattern(String p, int method) {
    this.updatePattern(p, method);
  }

  public int getMethod() {
    return this.method;
  }

  public String getMethodString() {
    switch (this.getMethod()) {
    case 0:
      return "AND";

    case 1:
      return "OR";

    case 2:
      return "EXACT";
    }
    return null;
  }

  public boolean matches(String compare) {
    if (compare == null)
      return false;
    compare = compare.toLowerCase();
    switch (this.method) {
    case 0:
      return this.matchesAnd(compare);

    case 1:
      return this.matchesOr(compare);

    case 2:
      return this.matchesExact(compare);
    }
    return false;
  }

  private boolean matchesAnd(String comp) {
    boolean matches = false;
    for (String patternToken : this.patternTokens) {
      matches = comp.indexOf(patternToken) > -1;
      if (!matches)
        break;
    }

    return matches;
  }

  private boolean matchesExact(String comp) {
    return comp.indexOf(this.rawPattern) > -1;
  }

  private boolean matchesOr(String comp) {
    boolean matches = false;
    for (String patternToken : this.patternTokens) {
      matches = comp.indexOf(patternToken) > -1;
      if (matches)
        break;
    }

    return matches;
  }

  @Override
  public String toString() {
    return this.rawPattern;
  }

  public void updatePattern(String p, int method) {
    if (method < 0 || method > 2)
      throw new IllegalArgumentException("Unknown search method");
    p = p.toLowerCase();
    this.method = method;
    this.rawPattern = p;
    if (method < 2) {
      StringTokenizer t = new StringTokenizer(p, " ");
      this.patternTokens = new String[t.countTokens()];
      for (int i = 0; t.hasMoreTokens(); i++)
        this.patternTokens[i] = t.nextToken();
    }
  }
}
