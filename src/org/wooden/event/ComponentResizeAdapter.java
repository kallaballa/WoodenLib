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

package org.wooden.event;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public class ComponentResizeAdapter extends MouseMotionAdapter implements
    MouseListener {

  private Component resizeComponent;

  private Object mutex;

  private Point origin;

  private Dimension size;

  private Point location;

  private boolean isUpperLeftActive;

  private boolean isBottomRightActive;

  public ComponentResizeAdapter(Component cmp) {
    this.mutex = new Object();
    this.origin = null;
    this.size = null;
    this.location = null;
    this.isUpperLeftActive = false;
    this.isBottomRightActive = false;
    this.resizeComponent = cmp;
  }

  public boolean isActive() {
    synchronized (this.mutex) {
      return this.isUpperLeftActive || this.isBottomRightActive;
    }
  }

  @Override
  public void mouseClicked(MouseEvent mouseevent) {}

  @Override
  public void mouseDragged(MouseEvent e) {
    synchronized (this.mutex) {
      Point current = e.getPoint();
      Component src = e.getComponent();
      Insets insets;
      if (src instanceof JComponent)
        insets = ((JComponent) src).getBorder().getBorderInsets(src);
      else
        insets = new Insets(0, 0, 0, 0);
      int top = insets.top;
      int left = insets.left;
      int bottom = insets.bottom;
      int right = insets.right;
      int width = this.resizeComponent.getWidth();
      int height = this.resizeComponent.getHeight();
      if ((current.x > width - right || current.y > height - bottom || this.isBottomRightActive)
          && !this.isUpperLeftActive) {
        if (this.origin != null) {
          this.isBottomRightActive = true;
          int diffX = this.origin.x - current.x;
          int diffY = this.origin.y - current.y;
          int w = (int) this.size.getWidth() - diffX;
          int h = (int) this.size.getWidth() - diffY;
          if (w > 100 && h > 100)
            this.resizeComponent.setSize(w, h);
        }
      } else if ((current.y <= top || current.x <= left || this.isUpperLeftActive)
          && !this.isBottomRightActive && this.origin != null) {
        this.isUpperLeftActive = true;
        int diffX = current.x - this.origin.x;
        int diffY = current.y - this.origin.y;
        int w = width - diffX;
        int h = height - diffY;
        if (w > 100 && h > 100) {
          this.resizeComponent.setLocation(this.location.x + diffX,
              this.location.y + diffY);
          this.location = this.resizeComponent.getLocation();
          this.resizeComponent.setSize(w, h);
        }
      }
    }
  }

  @Override
  public void mouseEntered(MouseEvent mouseevent) {}

  @Override
  public void mouseExited(MouseEvent mouseevent) {}

  @Override
  public void mousePressed(MouseEvent e) {
    synchronized (this.mutex) {
      Point current = e.getPoint();
      if (current.y < 6 || current.x < 6
          || current.x > this.resizeComponent.getWidth() - 5
          || current.y > this.resizeComponent.getHeight() - 5) {
        this.origin = current;
        this.size = this.resizeComponent.getSize();
        this.location = this.resizeComponent.getLocation();
        this.resizeComponent.setIgnoreRepaint(true);
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    synchronized (this.mutex) {
      this.origin = null;
      this.location = null;
      this.size = null;
      int w = this.resizeComponent.getWidth();
      int h = this.resizeComponent.getHeight();
      this.isUpperLeftActive = false;
      this.isBottomRightActive = false;
      if (w < 100)
        w = 110;
      if (h < 100)
        h = 110;
      this.resizeComponent.setSize(w, h);
      this.resizeComponent.setIgnoreRepaint(false);
      this.resizeComponent.validate();
      this.resizeComponent.update(this.resizeComponent.getGraphics());
    }
  }
}
