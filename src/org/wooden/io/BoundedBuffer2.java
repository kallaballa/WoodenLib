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

public class BoundedBuffer2 {
  public static final int PUT = 0;
  public static final int GET = 1;
  private final int size;
  private byte buffer[];
  private int front;
  private int rear;
  private Sema2 full;
  private Sema2 empty;
  private Object pt;
  private Object gt;

  public BoundedBuffer2(int init) {
    this.buffer = new byte[init];
    this.size = init;
    this.front = 0;
    this.rear = 0;

    this.pt = new Object();
    this.gt = new Object();

    this.full = new Sema2(init);
    this.empty = new Sema2(0);
  }

  protected int front() {
    return this.front;
  }

  public int get() {
    this.empty.work();
    int v;
    synchronized (this.gt) {
      v = this.buffer[this.front];
      int s = this.size();
      this.front += 1;
      if (this.front >= s)
        this.front -= s;
    }
    this.full.free();
    return v;
  }

  public void put(byte v) {
    this.full.work();
    synchronized (this.pt) {
      this.buffer[this.rear] = v;
      int s = this.size();
      this.rear += 1;
      if (this.rear >= s)
        this.rear -= s;

    }
    this.empty.free();
  }

  protected int rear() {
    return this.rear;
  }

  public int size() {
    return this.size;
  }

}
