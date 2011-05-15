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
import java.lang.reflect.InvocationTargetException;

public abstract class BoundedBufferAccess extends Thread {

  private BoundedBuffer sharedBuffer;

  private boolean closed;

  private boolean closeStreamOnExit;

  public BoundedBufferAccess(BoundedBuffer sharedBuffer) {
    this(sharedBuffer, true);
  }

  public BoundedBufferAccess(BoundedBuffer sharedBuffer,
      boolean closeStreamOnExit) {
    this.closed = false;
    this.closeStreamOnExit = true;
    this.sharedBuffer = sharedBuffer;
    this.closeStreamOnExit = closeStreamOnExit;
  }

  public void abort(Throwable t) {
    this.sharedBuffer.abort(t);
  }

  public InvocationTargetException abortionCause() {
    return this.sharedBuffer.abortionCause();
  }

  public void close() throws IOException, InvocationTargetException {
    if (this.isClosed()) {
      return;
    } else {
      this.closed = true;
      this.closeStream();
      this.getSharedBuffer().close();
      return;
    }
  }

  protected abstract void closeStream() throws IOException;

  public BoundedBuffer getSharedBuffer() {
    return this.sharedBuffer;
  }

  public boolean isAborted() {
    return this.sharedBuffer.isAborted();
  }

  public boolean isClosed() {
    return this.closed;
  }
}
