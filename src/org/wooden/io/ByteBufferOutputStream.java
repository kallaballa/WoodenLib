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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ByteBufferOutputStream extends ByteArrayOutputStream {

  private Object copyMutex;

  public ByteBufferOutputStream() {
    this.copyMutex = new Object();
  }

  public ByteBufferOutputStream(int p0) {
    super(p0);
    this.copyMutex = new Object();
  }

  public void appendTo(ByteBufferInputStream in) {
    synchronized (this.copyMutex) {
      in.append(super.buf, 0, super.count);
    }
  }

  public void copyTo(ByteBufferInputStream in) {
    synchronized (this.copyMutex) {
      in.copy(super.buf, 0, super.count);
    }
  }

  public byte[] getBuffer() {
    return super.buf;
  }

  @Override
  public void reset() {
    synchronized (this.copyMutex) {
      super.reset();
    }
  }

  @Override
  public int size() {
    synchronized (this.copyMutex) {
      return super.size();
    }
  }

  @Override
  public byte[] toByteArray() {
    synchronized (this.copyMutex) {
      return super.toByteArray();
    }
  }

  @Override
  public String toString() {
    synchronized (this.copyMutex) {
      return super.toString();
    }
  }

  @Override
  public String toString(int hibyte) {
    synchronized (this.copyMutex) {
      return super.toString(hibyte);
    }
  }

  @Override
  public String toString(String enc) throws UnsupportedEncodingException {
    synchronized (this.copyMutex) {
      return super.toString(enc);
    }
  }

  @Override
  public void write(byte b[], int off, int len) {
    synchronized (this.copyMutex) {
      super.write(b, off, len);
    }
  }

  @Override
  public void write(int b) {
    synchronized (this.copyMutex) {
      super.write(b);
    }
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {
    synchronized (this.copyMutex) {
      super.writeTo(out);
    }
  }
}
