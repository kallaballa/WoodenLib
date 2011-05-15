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
import java.util.Enumeration;
import java.util.Vector;

public abstract class FileTask extends Vector implements Enumeration {

  public static void main(String args[]) {
    FileTaskImpl f = null;
    try {
      f = new FileTaskImpl(new File("C:/programming/"), new File[] { new File(
          "C:/programming/develop/") });
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    System.out.println(f.totalSize());
  }

  private long totalSize;

  private int index;

  public static final int ROOT_TASK = 0;

  public static final int FILE_TASK = 1;

  public static final int DIRECTORY_TASK = 2;

  private int task;

  protected FileTask(int task) throws IOException {
    this.totalSize = 0L;
    this.index = -1;
    this.task = -1;
    this.task = task;
  }

  public FileTask(Object files[]) throws IOException {
    super(files.length);
    this.totalSize = 0L;
    this.index = -1;
    this.task = -1;
    this.task = 0;
    this.parseTasks(files);
  }

  protected FileTask(Object file, int task) throws IOException {
    super(1);
    this.totalSize = 0L;
    this.index = -1;
    this.task = -1;
    this.task = task;
    if (task == 2)
      this.parseTasks(file);
  }

  protected synchronized void addTask(FileTask task) {
    if (task == null)
      return;
    if (task.getType() == 2)
      this.totalSize += task.totalSize();
    else
      this.totalSize += task.getFileSize();
    this.add(task);
  }

  protected synchronized void addTasks(FileTask tasks[]) {
    if (tasks == null)
      return;
    for (FileTask task2 : tasks)
      this.addTask(task2);

  }

  public int countTasks() {
    int cnt = 0;
    if (this.getType() == 1) {
      cnt = 1;
    } else {
      FileTask arrTasks[] = this.getChildTasks();
      for (FileTask arrTask : arrTasks)
        cnt += arrTask.countTasks();

    }
    return cnt;
  }

  public abstract FileTask[] createTasks(Object aobj[]) throws IOException;

  public abstract FileTask[] createTasks(Object obj) throws IOException;

  public abstract String getAbsolutePath();

  public FileTask[] getChildTasks() {
    return (FileTask[]) this.toArray(new FileTask[0]);
  }

  public abstract String getFileName();

  public abstract long getFileSize();

  public abstract String getRelativePath();

  public int getType() {
    return this.task;
  }

  @Override
  public synchronized boolean hasMoreElements() {
    return this.index + 1 < this.size();
  }

  public boolean hasMoreTasks() {
    return this.hasMoreElements();
  }

  @Override
  public synchronized Object nextElement() {
    return this.get(++this.index);
  }

  public FileTask nextTask() {
    return (FileTask) this.nextElement();
  }

  public void parseTasks(Object files[]) throws IOException {
    this.addTasks(this.createTasks(files));
  }

  public void parseTasks(Object file) throws IOException {
    this.addTasks(this.createTasks(file));
  }

  public synchronized long totalSize() {
    return this.totalSize;
  }
}
