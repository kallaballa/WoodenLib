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


public class ExtendedSema {

  private int init;

  private int s;

  private int del;

  private String name;

  public ExtendedSema(int i) {
    if (i >= 0)
      this.init = i;
    else
      this.init = 0;
    this.s = this.init;
    this.del = 0;
  }

  public ExtendedSema(int i, String name) {
    this(i);
    this.name = name;
  }

  public synchronized void P() {
    this.P(1);
  }

  public synchronized void P(int operations) {
    while (this.s - operations < 0) {
      this.del++;
      try {
        if (this.name != null) {
          System.err.println((new StringBuilder(String.valueOf(this.name)))
              .append(" paused: ").append(this.s).append("/")
              .append(operations).toString());
          System.err.flush();
        }
        this.wait();
        if (this.name != null && this.s - operations > 0) {
          System.err.println((new StringBuilder(String.valueOf(this.name)))
              .append(" resumed").toString());
          System.err.flush();
        }
      } catch (InterruptedException interruptedexception) {}
      this.del--;
    }
    this.s -= operations;
  }

  public synchronized void V() {
    this.V(1);
  }

  public synchronized void V(int operations) {
    this.s += operations;
    if (this.del > 0)
      this.notifyAll();
  }

  public synchronized boolean willBlock() {
    return this.willBlock(0);
  }

  public synchronized boolean willBlock(int operations) {
    return this.s - operations < 0;
  }
}
