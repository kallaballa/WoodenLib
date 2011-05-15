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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.VolatileImage;

public class ScreenBuffer extends ComponentAdapter {

  private VolatileImage buffer;

  private Component cmp;

  private Color back;

  private boolean copyOnResize;

  private Graphics bufferGraphics;

  public ScreenBuffer(Component cmp, boolean copyOnResize) {
    this(cmp, cmp.getWidth(), cmp.getHeight(), cmp.getBackground(),
        copyOnResize);
  }

  public ScreenBuffer(Component cmp, int w, int h) {
    this(cmp, w, h, null);
  }

  public ScreenBuffer(Component cmp, int w, int h, Color back) {
    this(cmp, w, h, back, false);
  }

  public ScreenBuffer(Component cmp, int w, int h, Color back,
      boolean copyOnResize) {
    this.cmp = cmp;
    this.copyOnResize = copyOnResize;
    this.setBackground(back);
    this.init(cmp, w, h);
    cmp.addComponentListener(this);
  }

  public synchronized void clear() {
    this.clear(this.getBackground());
  }

  public synchronized void clear(Color c) {
    Graphics g = this.buffer.getGraphics();
    int w = this.buffer.getWidth();
    int h = this.buffer.getHeight();
    if (c != null) {
      g.setColor(c);
      g.fillRect(0, 0, w, h);
      g.dispose();
    }
  }

  @Override
  public void componentResized(ComponentEvent e) {
    this.setSize(this.cmp.getWidth(), this.cmp.getHeight());
  }

  public synchronized Color getBackground() {
    return this.back;
  }

  public synchronized VolatileImage getBuffer() {
    return this.buffer;
  }

  public synchronized Graphics getGraphics() {
    return this.bufferGraphics;
  }

  public synchronized int getHeight() {
    return this.buffer.getHeight();
  }

  public synchronized int getWidth() {
    return this.buffer.getWidth();
  }

  private void init(Component cmp, int w, int h) {
    this.setBuffer(cmp.getGraphicsConfiguration()
        .createCompatibleVolatileImage(w, h));
    this.clear();
  }

  public void setBackground(Color b) {
    this.back = b;
  }

  private synchronized void setBuffer(VolatileImage buffer) {
    this.buffer = buffer;
    this.bufferGraphics = buffer.getGraphics();
  }

  public synchronized void setSize(int w, int h) {
    VolatileImage oldImg = this.getBuffer();
    this.init(this.cmp, w, h);
    if (this.copyOnResize && oldImg != null) {
      Graphics g = this.getGraphics();
      g.drawImage(oldImg, 0, 0, this.cmp);
      g.dispose();
    }
  }
}
