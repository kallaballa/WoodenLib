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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

  public static final SimpleDateFormat STD_FORMAT;

  public static String createTimestamp() {
    return createTimestamp(STD_FORMAT);
  }

  public static String createTimestamp(SimpleDateFormat format) {
    return format.format(new Date());
  }

  private PrintStream out;

  private File f_out;

  private int max_len;

  private boolean append;

  private boolean autoflush;

  protected SimpleDateFormat format;

  static {
    STD_FORMAT = new SimpleDateFormat("dd.MM.yy kk:mm:ss", Locale.GERMAN);
  }

  public Logger(File f, boolean append) throws IOException,
      NullPointerException {
    this(f, append, false, -1);
  }

  public Logger(File f, boolean append, boolean autoflush, int max_len)
      throws IOException, NullPointerException {
    this(f, append, autoflush, max_len, STD_FORMAT);
  }

  public Logger(File f, boolean append, boolean autoflush, int max_len,
      SimpleDateFormat format) throws IOException, NullPointerException {
    if (f == null) {
      throw new NullPointerException("the ouput file can not be null");
    } else {
      this.out = new PrintStream(new FileOutputStream(f, append));
      this.f_out = f;
      this.append = append;
      this.max_len = max_len;
      this.format = format;
      this.autoflush = autoflush;
      return;
    }
  }

  public Logger(PrintStream out, boolean append) throws IOException,
      NullPointerException {
    this(out, append, true, STD_FORMAT);
  }

  public Logger(PrintStream out, boolean append, boolean autoflush,
      SimpleDateFormat format) throws IOException, NullPointerException {
    this.out = out;
    this.max_len = -1;
    this.append = append;
    this.autoflush = autoflush;
    this.format = format;
  }

  public boolean checkFileLength(File f, int maxlen) throws IOException {
    if (f == null)
      return false;
    if (maxlen != -1 && f.length() > maxlen) {
      if (!f.delete())
        throw new IOException((new StringBuilder("coudn't delete file: "))
            .append(f.getAbsolutePath()).toString());
      if (!f.createNewFile())
        throw new IOException((new StringBuilder("coudn't recreate file: "))
            .append(f.getAbsolutePath()).toString());
      else
        return true;
    } else {
      return false;
    }
  }

  private synchronized void checkLogFile() throws IOException {
    if (this.checkFileLength(this.f_out, this.max_len))
      this.out = new PrintStream(new FileOutputStream(this.f_out, this.append));
  }

  public void close() {
    this.out.close();
  }

  public void flush() throws IOException {
    this.out.flush();
  }

  public PrintStream getPrintStream() {
    return this.out;
  }

  public void printEntry(String strEntry) throws IOException {
    this.checkLogFile();
    this.out.print(strEntry);
    if (this.autoflush)
      this.flush();
  }

  public void printEntryLine() throws IOException {
    this.checkLogFile();
    this.out.println();
    if (this.autoflush)
      this.flush();
  }

  public void printEntryLine(String strEntry) throws IOException {
    this.checkLogFile();
    this.out.println(strEntry);
    if (this.autoflush)
      this.flush();
  }

  public void printException(Exception e) throws IOException {
    this.checkLogFile();
    e.printStackTrace(this.out);
    if (this.autoflush)
      this.flush();
  }

  public void printTimestamp() throws IOException {
    this.printEntry(createTimestamp());
  }

  public void printTimestamp(SimpleDateFormat format) throws IOException {
    this.printEntry(createTimestamp(format));
  }

  public void printTimestamp(String comment) throws IOException {
    this.checkLogFile();
    this.printEntryLine((new StringBuilder(String
        .valueOf(createTimestamp(this.format)))).append(" ").append(comment)
        .toString());
  }
}
