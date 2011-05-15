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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class FileSelector implements ActionListener {

  private JTextField lbl;

  private JButton btn;

  private JTextField txt;

  private String name;

  private JFrame parentFrame;

  private ConfigurationPane confPane;

  private FileFilter filter;

  public FileSelector(String name, File current, FileFilter filter,
      ConfigurationPane confPane) {
    this(name, current, filter, confPane.findRootFrame());
    if (confPane == null) {
      throw new NullPointerException("ConfigurationPane must not be null");
    } else {
      this.confPane = confPane;
      return;
    }
  }

  public FileSelector(String name, File current, FileFilter filter,
      JFrame parentFrame) {
    this.lbl = new JTextField((new StringBuilder(String.valueOf(name))).append(
        ": ").toString());
    this.btn = new JButton("Select File...");
    this.filter = filter;
    String defaultPath;
    if (current != null)
      defaultPath = current.getAbsolutePath();
    else
      try {
        defaultPath = (new File(".")).getCanonicalPath();
      } catch (IOException ex) {
        defaultPath = System.getProperty("user.home");
      }
    this.txt = new JTextField(defaultPath);
    this.name = name;
    this.parentFrame = parentFrame;
    this.btn.addActionListener(this);
    this.lbl.setEditable(false);
    this.lbl.setBorder(null);
    this.lbl.setHorizontalAlignment(0);
    this.lbl.setFont(new Font(this.lbl.getFont().getFontName(), 1, this.lbl
        .getFont().getSize()));
    this.lbl.setVisible(true);
    this.btn.setVisible(true);
    this.txt.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (this.parentFrame == null)
      this.parentFrame = this.confPane.findRootFrame();
    if (this.parentFrame != null)
      (new ConfigurationChooser(this.parentFrame, this.txt, 0, this.filter))
          .open();
    if (this.confPane != null)
      this.confPane.checkForContinuation();
  }

  public JButton button() {
    return this.btn;
  }

  public JTextField label() {
    return this.lbl;
  }

  public String name() {
    return this.name;
  }

  public JTextField textfield() {
    return this.txt;
  }
}
