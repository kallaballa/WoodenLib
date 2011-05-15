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

public class BoundedBufferRear extends BoundedBufferAccess {

  private boolean closed;

  private InputStream in;

  public BoundedBufferRear(BoundedBuffer sharedBuffer) {
    super(sharedBuffer);
    this.closed = false;
  }

  public BoundedBufferRear(BoundedBuffer sharedBuffer, boolean closeStreamOnExit) {
    super(sharedBuffer, closeStreamOnExit);
    this.closed = false;
  }

  @Override
  protected void closeStream() throws IOException {
    this.in.close();
  }

  public synchronized void connect(InputStream in) {
    if (!this.isClosed()) {
      this.in = in;
      super.start();
    }
  }

  @Override
  public void run() {
    try {
      int d;
      while ((d = this.in.read()) > -1)
        this.getSharedBuffer().put((byte) d);
      this.getSharedBuffer().terminate();
    } catch (Exception ex) {
      if (!this.getSharedBuffer().isAborted())
        this.getSharedBuffer().abort(ex);
    }
  }
}
