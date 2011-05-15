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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class ComponentDragAdapter implements MouseMotionListener {

  private Point dragLocation;

  private Component dragComponent;

  public ComponentDragAdapter(Component dragComponent) {
    this.dragLocation = null;
    this.dragComponent = dragComponent;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Component src = e.getComponent();
    JComponent jsrc = null;
    if (src instanceof JComponent)
      jsrc = (JComponent) src;
    if (this.dragLocation == null) {
      if (jsrc != null)
        this.dragLocation = new Point(jsrc.getWidth() / 2, jsrc.getBorder()
            .getBorderInsets(jsrc).top / 2);
      else
        this.dragLocation = new Point(src.getWidth() / 2, 5);
    } else {
      this.dragComponent.setLocation((int) ((this.dragComponent.getX() + e
          .getX()) - this.dragLocation.getX()), (int) ((this.dragComponent
          .getY() + e.getY()) - this.dragLocation.getY()));
    }
  }

  @Override
  public void mouseMoved(MouseEvent mouseevent) {}
}
