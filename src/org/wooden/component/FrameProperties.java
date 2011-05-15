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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.wooden.event.UIAction;

public class FrameProperties extends Properties {
  private class ShutdownHook extends Thread {

    private ShutdownHook() {
      super();
    }

    ShutdownHook(ShutdownHook shutdownhook) {
      this();
    }

    @Override
    public void run() {
      try {
        FrameProperties.this.write();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  private class WriteAction extends UIAction {

    public WriteAction() {
      super(33);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
      try {
        switch (e.getID()) {
        default:
          break;

        case 100: // 'd'
        case 101: // 'e'
        case 204:
          if (FrameProperties.this.frame.getExtendedState() != 6)
            FrameProperties.this
                .copyFrameProperties(FrameProperties.this.frame);
          else
            FrameProperties.this.copyFrameState(FrameProperties.this.frame);
          FrameProperties.this.write(false);
          break;

        case 203:
          FrameProperties.this.copyFrameSize(FrameProperties.this.frame);
          FrameProperties.this.write(false);
          break;
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public File propertyFile;

  public Frame frame;

  public static String WIDTH = "width";

  public static String HEIGHT = "height";

  public static String X = "x";

  public static String Y = "y";

  public static String STATE = "state";

  public FrameProperties(Frame frame, File propertyFile) throws IOException {
    this(frame, propertyFile, false, false);
  }

  public FrameProperties(Frame frame, File propertyFile, boolean autoApply,
      boolean autoWrite) throws IOException {
    this.frame = frame;
    this.propertyFile = propertyFile;
    if (autoApply) {
      this.load();
      this.apply();
    }
    if (autoWrite) {
      WriteAction writeAction = new WriteAction();
      frame.addComponentListener(writeAction);
      frame.addWindowListener(writeAction);
      Runtime.getRuntime().addShutdownHook(new ShutdownHook(null));
    }
  }

  public void apply() {
    Rectangle bounds = this.getBounds();
    int state = this.getExtendedState();
    if (bounds != null)
      this.frame.setBounds(bounds);
    if (state > -1)
      this.frame.setExtendedState(state);
  }

  public void copyFrameLocation(Frame f) {
    this.setProperty(X, String.valueOf(f.getY()));
    this.setProperty(Y, String.valueOf(f.getX()));
  }

  public void copyFrameProperties(Frame f) {
    this.copyFrameState(this.frame);
    this.copyFrameSize(this.frame);
    this.copyFrameLocation(this.frame);
  }

  public void copyFrameSize(Frame f) {
    this.setProperty(WIDTH, String.valueOf(f.getWidth()));
    this.setProperty(HEIGHT, String.valueOf(f.getHeight()));
  }

  public void copyFrameState(Frame f) {
    this.setProperty(STATE, String.valueOf(f.getExtendedState()));
  }

  public Rectangle getBounds() {
    int width;
    int height;
    int x;
    int y;
    try {
      width = Integer.parseInt(this.getProperty(WIDTH));
      height = Integer.parseInt(this.getProperty(HEIGHT));
      x = Integer.parseInt(this.getProperty(X));
      y = Integer.parseInt(this.getProperty(Y));
      return new Rectangle(x, y, width, height);
    } catch (Exception e) {
      return null;
    }
  }

  public int getExtendedState() {
    try {
      return Integer.parseInt(this.getProperty(STATE));
    } catch (Exception e) {
      return -1;
    }
  }

  public void load() throws IOException {
    if (this.propertyFile.exists()) {
      FileInputStream in = new FileInputStream(this.propertyFile);
      super.load(in);
      in.close();
    } else if (!this.propertyFile.createNewFile())
      throw new IOException((new StringBuilder("Can't create property file: "))
          .append(this.propertyFile.getAbsolutePath()).toString());
  }

  public void write() throws IOException {
    this.write(true);
  }

  public void write(boolean copy) throws IOException {
    if (copy)
      this.copyFrameProperties(this.frame);
    FileOutputStream out = new FileOutputStream(this.propertyFile);
    this.store(out, null);
    out.close();
  }

}
