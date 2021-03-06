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

public class Sema {

  private int init;

  private int s;

  private int del;

  public Sema(int i) {
    if (i >= 0)
      this.init = i;
    else
      this.init = 0;
    this.s = this.init;
    this.del = 0;
  }

  public synchronized void P() {
    while (this.s <= 0) {
      this.del++;
      try {
        this.wait();
      } catch (InterruptedException ex) {}
      this.del--;
    }
    this.s--;
  }

  public synchronized void P(long maxWait) {
    if (this.s <= 0) {
      this.del++;
      try {
        this.wait(maxWait);
      } catch (InterruptedException interruptedexception) {}
      this.del--;
      this.s--;
    }
  }

  public synchronized void V() {
    this.s++;
    if (this.del > 0)
      this.notifyAll();
  }
}
