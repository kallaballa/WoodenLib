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
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class ByteStreamPipe extends BoundedBuffer {

  public static final int DEFAULT_BUFFER_SIZE = 8192;

  private BoundedBufferFront front;

  private BoundedBufferRear rear;

  private boolean closed;

  public ByteStreamPipe(InputStream in, OutputStream out) {
    this(in, out, 8192, -1L, true);
  }

  public ByteStreamPipe(InputStream in, OutputStream out, int capacity,
      long autoflushInterval, boolean autoCloseStreams) {
    this(in, out, capacity, autoflushInterval, autoCloseStreams, null);
  }

  public ByteStreamPipe(InputStream in, OutputStream out, int capacity,
      long autoflushInterval, boolean autoCloseStreams, String name) {
    super(capacity, name);
    this.closed = false;
    this.front = new BoundedBufferFront(this, autoflushInterval,
        autoCloseStreams);
    this.rear = new BoundedBufferRear(this, autoCloseStreams);
    this.front.connect(out);
    this.rear.connect(in);
  }

  @Override
  public void close() throws InvocationTargetException {
    if (this.isClosed())
      return;
    this.closed = true;
    try {
      this.front.close();
    } catch (IOException ioexception) {}
    try {
      this.rear.close();
    } catch (IOException ioexception1) {}
    super.close();
  }

  @Override
  public boolean isClosed() {
    return this.closed;
  }

  public void join() throws InterruptedException, InvocationTargetException {
    if (this.front.isAlive())
      this.front.join();
    if (this.isAborted())
      throw this.abortionCause();
    else
      return;
  }
}
