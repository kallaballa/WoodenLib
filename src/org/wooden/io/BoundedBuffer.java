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

import java.lang.reflect.InvocationTargetException;
import java.nio.BufferOverflowException;

public class BoundedBuffer {

  public static final int PUT = 0;

  public static final int GET = 1;

  private final int size;

  private byte buffer[];

  private int front;

  private int rear;

  private Semaphore full;

  private Semaphore empty;

  private Object pt;

  private Object gt;

  private boolean terminated;

  private boolean closed;

  private InvocationTargetException cause;

  public BoundedBuffer(int init) {
    this(init, null);
  }

  public BoundedBuffer(int init, String name) {
    this.terminated = false;
    this.closed = false;
    if (init <= 1 || init >= 0x7fffffff)
      throw new IllegalArgumentException((new StringBuilder(
          "Illegal buffer size (size > 1 && size < Integer.MAX_VALUE): "))
          .append(init).toString());
    this.buffer = new byte[init];
    this.size = init;
    this.front = 0;
    this.rear = 0;
    this.pt = new Object();
    this.gt = new Object();
    if (name != null) {
      this.full = new Semaphore(init, (new StringBuilder(String.valueOf(name)))
          .append("/FULL").toString());
      this.empty = new Semaphore(0, (new StringBuilder(String.valueOf(name)))
          .append("/EMPTY").toString());
    } else {
      this.full = new Semaphore(init);
      this.empty = new Semaphore(0);
    }
  }

  public void abort(Throwable cause) {
    this.closed = true;
    this.terminated = true;
    this.empty.invalidate();
    this.full.invalidate();
    this.buffer = null;
    this.empty = null;
    this.full = null;
    this.pt = null;
    this.gt = null;
    this.cause = new InvocationTargetException(cause);
  }

  public InvocationTargetException abortionCause() {
    return this.cause;
  }

  protected int adjustDataLength(int len) {
    int rem = this.remainingValues();
    return rem >= len ? len : rem;
  }

  protected void checkDataLength(int len) {
    if (len > this.size())
      throw new BufferOverflowException();
    else
      return;
  }

  public void close() throws InvocationTargetException {
    if (this.isClosed())
      return;
    if (!this.isTerminated())
      this.terminate();
    this.closed = true;
    this.empty.invalidate();
    this.full.invalidate();
    this.buffer = null;
    this.empty = null;
    this.full = null;
    this.pt = null;
    this.gt = null;
  }

  public void ensureOpen() throws InvocationTargetException {
    if (this.isClosed())
      throw new IllegalStateException("The buffer was closed");
    if (this.isAborted())
      throw this.abortionCause();
    else
      return;
  }

  protected int front() {
    return this.front;
  }

  public int get() throws InvocationTargetException {
    this.ensureOpen();
    if (this.isTerminated() && this.isEmpty())
      return -1;
    this.empty.work();
    if (this.isClosed() || this.isTerminated() && this.isEmpty())
      return -1;
    int v;
    synchronized (this.gt) {
      v = this.buffer[this.front];
      this.incrementFront(1);
    }
    this.full.free();
    return v;
  }

  public int get(byte data[]) throws InvocationTargetException {
    return this.get(data, 0, data.length);
  }

  public int get(byte data[], int off, int len)
      throws InvocationTargetException {
    this.ensureOpen();
    this.checkDataLength(len);
    if (this.isTerminated()) {
      if (this.isEmpty())
        return -1;
      this.empty.work(len = this.adjustDataLength(len));
    } else {
      this.empty.work(len);
    }
    synchronized (this.gt) {
      if (this.front + len > this.size) {
        int first = this.size - this.front;
        int second = len - first;
        System.arraycopy(this.buffer, this.front, data, off, first);
        System.arraycopy(this.buffer, 0, data, first, second);
      } else {
        System.arraycopy(this.buffer, this.front, data, off, len);
      }
      this.incrementFront(len);
    }
    this.full.free(len);
    return len;
  }

  private void incrementFront(int inc) {
    int s = this.size();
    this.front += inc;
    if (this.front >= s)
      this.front -= s;
  }

  private void incrementRear(int inc) {
    int s = this.size();
    this.rear += inc;
    if (this.rear >= s)
      this.rear -= s;
  }

  public boolean isAborted() {
    return this.abortionCause() != null;
  }

  public boolean isClosed() {
    return this.closed;
  }

  public boolean isEmpty() throws InvocationTargetException {
    this.ensureOpen();
    return this.front() == this.rear();
  }

  public boolean isFull() throws InvocationTargetException {
    this.ensureOpen();
    return this.size() == this.remainingArrayElements();
  }

  public boolean isTerminated() throws InvocationTargetException {
    this.ensureOpen();
    return this.terminated;
  }

  public boolean operationWillBlock(int len, int operation)
      throws InvocationTargetException {
    this.ensureOpen();
    this.checkDataLength(len);
    if (operation == 0)
      return this.full.willBlock(len);
    if (operation == 1)
      return this.empty.willBlock(len);
    else
      throw new IllegalArgumentException("Unknown operation");
  }

  public void put(byte v) throws InvocationTargetException {
    this.ensureOpen();
    this.full.work();
    synchronized (this.pt) {
      this.buffer[this.rear] = v;
      this.incrementRear(1);
    }
    this.empty.free();
  }

  public void put(byte data[]) throws InvocationTargetException {
    this.put(data, 0, data.length);
  }

  public void put(byte data[], int off, int len)
      throws InvocationTargetException {
    this.ensureOpen();
    if (this.terminated)
      throw new IllegalStateException("The buffer was terminated");
    this.checkDataLength(len);
    this.full.work(len);
    if (this.isClosed() || this.isTerminated())
      return;
    synchronized (this.pt) {
      int rem = this.remainingArrayElements();
      int rear = this.rear();
      if (rem < len) {
        System.arraycopy(data, off, this.buffer, rear, rem);
        System.arraycopy(data, rem, this.buffer, 0, len - rem);
      } else {
        try {
          System.arraycopy(data, 0, this.buffer, rear, len);
        } catch (Exception ex) {
          System.out.println((new StringBuilder(String.valueOf(rear)))
              .append(":").append(len).append("/").append(rem).toString());
        }
      }
      this.incrementRear(len);
    }
    this.empty.free(len);
  }

  protected int rear() {
    return this.rear;
  }

  protected int remainingArrayElements() {
    return this.size() - this.rear();
  }

  protected int remainingValues() {
    int r = this.rear();
    int f = this.front();
    if (r > f)
      return r - f;
    if (r < f)
      return f - r;
    else
      return 0;
  }

  public int size() {
    return this.size;
  }

  public void terminate() throws InvocationTargetException {
    this.ensureOpen();
    this.terminated = true;
    if (this.isEmpty()) {
      this.empty.invalidate();
      this.full.invalidate();
    }
  }
}
