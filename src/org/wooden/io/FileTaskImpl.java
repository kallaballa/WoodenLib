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

import java.io.File;
import java.io.IOException;

public class FileTaskImpl extends FileTask {

  public static void main(String args[]) {
    FileTaskImpl f = null;
    try {
      f = new FileTaskImpl(new File("C:/"), new File[] {
          new File("c:/Windows/"), new File("C:/Programme/"),
          new File("C:/Temp/") });
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    System.out.println(f.totalSize());
  }

  private File file;

  private File root;

  public FileTaskImpl(File rootDirectoy, File files[]) throws IOException {
    this(rootDirectoy, files, true);
  }

  public FileTaskImpl(File rootDirectoy, File files[], boolean parseTasks)
      throws IOException {
    super(0);
    this.file = null;
    if (!rootDirectoy.isDirectory())
      throw new IllegalArgumentException(
          (new StringBuilder("not a directoy: ")).append(
              rootDirectoy.getAbsolutePath()).toString());
    this.root = rootDirectoy.getCanonicalFile();
    if (parseTasks)
      this.parseTasks(files);
  }

  protected FileTaskImpl(File rootDirectoy, File file, int task)
      throws IOException {
    this(rootDirectoy, file, task, true);
  }

  protected FileTaskImpl(File rootDirectoy, File file, int task,
      boolean parseTasks) throws IOException {
    super(task);
    this.file = null;
    this.file = file.getCanonicalFile();
    if (!rootDirectoy.isDirectory())
      throw new IllegalArgumentException(
          (new StringBuilder("not a directoy: ")).append(
              rootDirectoy.getAbsolutePath()).toString());
    this.root = rootDirectoy;
    if (parseTasks)
      this.parseTasks(file);
  }

  @Override
  public FileTask[] createTasks(Object f) throws IOException {
    File file = (File) f;
    if (file.isDirectory())
      return this.createTasks(((file.listFiles())));
    else
      return new FileTask[0];
  }

  @Override
  public FileTask[] createTasks(Object files[]) throws IOException {
    FileTask arrTasks[] = new FileTask[files.length];
    for (int i = 0; i < files.length; i++) {
      int t;
      if (((File) files[i]).isDirectory())
        t = 2;
      else
        t = 1;
      arrTasks[i] = new FileTaskImpl(this.root, (File) files[i], t);
    }

    return arrTasks;
  }

  @Override
  public String getAbsolutePath() {
    return this.file == null ? this.root.getAbsolutePath() : this.file
        .getAbsolutePath();
  }

  public File getFile() {
    return this.file;
  }

  @Override
  public String getFileName() {
    if (this.file != null)
      return this.file.getName();
    else
      return this.root.getName();
  }

  @Override
  public long getFileSize() {
    return this.file.length();
  }

  @Override
  public String getRelativePath() {
    if (this.file != null) {
      String f_path = this.file.getAbsolutePath();
      String r_path = this.root.getAbsolutePath();
      return f_path.substring(r_path.length()).replace('\\', '/');
    } else {
      return this.root.getAbsolutePath();
    }
  }

  public File getRootDirectory() {
    return this.root;
  }
}
