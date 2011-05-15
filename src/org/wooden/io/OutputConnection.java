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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputConnection {

  private ConnectionHandler conHandler;

  private DataOutputStream out;

  private String name;

  public OutputConnection(String name, OutputStream out,
      ConnectionHandler conHandler) {
    this.conHandler = conHandler;
    this.out = new DataOutputStream(out);
    this.name = name;
  }

  public void close() throws IOException {
    this.out.close();
    this.conHandler.releaseOutputLock(this.name);
    this.conHandler.removeOutputConnection(this.name);
  }

  public void writeMessage(Message msg) throws IOException {
    this.conHandler.lockOutputConnection(this.name);
    this.out.writeLong(msg.getSequenceNumber());
    this.out.writeLong(msg.getCreationTime());
    this.out.writeInt(msg.getDataLength());
    this.out.write(msg.getData());
    this.conHandler.releaseOutputLock(this.name);
  }
}
