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

package org.wooden.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

public class PortRange {

  private boolean ports[];

  private int index;

  private int portsLeft;

  public PortRange(int min, int max) throws IndexOutOfBoundsException {
    this.index = 0;
    if (min < 0 || min >= max) {
      throw new IndexOutOfBoundsException(
          "portrange must be between 0 and 65535 and max must be greater than min");
    } else {
      this.portsLeft = max - min;
      this.ports = new boolean[this.portsLeft];
      Arrays.fill(this.ports, true);
      return;
    }
  }

  public synchronized int nextPort() throws RuntimeException {
    for (; this.index < this.ports.length; this.index++)
      if (!this.ports[this.index++]) {
        if (!this.used(this.index))
          break;
        this.ports[this.index] = false;
        this.portsLeft--;
      }

    if (this.portsLeft == 0) {
      this.update();
      return this.nextPort();
    }
    if (this.index == this.ports.length) {
      this.index = 0;
      return this.nextPort();
    } else {
      return this.index;
    }
  }

  public synchronized void setFreePort(int port) {
    if (this.ports[port]) {
      return;
    } else {
      this.ports[port] = true;
      this.portsLeft++;
      return;
    }
  }

  public synchronized void update() throws RuntimeException {
    this.portsLeft = this.ports.length;
    Arrays.fill(this.ports, true);
    for (int i = 0; i < this.ports.length; i++)
      if (this.used(i)) {
        this.portsLeft--;
        this.ports[i] = false;
      }

    if (this.portsLeft == 0)
      throw new RuntimeException("No more ports left");
    else
      return;
  }

  public boolean used(int port) {
    try {
      ServerSocket s = new ServerSocket(port);
      s.close();
    } catch (IOException ex) {
      return false;
    }
    return true;
  }
}
