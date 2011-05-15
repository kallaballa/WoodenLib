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

import java.util.Date;

public class Message {

  private long sequenceNumber;

  private long creationTime;

  private byte data[];

  private int length;

  private String command;

  public Message(String command, byte data[], int length, long sequenceNumber) {
    this(command, data, length, sequenceNumber, System.currentTimeMillis());
  }

  public Message(String command, byte data[], int length, long sequenceNumber,
      long creationTime) {
    this.command = command;
    this.data = data;
    this.sequenceNumber = sequenceNumber;
    this.creationTime = creationTime;
    this.length = length;
  }

  public String getCommand() {
    return this.command;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public byte[] getData() {
    return this.data;
  }

  public int getDataLength() {
    return this.length;
  }

  public long getSequenceNumber() {
    return this.sequenceNumber;
  }

  @Override
  public String toString() {
    return (new StringBuilder("[ seqNr=")).append(this.sequenceNumber)
        .append(", creationTime=").append(new Date(this.creationTime))
        .append(", command=").append(this.command).append(", dataLen=")
        .append(this.length).append(" ]").toString();
  }
}
