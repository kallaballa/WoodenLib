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

public class Semaphore {

  private int init;

  private int s;

  private int del;

  private String name;

  private boolean valid;

  public Semaphore(int i) {
    this.valid = true;
    if (i >= 0)
      this.init = i;
    else
      this.init = 0;
    this.s = this.init;
    this.del = 0;
  }

  public Semaphore(int i, String name) {
    this(i);
    this.name = name;
  }

  private synchronized void check() {
    if (!this.isValid())
      throw new IllegalStateException("Semaphore is invalidated");
    else
      return;
  }

  public synchronized void free() {
    this.free(1);
  }

  public synchronized void free(int operations) {
    this.check();
    this.s += operations;
    if (this.del > 0)
      this.notifyAll();
  }

  public synchronized void invalidate() {
    this.check();
    this.valid = false;
    if (this.del > 0)
      this.notifyAll();
  }

  public synchronized boolean isValid() {
    return this.valid;
  }

  public synchronized boolean willBlock() {
    return this.willBlock(0);
  }

  public synchronized boolean willBlock(int operations) {
    this.check();
    return this.s - operations < 0;
  }

  public synchronized void work() {
    this.work(1);
  }

  public synchronized void work(int operations) {
    this.check();
    while (this.s - operations < 0) {

      this.del++;
      if (this.name != null) {
        System.err.println((new StringBuilder(String.valueOf(this.name)))
            .append(" paused: ").append(this.s).append("/").append(operations)
            .toString());
        System.err.flush();
      }
      try {
        this.wait();
      } catch (InterruptedException interruptedexception) {}
      if (!this.isValid())
        break; /* Loop/switch isn't completed */

      if (this.name != null && this.s - operations >= 0) {
        System.err.println((new StringBuilder(String.valueOf(this.name)))
            .append(" resumed").toString());
        System.err.flush();
      }

      this.del--;
    }
    this.s -= operations;
    return;
  }
}
