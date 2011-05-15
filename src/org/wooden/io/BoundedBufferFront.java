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
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class BoundedBufferFront extends BoundedBufferAccess {
  private class AutoFlush extends Thread {

    private long interval;

    private OutputStream out;

    public AutoFlush(OutputStream out, long interval) {
      super();
      this.out = out;
      this.interval = interval;
    }

    @Override
    public void run() {
      try {
        for (; !BoundedBufferFront.this.isClosed(); this.out.flush())
          Thread.sleep(this.interval);

      } catch (Throwable throwable) {}
    }
  }

  private OutputStream out;

  private long flushInterval;

  public BoundedBufferFront(BoundedBuffer sharedBuffer) {
    this(sharedBuffer, -1L);
  }

  public BoundedBufferFront(BoundedBuffer sharedBuffer, long flushInterval) {
    this(sharedBuffer, flushInterval, true);
  }

  public BoundedBufferFront(BoundedBuffer sharedBuffer, long flushInterval,
      boolean closeStreamOnExit) {
    super(sharedBuffer, closeStreamOnExit);
    this.flushInterval = flushInterval;
  }

  @Override
  protected void closeStream() throws IOException {
    this.out.close();
  }

  public void connect(OutputStream out) {
    if (!this.isClosed()) {
      this.out = out;
      this.start();
      if (this.flushInterval > 0L)
        (new AutoFlush(out, this.flushInterval)).start();
    }
  }

  @Override
  public void run() {
    try {
      int d;
      while ((d = this.getSharedBuffer().get()) != -1)
        this.out.write(d);
      this.out.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      if (!this.getSharedBuffer().isAborted())
        this.getSharedBuffer().abort(ex);
    }
    try {
      if (!this.getSharedBuffer().isClosed())
        this.getSharedBuffer().close();
    } catch (InvocationTargetException invocationtargetexception) {}
  }
}
