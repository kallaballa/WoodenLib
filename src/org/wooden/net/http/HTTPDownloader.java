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
package org.wooden.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.wooden.io.IOUtil;

public class HTTPDownloader {

  public static byte[] download(URL url) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in = url.openStream();
    int d;
    while ((d = in.read()) != -1)
      out.write(d);
    out.close();
    in.close();
    return out.toByteArray();
  }

  public static void store(URL url, OutputStream out) throws IOException {
    try {
      IOUtil.pipeStream(url.openStream(), out);
    } catch (InvocationTargetException e) {
      throw new IOException(e.getMessage());
    }
  }

  public HTTPDownloader() {}
}
