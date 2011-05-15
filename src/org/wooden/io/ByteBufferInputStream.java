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

import java.io.ByteArrayInputStream;

public class ByteBufferInputStream extends ByteArrayInputStream {

  private Object dataChangeMutex;

  public ByteBufferInputStream() {
    this(1024);
  }

  public ByteBufferInputStream(int initSize) {
    super(new byte[initSize]);
    this.dataChangeMutex = new Object();
    super.count = 0;
    super.pos = 0;
    super.mark = 0;
  }

  public void append(byte dataSource[]) {
    synchronized (this.dataChangeMutex) {
      this.append(dataSource, 0, dataSource.length);
    }
  }

  public void append(byte dataSource[], int off, int len) {
    synchronized (this.dataChangeMutex) {
      if (super.buf.length < this.count + len) {
        System.err.println("--- buffer resized (append) ---");
        super.buf = new byte[len];
      }
      System.arraycopy(dataSource, off, super.buf, this.count, len);
      super.count = this.count + len;
    }
  }

  @Override
  public int available() {
    synchronized (this.dataChangeMutex) {
      return super.available();
    }
  }

  public void copy(byte dataSource[]) {
    synchronized (this.dataChangeMutex) {
      this.copy(dataSource, 0, dataSource.length);
    }
  }

  public void copy(byte dataSource[], int off, int len) {
    synchronized (this.dataChangeMutex) {
      if (super.buf.length < len) {
        System.err.println("--- buffer resized (copy) ---");
        super.buf = new byte[len];
      }
      System.arraycopy(dataSource, off, super.buf, 0, len);
      super.count = len;
      super.pos = 0;
      super.mark = 0;
    }
  }

  @Override
  public void mark(int readAheadLimit) {
    synchronized (this.dataChangeMutex) {
      super.mark(readAheadLimit);
    }
  }

  @Override
  public int read() {
    synchronized (this.dataChangeMutex) {
      return super.read();
    }
  }

  @Override
  public int read(byte b[], int off, int len) {
    synchronized (this.dataChangeMutex) {
      return super.read(b, off, len);
    }
  }

  @Override
  public void reset() {
    synchronized (this.dataChangeMutex) {
      super.reset();
    }
  }

  @Override
  public long skip(long n) {
    Object obj = this.dataChangeMutex;
    synchronized (this.dataChangeMutex) {
      return super.skip(n);
    }
  }
}
