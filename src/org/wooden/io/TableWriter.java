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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

public class TableWriter extends BufferedWriter {

  private String delimiter;

  public TableWriter(OutputStream out, String delimiter) {
    this(((new OutputStreamWriter(out))), delimiter);
  }

  public TableWriter(Writer out, String delimiter) {
    super(out);
    this.delimiter = delimiter;
  }

  public void writeArray(String arrData[]) throws IOException {
    for (String element : arrData) {
      this.write(element);
      this.write(this.delimiter);
    }

    this.newLine();
  }

  public void writeCollection(Collection c) throws IOException {
    this.writeIterator(c.iterator());
  }

  public void writeIterator(Iterator i) throws IOException {
    for (; i.hasNext(); this.write(this.delimiter))
      this.write(i.next().toString());

    this.newLine();
  }
}
