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
package org.wooden.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.ImageIcon;

public class JarResource implements DataResource {

  private File soureFile;

  private JarFile jar;

  private HashMap jarCache;

  private Vector changedEntries;

  private boolean cached;

  public JarResource(File jarfile) throws IOException {
    this.jarCache = new HashMap();
    this.changedEntries = new Vector();
    this.cached = false;
    this.jar = new JarFile(jarfile);
  }

  public JarResource(URL url) throws IOException {
    this(url, true, true);
  }

  public JarResource(URL url, boolean preload, boolean cache)
      throws IOException {
    this.jarCache = new HashMap();
    this.changedEntries = new Vector();
    this.cached = false;
    this.cached = cache;
    if (preload)
      this.preloadResource(url, cache);
    else
      this.jar = new JarFile(new File(url.getFile()));
    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        try {
          if (JarResource.this.jar != null)
            JarResource.this.close(true);
        } catch (IOException ioexception) {}
      }
    });
  }

  public void cache(String name, byte data[]) {
    if (this.isCached())
      this.jarCache.put(name, data);
  }

  public synchronized void clearCache() {
    this.jarCache.clear();
  }

  public void close(boolean writeChange) throws IOException,
      MalformedURLException {
    this.jar.close();
    if (writeChange) {
      Vector newEntries = null;
      JarOutputStream out = new JarOutputStream(this.soureFile.toURL()
          .openConnection().getOutputStream());
      if (this.cached) {
        int len = this.changedEntries.size();
        for (int i = 0; i < len; i++)
          this.jarCache.put(((JarEntry) this.changedEntries.get(i)).getName(),
              this.changedEntries);

        newEntries = new Vector(this.jarCache.values());
      } else {
        Enumeration enumEntries = this.jar.entries();
        newEntries = new Vector();
        Object e;
        int index;
        while (enumEntries.hasMoreElements())
          if ((index = this.changedEntries.indexOf(e = enumEntries
              .nextElement())) > -1)
            newEntries.add(this.changedEntries.get(index));
          else
            newEntries.add(e);
      }
      JarEntry entries[] = (JarEntry[]) newEntries.toArray(new JarEntry[0]);
      for (JarEntry entrie : entries)
        out.putNextEntry(entrie);

      out.close();
    }
  }

  public Enumeration enumateResourcePaths() {
    Enumeration entries = this.jar.entries();
    Vector paths = new Vector();
    for (; entries.hasMoreElements(); paths.add(((JarEntry) entries
        .nextElement()).getName()))
      ;
    return paths.elements();
  }

  public ImageIcon getIcon(String name, Component observer) throws IOException {
    ImageIcon icon = new ImageIcon(this.getImage(name, observer));
    if (icon.getImageLoadStatus() == 4)
      throw new RasterFormatException("Illegal Icon Image");
    while (icon.getImageLoadStatus() != 8)
      try {
        Thread.sleep(50L);
      } catch (InterruptedException interruptedexception) {}
    return icon;
  }

  public Image getImage(String name, Component observer) throws IOException {
    Toolkit kit = null;
    Image img = null;
    if (observer == null)
      throw new NullPointerException("Observer can't be null");
    kit = observer.getToolkit();
    if (kit == null)
      kit = Toolkit.getDefaultToolkit();
    img = kit.createImage(this.getResource(name));
    for (int i = 0; i < 10 && img.getWidth(observer) < 0; i++)
      try {
        Thread.sleep(50L);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

    if (img.getWidth(observer) < 0) {
      throw new RasterFormatException("Illegal Image Format");
    } else {
      kit.prepareImage(img, observer.getWidth(), observer.getHeight(), observer);
      return img;
    }
  }

  @Override
  public byte[] getResource(String name) throws IOException {
    if (this.isCached())
      return (byte[]) this.jarCache.get(name);
    JarEntry e = this.jar.getJarEntry(name);
    if (e == null)
      throw new IllegalArgumentException((new StringBuilder(
          "Unknown Resource: ")).append(name).toString());
    InputStream in = this.jar.getInputStream(e);
    int read = 0;
    byte buffer[] = new byte[8192];
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    while ((read = in.read(buffer, 0, buffer.length)) != -1)
      out.write(buffer, 0, read);
    return out.toByteArray();
  }

  @Override
  public InputStream getResourceStream(String name) throws IOException {
    JarEntry e = this.jar.getJarEntry(name);
    if (e == null)
      throw new IllegalArgumentException((new StringBuilder(
          "Unknown Resource: ")).append(name).toString());
    else
      return this.jar.getInputStream(e);
  }

  @Override
  public synchronized boolean isCached() {
    return this.cached;
  }

  private void preloadResource(URL url, boolean cache) throws IOException {
    JarOutputStream out;
    ByteArrayOutputStream buffer;
    String u = url.toString();
    if (!u.startsWith("jar:")) {
      u = (new StringBuilder("jar:")).append(u).append("!/").toString();
      url = new URL(u);
    }
    out = null;
    JarURLConnection connection = null;
    buffer = new ByteArrayOutputStream();
    try {
      File tmp = File.createTempFile("jar", ".res");
      tmp.deleteOnExit();
      connection = (JarURLConnection) url.openConnection();
      JarFile remoteJar = connection.getJarFile();
      out = new JarOutputStream(new FileOutputStream(tmp));
      InputStream in_e;
      for (Enumeration remoteEntries = remoteJar.entries(); remoteEntries
          .hasMoreElements(); in_e.close()) {
        JarEntry entry = (JarEntry) remoteEntries.nextElement();
        in_e = remoteJar.getInputStream(entry);
        int d;
        while ((d = in_e.read()) != -1)
          buffer.write(d);
        byte data[] = buffer.toByteArray();
        entry.setSize(data.length);
        this.cache(entry.getName(), data);
        out.putNextEntry(entry);
        out.write(data);
        out.closeEntry();
        buffer.reset();
      }

      out.close();
      this.jar = new JarFile(tmp);
    } catch (Exception ex) {
      ex.printStackTrace();
      if (out != null)
        out.close();
      throw new IOException(ex.getMessage());
    }
  }

  @Override
  public void setResource(String name, byte data[]) throws IOException {
    JarEntry res = new JarEntry(name);
    res.setSize(data.length);
    if (this.isCached())
      this.jarCache.put(name, data);
  }

}
