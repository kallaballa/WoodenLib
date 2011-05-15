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

package org.wooden.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class TableReader extends BufferedReader {

  private String delimiter;

  public TableReader(InputStream in, String delimiter) throws IOException {
    this(((new InputStreamReader(in))), delimiter);
  }

  public TableReader(Reader r, String delimiter) throws IOException {
    super(r);
    this.delimiter = delimiter;
  }

  public String[] readArray() throws IOException {
    Collection c = this.readCollection();
    if (c != null)
      return (String[]) c.toArray(new String[0]);
    else
      return null;
  }

  public Collection readCollection() throws IOException {
    StringTokenizer s = this.readStringTokenizer();
    if (s == null)
      return null;
    boolean lastWasDelimiter = true;
    Vector tokens = new Vector();
    for (int i = 0; s.hasMoreTokens(); i++) {
      String token = s.nextToken().trim();
      if (lastWasDelimiter) {
        if (token.equals(this.delimiter)) {
          tokens.add("");
        } else {
          lastWasDelimiter = false;
          tokens.add(token);
        }
      } else {
        lastWasDelimiter = true;
      }
    }

    return tokens;
  }

  public Iterator readIterator() throws IOException {
    Collection c = this.readCollection();
    if (c != null)
      return c.iterator();
    else
      return null;
  }

  private StringTokenizer readStringTokenizer() throws IOException {
    String line;
    while ((line = this.readLine()) != null)
      if (line.trim().length() > 0)
        break;
    if (line == null)
      return null;
    else
      return new StringTokenizer(line, this.delimiter, true);
  }
}
