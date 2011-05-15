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
package org.wooden.component;

import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class ConfigurationChooser extends JFileChooser {

  private static final String PROPERTY_RECENTFILE = "choose.recent";

  private JTextField txtPath;

  private Frame parent;

  private int mode;

  public ConfigurationChooser(Frame parent, JTextField txtPath, int mode) {
    this(parent, txtPath, mode, null);
  }

  public ConfigurationChooser(Frame parent, JTextField txtPath, int mode,
      FileFilter filter) {
    if (parent == null)
      throw new IllegalArgumentException("the parent can't be null");
    if (mode != 0 && mode != 1)
      throw new IllegalArgumentException((new StringBuilder("Unknown mode: "))
          .append(mode).toString());
    if (filter != null)
      super.setFileFilter(filter);
    this.mode = mode;
    this.parent = parent;
    this.txtPath = txtPath;
    String path;
    if (txtPath == null || (path = txtPath.getText()) == null) {
      if ((path = System.getProperty("choose.recent")) == null)
        path = ".";
      path = (new File(path)).getAbsolutePath();
    } else {
      path = txtPath.getText();
    }
    File defaultfile = new File(path);
    this.setCurrentDirectory(defaultfile);
    this.setSelectedFile(defaultfile);
    this.setFileSelectionMode(0);
  }

  public File open() {
    int returnVal;
    if (this.mode == 0)
      returnVal = this.showDialog(this.parent, "\uFFFDffnen");
    else
      returnVal = this.showDialog(this.parent, "Speichern");
    if (returnVal == 0) {
      File file = this.getSelectedFile();
      String path = file.getAbsolutePath();
      if (this.txtPath != null)
        this.txtPath.setText(path);
      System.setProperty("choose.recent", path);
      return file;
    } else {
      return null;
    }
  }
}
