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

public class BoundedObjectBuffer {

  private Object buffer[];

  private int size;

  private int front;

  private int rear;

  private ExtendedSema empty;

  private ExtendedSema full;
  private Object pt;
  private Object gt;

  public BoundedObjectBuffer(int init) {
    this.buffer = new Object[init];
    this.size = init;
    this.front = 0;
    this.rear = 0;
    this.empty = new ExtendedSema(init);
    this.full = new ExtendedSema(0);
    this.pt = new Object();
    this.gt = new Object();
  }

  public BoundedObjectBuffer(int init, String name) {
    this.buffer = new Object[init];
    this.size = init;
    this.front = 0;
    this.rear = 0;
    this.empty = new ExtendedSema(init,
        (new StringBuilder(String.valueOf(name))).append("/full").toString());
    this.full = new ExtendedSema(0, (new StringBuilder(String.valueOf(name)))
        .append("/empty").toString());
    this.pt = new Object();
    this.gt = new Object();
  }

  public boolean empty() {
    return this.front == this.rear;
  }

  public Object get() {
    this.full.P();
    Object v;
    synchronized (this.gt) {
      v = this.buffer[this.front];
      this.front++;
      if (this.front >= this.size)
        this.front -= this.size;
    }
    this.empty.V();
    return v;
  }

  public void put(Object v) {
    this.empty.P();
    synchronized (this.pt) {
      this.buffer[this.rear] = v;
      this.rear++;
      if (this.rear >= this.size)
        this.rear -= this.size;
    }
    this.full.V();
  }
}
