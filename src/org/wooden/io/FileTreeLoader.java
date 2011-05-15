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
import java.util.Stack;

import javax.swing.filechooser.FileFilter;

public class FileTreeLoader extends BoundedObjectBuffer {
  private class Loader extends Thread {

    private Stack currentNodes;

    private FileFilter filters[];

    private File root;

    public Loader(File root) {
      this(root, null);
    }

    public Loader(File root, FileFilter filters[]) {
      super();
      this.currentNodes = new Stack();
      this.filters = null;
      this.root = null;
      this.root = root;
      this.filters = filters;
    }

    public boolean acceptFile(File f) {
      if (this.filters == null)
        return true;
      for (FileFilter filter : this.filters)
        if (filter.accept(f))
          return true;

      return false;
    }

    public void clear() {
      this.currentNodes.clear();
    }

    @Override
    public void run() {
      try {
        this.currentNodes.push(this.root);
        while (FileTreeLoader.this.isRunning() && !this.currentNodes.isEmpty()) {
          File currentFiles[] = ((File) this.currentNodes.pop()).listFiles();
          for (File currentFile : currentFiles) {
            if (currentFile.isDirectory())
              this.currentNodes.push(currentFile);
            if (this.acceptFile(currentFile))
              FileTreeLoader.this.queueFile(currentFile);
          }

        }
        FileTreeLoader.this.terminate(this);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public static void main(String args[]) {
    try {
      File files[] = { new File(""), new File(""), new File("") };
      FileTreeLoader ftl = new FileTreeLoader(files);
      ftl.load(new ExtensionFileFilter[] { new ExtensionFileFilter("", false) });
      Thread.sleep(3000L);
      int cnt = 0;
      for (; ftl.isRunning(); System.out.println(ftl.nextFile()
          .getAbsolutePath()))
        cnt++;

      System.out.println(cnt);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private File rootDirs[];

  private Loader loaders[];

  public FileTreeLoader(File rootDirs[]) {
    this(rootDirs, 200);
  }

  public FileTreeLoader(File rootDir) {
    this(rootDir, 200);
  }

  public FileTreeLoader(File rootDirs[], int buffersize) {
    super(buffersize, "FTL");
    this.rootDirs = null;
    this.loaders = null;
    for (File rootDir : rootDirs)
      if (rootDir.isFile())
        throw new IllegalArgumentException((new StringBuilder(
            "The root file not a directory: ")).append(
            rootDir.getAbsolutePath()).toString());

    this.rootDirs = rootDirs;
  }

  public FileTreeLoader(File rootDir, int buffersize) {
    this(new File[] { rootDir }, buffersize);
  }

  public boolean isRunning() {
    if (this.loaders == null)
      return false;
    for (Loader loader : this.loaders)
      if (loader != null && loader.isAlive())
        return true;

    return false;
  }

  public synchronized void load() {
    this.load(null);
  }

  public synchronized void load(FileFilter filters[]) {
    if (this.isRunning())
      throw new IllegalStateException("Already loading file stack");
    this.loaders = new Loader[this.rootDirs.length];
    for (int i = 0; i < this.rootDirs.length; i++) {
      this.loaders[i] = new Loader(this.rootDirs[i], filters);
      this.loaders[i].start();
    }

  }

  public File nextFile() {
    if (!this.isRunning() && this.empty())
      return null;
    else
      return (File) this.get();
  }

  private void queueFile(File f) {
    this.put(f);
  }

  public synchronized void terminate(Loader l) {
    l.clear();
    for (int i = 0; i < this.loaders.length; i++)
      if (this.loaders[i] != null && this.loaders[i].equals(l))
        this.loaders[i] = null;

  }

  public synchronized void terminateAll() {
    for (int i = 0; i < this.loaders.length; i++)
      if (this.loaders[i] != null) {
        this.loaders[i].clear();
        this.loaders[i] = null;
      }

    this.put(null);
  }

  public void waitFor() {
    if (!this.isRunning())
      throw new IllegalStateException("File stack is currently not loading");
    try {
      for (Loader loader : this.loaders)
        loader.join();

    } catch (InterruptedException interruptedexception) {}
  }

}
