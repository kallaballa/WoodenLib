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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputConnection {

  private ConnectionHandler conHandler;

  private DataInputStream in;

  private String name;

  public InputConnection(String name, InputStream in,
      ConnectionHandler conHandler) {
    this.name = name;
    if (!(in instanceof BufferedInputStream))
      in = new BufferedInputStream(in, 4069);
    this.in = new DataInputStream(in);
    this.conHandler = conHandler;
  }

  public void close() throws IOException {
    this.in.close();
    this.conHandler.releaseInputLock(this.name);
    this.conHandler.removeInputConnection(this.name);
  }

  public Message readMessage() throws IOException {
    this.conHandler.lockInputConnection(this.name);
    long seqNr = this.in.readLong();
    long creationTime = this.in.readLong();
    int dataLen = this.in.readInt();
    byte data[] = new byte[dataLen];
    this.in.readFully(data);
    this.conHandler.releaseInputLock(this.name);
    return new Message("", data, dataLen, seqNr, creationTime);
  }
}
