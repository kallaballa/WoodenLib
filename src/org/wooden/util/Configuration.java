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

import java.applet.Applet;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class Configuration extends Properties {

  private File conffile;

  public Configuration() {}

  public Configuration(Applet applet) {
    this.loadFromApplet(applet);
  }

  public Configuration(Configuration defaults) {
    super(defaults);
  }

  public Configuration(File conffile) throws IOException {
    this.conffile = conffile;
    FileInputStream in;
    super.load(in = new FileInputStream(conffile));
    in.close();
  }

  public Configuration(InputStream in) throws IOException {
    super.load(in);
  }

  public Configuration(URL url) throws IOException {
    super.load(url.openStream());
  }

  public void apply(Configuration c) {
    String keys[] = c.getKeys();
    for (String key : keys)
      this.put(key, c.getProperty(key));

  }

  public String doubleSlashes(String value) {
    if (value != null) {
      for (int bi = 0; (bi = value.indexOf("\\", bi)) >= 0; bi += 4)
        value = (new StringBuilder(String.valueOf(value.substring(0, bi + 1))))
            .append("\\").append(value.substring(bi + 1)).toString();

    }
    return value;
  }

  public boolean getBoolean(String key) throws IllegalArgumentException {
    return this.getBoolean(key, true);
  }

  public boolean getBoolean(String key, boolean throwEx)
      throws IllegalArgumentException {
    String v = null;
    try {
      v = this.getProperty(key);
      if (v == null) {
        if (throwEx)
          this.throwIllegalArgumentException(key, v);
      } else {
        v = v.trim().toLowerCase();
        if (v.equals("true"))
          return true;
        if (v.equals("false"))
          return false;
        int num = Integer.parseInt(v);
        if (num == 0)
          return false;
        if (num == 1)
          return true;
      }
    } catch (NumberFormatException numberformatexception) {
      if (throwEx)
        this.throwIllegalArgumentException(key, v);
    }
    return false;
  }

  public byte[] getBytes() {
    ByteArrayOutputStream rawconf = null;
    try {
      rawconf = new ByteArrayOutputStream();
      this.store(rawconf);
      return rawconf.toByteArray();
    } catch (IOException ex) {
      return null;
    }
  }

  public File getConfigurationFile() {
    return this.conffile;
  }

  public File getFile(String key) throws IllegalArgumentException {
    return this.getFile(key, true);
  }

  public File getFile(String key, boolean throwEx)
      throws IllegalArgumentException {
    String v = null;
    try {
      v = this.getProperty(key);
      return new File(v);
    } catch (Exception ex) {
      if (throwEx)
        this.throwIllegalArgumentException(key, v);
      return null;
    }
  }

  public int getInt(String key) throws IllegalArgumentException {
    return this.getInt(key, true);
  }

  public int getInt(String key, boolean throwEx)
      throws IllegalArgumentException {
    String v = null;
    try {
      v = this.getProperty(key);
      return Integer.parseInt(v);
    } catch (Exception ex) {
      if (throwEx)
        this.throwIllegalArgumentException(key, v);
      return -1;
    }
  }

  public String[] getKeys() {
    return this.keySet().toArray(new String[0]);
  }

  public long getLong(String key) throws IllegalArgumentException {
    return this.getLong(key, true);
  }

  public long getLong(String key, boolean throwEx)
      throws IllegalArgumentException {
    String v = null;
    try {
      v = this.getProperty(key);
      return Long.parseLong(v);
    } catch (Exception ex) {
      if (throwEx)
        this.throwIllegalArgumentException(key, v);
      return -1L;
    }
  }

  @Override
  public String getProperty(String key) {
    String value = null;
    if (key != null) {
      Object v = super.get(key.toLowerCase());
      if (v != null)
        value = this.removeBrackets(v.toString());
    }
    return value;
  }

  public String getString(String key) throws IllegalArgumentException {
    return this.getString(key, true);
  }

  public String getString(String key, boolean throwEx)
      throws IllegalArgumentException {
    String s = this.getProperty(key);
    if (s == null && throwEx)
      this.throwIllegalArgumentException(key, s);
    return s;
  }

  public String[] getTokenizedArray(String key, String delim) {
    StringTokenizer st = new StringTokenizer(this.getProperty(key), delim);
    String arrTokens[] = new String[st.countTokens()];
    for (int i = 0; i < arrTokens.length; i++)
      arrTokens[i] = st.nextToken();

    return arrTokens;
  }

  public Vector getTokenizedVector(String key, String delim) {
    StringTokenizer st = new StringTokenizer(this.getProperty(key), delim);
    Vector vecTokens = new Vector();
    for (; st.hasMoreTokens(); vecTokens.add(st.nextToken()))
      ;
    return vecTokens;
  }

  public String[] getValues() {
    return this.values().toArray(new String[0]);
  }

  private void loadFromApplet(Applet applet) {
    String pinfo[][] = applet.getParameterInfo();
    for (String[] element : pinfo) {
      String paramname = element[0];
      String param;
      if ((param = applet.getParameter(paramname)) == null)
        this.throwIllegalArgumentException(paramname, param);
      else
        this.putProperty(paramname, param);
    }

  }

  @Override
  public Object put(Object key, Object value) {
    return super.put(key.toString().toLowerCase(), value);
  }

  public String putProperty(String key, String value) {
    return (String) super.put(key.toString().toLowerCase(), value);
  }

  private String removeBrackets(String value) {
    if (value != null && value.length() > 2) {
      value = value.trim();
      if (value.startsWith("\"") && value.endsWith("\""))
        value = value.substring(1, value.length() - 1);
    }
    return value;
  }

  public Object removeProperty(String key) {
    return super.remove(key.toLowerCase());
  }

  public void saveConfiguration() throws IOException {
    this.saveConfiguration(this.conffile);
  }

  public void saveConfiguration(File dest) throws IOException {
    if (dest != null) {
      this.conffile = dest;
      FileOutputStream out = new FileOutputStream(dest);
      this.store(out);
      out.close();
    }
  }

  public String[] sort(String arr[]) {
    Arrays.sort(arr);
    return arr;
  }

  public synchronized void store(OutputStream out) throws IOException {
    this.store(out, null);
  }

  @Override
  public synchronized void store(OutputStream out, String valueSep)
      throws IOException {
    if (valueSep == null)
      valueSep = "=";
    PrintWriter awriter = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(out, "8859_1")));
    String keys[] = this.sort(this.getKeys());
    for (String key : keys)
      awriter.println((new StringBuilder(String.valueOf('\t'))).append(key)
          .append(valueSep).append(" \"")
          .append(this.doubleSlashes(this.getProperty(key))).append("\"")
          .toString());

    awriter.flush();
  }

  public void throwIllegalArgumentException(String key, String value)
      throws IllegalArgumentException {
    throw new IllegalArgumentException(
        (new StringBuilder("Invalid value for ")).append(key).append(": ")
            .append(value).toString());
  }
}
