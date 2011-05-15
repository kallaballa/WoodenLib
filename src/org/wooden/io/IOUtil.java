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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class IOUtil {

  public static final int BOUNDED_BUFFER_SIZE = 0x100000;

  public static void copyFile(File fIn, File fOut) throws IOException,
      InvocationTargetException {
    pipeStream(new FileInputStream(fIn), new FileOutputStream(fOut));
  }

  public static void pipeStream(InputStream in, OutputStream out)
      throws IOException, InvocationTargetException {
    pipeStream(in, out, 0x100000);
  }

  public static void pipeStream(InputStream in, OutputStream out,
      int mainBufferSize) throws IOException, InvocationTargetException {
    BoundedBuffer sharedBuffer = new BoundedBuffer(mainBufferSize);
    BoundedBufferRear rear = new BoundedBufferRear(sharedBuffer);
    BoundedBufferFront front = new BoundedBufferFront(sharedBuffer);
    rear.setName((new StringBuilder("IOUtilRear( "))
        .append(Thread.currentThread().getName()).append(")").toString());
    front.setName((new StringBuilder("IOUtilFront( "))
        .append(Thread.currentThread().getName()).append(")").toString());
    rear.connect(in);
    front.connect(out);
    try {
      front.join();
      rear.join();
    } catch (InterruptedException interruptedexception) {}
    in.close();
    out.close();
    if (front.isAborted())
      throw front.abortionCause();
    if (rear.isAborted())
      throw rear.abortionCause();
    else
      return;
  }

  public static byte[] readFully(InputStream in) throws IOException,
      InvocationTargetException {
    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    pipeStream(in, byteBuffer);
    return byteBuffer.toByteArray();
  }

  private IOUtil() {}
}
