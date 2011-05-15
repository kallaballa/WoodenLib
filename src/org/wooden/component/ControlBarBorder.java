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

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

public class ControlBarBorder extends AbstractBorder {
  protected class MouseEventDelegator implements MouseListener,
      MouseMotionListener {

    protected MouseEventDelegator() {
      super();
    }

    private boolean belongsToControlBar(MouseEvent e) {
      return e.getY() > ControlBarBorder.this.off
          && e.getX() > ControlBarBorder.this.off
          && e.getX() + ControlBarBorder.this.off < ControlBarBorder.this.controlBar
              .getWidth()
          && e.getY() + ControlBarBorder.this.off < ControlBarBorder.this.controlBar
              .getHeight();
    }

    private void dispatchEvent(MouseEvent e) {
      if (this.belongsToControlBar(e)) {
        e = this.translate(e);
        Component arrCmp[] = ControlBarBorder.this.controlBar.getComponents();
        for (int i = 0; arrCmp != null && i < arrCmp.length; i++)
          if (arrCmp[i].getX() <= e.getX() && arrCmp[i].getWidth() >= e.getX()
              && arrCmp[i].getY() <= e.getY()
              && arrCmp[i].getHeight() >= e.getY()) {
            e.translatePoint(arrCmp[i].getX(), arrCmp[i].getY());
            e.setSource(arrCmp[i]);
            arrCmp[i].dispatchEvent(e);
            ControlBarBorder.this.container
                .paint(ControlBarBorder.this.container.getGraphics());
          }

      }
      ControlBarBorder.this.container.paint(ControlBarBorder.this.container
          .getGraphics());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
      this.dispatchEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      this.dispatchEvent(e);
    }

    private MouseEvent translate(MouseEvent e) {
      e.translatePoint(-ControlBarBorder.this.off, -ControlBarBorder.this.off);
      return e;
    }
  }

  private JComponent controlBar;

  private JComponent container;

  private int off;

  public ControlBarBorder(JComponent container, JComponent controlBar, int off) {
    this.controlBar = controlBar;
    this.container = container;
    this.off = off;
    MouseEventDelegator delegator = new MouseEventDelegator();
    container.addMouseListener(delegator);
    container.addMouseMotionListener(delegator);
  }

  @Override
  public Insets getBorderInsets(Component c) {
    return new Insets(this.controlBar.getHeight(), 0, 0, 0);
  }

  @Override
  public Insets getBorderInsets(Component c, Insets insets) {
    insets.left = insets.right = insets.bottom = 0;
    insets.top = this.controlBar.getHeight();
    return insets;
  }

  public Container getControlBar() {
    return this.controlBar;
  }

  @Override
  public Rectangle getInteriorRectangle(Component c, int x, int y, int width,
      int height) {
    return getInteriorRectangle(c, ((this)), x, y, width, height);
  }

  @Override
  public boolean isBorderOpaque() {
    return true;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width,
      int height) {
    Component arrCmp[] = null;
    this.controlBar.setSize(width, this.controlBar.getHeight());
    g.translate(x, y);
    this.controlBar.printAll(g);
    arrCmp = this.controlBar.getComponents();
    for (int i = 0; arrCmp != null && i < arrCmp.length; i++) {
      g.translate(arrCmp[i].getX(), arrCmp[i].getY());
      arrCmp[i].printAll(g);
      g.translate(-arrCmp[i].getX(), -arrCmp[i].getY());
    }

    g.translate(-x, -y);
  }

  public void setControlBar(JComponent c) {
    this.controlBar = c;
  }

}
