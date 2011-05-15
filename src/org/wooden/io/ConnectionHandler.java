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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;

public class ConnectionHandler {

  public static final int LOCK_NONE = 0;

  public static final int LOCK_IN = 1;

  public static final int LOCK_OUT = 2;

  private Vector acceptedMessages;

  private HashMap inputConnections;

  private HashMap outputConnections;

  private boolean isInLocked;

  private boolean isOutLocked;

  private Sema outputLock;

  private Sema inputLock;

  private Sema messageLock;

  private String lockedOutputStream;

  private String lockedInputConnection;

  private SequenceNumberGenerator sng;

  private boolean open;

  protected int numInputConnections;

  protected int numOutputConnections;

  public ConnectionHandler() throws IOException {
    this(0, 0, 0);
  }

  public ConnectionHandler(int maxParallelInCons, int maxParallelOutCons,
      int lockMask) throws IOException {
    this.acceptedMessages = new Vector();
    this.inputConnections = new HashMap();
    this.outputConnections = new HashMap();
    this.isInLocked = false;
    this.isOutLocked = false;
    this.messageLock = new Sema(1);
    this.lockedOutputStream = null;
    this.lockedInputConnection = null;
    this.sng = new SequenceNumberGenerator();
    this.open = true;
    this.numInputConnections = 0;
    this.numOutputConnections = 0;
    if (lockMask < 0 || lockMask > 3) {
      throw new IllegalArgumentException((new StringBuilder(
          "Illegal lock mask: ")).append(lockMask).toString());
    } else {
      this.isInLocked = (lockMask & 1) == 1;
      this.isOutLocked = (lockMask & 2) == 2;
      this.inputLock = new Sema(maxParallelInCons);
      this.outputLock = new Sema(maxParallelOutCons);
      return;
    }
  }

  public synchronized void addInputConnection(String name, InputStream in) {
    this.inputConnections.put(name, new InputConnection(name, in, this));
    this.numInputConnections++;
  }

  public synchronized void addOutputConnection(String name, OutputStream out) {
    this.outputConnections.put(name, new OutputConnection(name, out, this));
    this.numOutputConnections++;
  }

  public void close() throws IOException {
    InputConnection inCons[] = (InputConnection[]) this.inputConnections
        .values().toArray(new InputConnection[0]);
    OutputConnection outCons[] = (OutputConnection[]) this.outputConnections
        .values().toArray(new OutputConnection[0]);
    for (InputConnection inCon : inCons)
      try {
        inCon.close();
      } catch (IOException ioexception) {}

    for (OutputConnection outCon : outCons)
      try {
        outCon.close();
      } catch (IOException ioexception1) {}

    this.outputLock.V();
    this.inputLock.V();
    this.open = false;
  }

  public synchronized InputConnection getInputConnection(String name) {
    return (InputConnection) this.inputConnections.get(name);
  }

  public synchronized OutputConnection getOutputConnection(String name) {
    return (OutputConnection) this.outputConnections.get(name);
  }

  public boolean isClosed() {
    return !this.open;
  }

  public boolean isInputLocked() {
    return this.isInLocked;
  }

  public boolean isOutputLocked() {
    return this.isOutLocked;
  }

  public synchronized void lockInputConnection(String name) {
    if (this.isInputLocked()) {
      if (!this.inputConnections.containsKey(name))
        throw new IllegalArgumentException((new StringBuilder(
            "Unknown Outp\uFFFDtConnection: ")).append(name).toString());
      this.inputLock.P();
      this.lockedInputConnection = name;
      System.err.println((new StringBuilder("Inputstream locked("))
          .append(Thread.currentThread().getName()).append("): ").append(name)
          .toString());
    }
  }

  public synchronized void lockOutputConnection(String name) {
    if (this.isOutputLocked()) {
      if (!this.outputConnections.containsKey(name))
        throw new IllegalArgumentException((new StringBuilder(
            "Unknown Outp\uFFFDtConnection: ")).append(name).toString());
      this.outputLock.P();
      this.lockedOutputStream = name;
      System.err.println((new StringBuilder("Outputstream locked("))
          .append(Thread.currentThread().getName()).append("): ").append(name)
          .toString());
    }
  }

  public void messageAccepted(long sequenceNumber) {
    this.acceptedMessages.add(String.valueOf(sequenceNumber));
    this.messageLock.V();
  }

  public synchronized long nextSequenceNumber() {
    return this.sng.nextSequenceNumber();
  }

  public synchronized void releaseInputLock(String name) {
    if (this.isInputLocked()) {
      if (name == null || !name.equals(this.lockedInputConnection))
        throw new IllegalArgumentException((new StringBuilder(
            "InputConnection ")).append(name).append(" was not locked")
            .toString());
      this.lockedInputConnection = null;
      System.err.println((new StringBuilder("InputStream release("))
          .append(Thread.currentThread().getName()).append("): ").append(name)
          .toString());
      this.inputLock.V();
    }
  }

  public synchronized void releaseOutputLock(String name) {
    if (this.isOutputLocked()) {
      if (name == null || !name.equals(this.lockedOutputStream))
        throw new IllegalArgumentException((new StringBuilder("OutputStream "))
            .append(name).append(" was not locked").toString());
      this.lockedOutputStream = null;
      System.err.println((new StringBuilder("Outputstream released("))
          .append(Thread.currentThread().getName()).append("): ").append(name)
          .toString());
      this.outputLock.V();
    }
  }

  public synchronized void removeInputConnection(String name) {
    this.inputConnections.remove(name);
    this.numInputConnections--;
  }

  public synchronized void removeOutputConnection(String name) {
    this.outputConnections.remove(name);
    this.numOutputConnections--;
  }

  public void waitForMessageSequence(long sequenceNumber) {
    for (String strSeq = String.valueOf(sequenceNumber); !this.acceptedMessages
        .contains(strSeq); this.messageLock.P())
      ;
    if (sequenceNumber >= this.numInputConnections)
      this.acceptedMessages.remove(String.valueOf(sequenceNumber
          - this.numInputConnections));
  }
}
