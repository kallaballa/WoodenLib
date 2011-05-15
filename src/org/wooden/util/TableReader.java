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

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class TableReader extends BufferedReader {

  private String delimiter;

  private boolean buffered;

  private char fullData[];

  private BufferedReader in;

  public TableReader(InputStream in, String delimiter) throws IOException {
    this(in, delimiter, false);
  }

  public TableReader(InputStream in, String delimiter, boolean buffered)
      throws IOException {
    this(new InputStreamReader(in), delimiter, buffered);
  }

  public TableReader(InputStreamReader r, String delimiter, boolean buffered)
      throws IOException {
    super(r);
    this.delimiter = delimiter;
    this.buffered = buffered;
    if (buffered) {
      this.fullData = this.readFully();
      this.in = new BufferedReader(new CharArrayReader(this.fullData));
    } else {
      this.in = this;
    }
  }

  public boolean buffered() {
    return this.buffered;
  }

  @Override
  public void close() throws IOException {
    this.fullData = null;
    super.close();
  }

  public int countDataSets() throws IOException {
    int cnt = 0;
    BufferedReader tmp;
    for (tmp = new BufferedReader(new CharArrayReader(this.fullData)); tmp
        .readLine() != null;)
      cnt++;

    tmp.close();
    return cnt;
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

  private char[] readFully() throws IOException {
    CharArrayWriter cw = new CharArrayWriter();
    if (this.in == null)
      this.in = this;
    int c;
    while ((c = this.in.read()) != -1)
      cw.write(c);
    return cw.toCharArray();
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
    while ((line = this.in.readLine()) != null)
      if (line.trim().length() > 0)
        break;
    if (line == null)
      return null;
    else
      return new StringTokenizer(line, this.delimiter, true);
  }
}
