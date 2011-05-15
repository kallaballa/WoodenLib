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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

public class LayoutContentPane extends Container {
  public static final int VERTICAL_ALIGN = 0;

  public static final int HORIZONTAL_ALIGN = 1;

  public static final int DEFAULT_ORDER = 2;

  public static final int REVERSE_ORDER = 3;

  private GridBagLayout layout;

  private Vector constraints;

  private Vector layoutPanes;

  private int alignmentDirective;

  private int orderDirective;

  public LayoutContentPane(int alignmentDirective, int orderDirective) {
    this.layout = new GridBagLayout();
    this.constraints = new Vector();
    this.layoutPanes = new Vector();
    this.alignmentDirective = 0;
    this.orderDirective = 2;
    this.alignmentDirective = alignmentDirective;
    this.orderDirective = orderDirective;
    this.setLayout(this.layout);
    this.updateLayout();
  }

  public synchronized void addLayoutPane(LayoutPane layoutPane,
      GridBagConstraints constr) {
    this.layoutPanes.add(layoutPane);
    this.constraints.add(constr);
  }

  public void alignPanes() {
    this.alignPanes(this.getAlignmentDirective(), this.getOrderDirective());
  }

  public synchronized void alignPanes(int aligmentDirective, int orderDirective) {
    LayoutPane panes[] = this.getLayoutPanes();
    GridBagConstraints constraints[] = this.getConstraints();
    boolean vertical = this.getAlignmentDirective() == 0;
    boolean defaultOrder = this.getOrderDirective() == 2;
    int cnt = panes.length;
    int gridheight = vertical ? cnt : 0;
    int gridwidth = vertical ? 0 : cnt;
    this.removeAll();
    for (int i = 0; i < cnt; i++) {
      LayoutPane lp;
      GridBagConstraints c;
      if (defaultOrder) {
        lp = panes[i];
        c = constraints[i];
      } else {
        lp = panes[cnt - 1 - i];
        c = constraints[cnt - 1 - i];
      }
      if (i == cnt - 1)
        if (vertical)
          gridheight = 0;
        else
          gridwidth = 0;
      c.gridheight = gridheight;
      c.gridwidth = gridwidth;
      this.layout.removeLayoutComponent(lp);
      this.layout.addLayoutComponent(lp, c);
      this.add(lp);
    }

  }

  public int getAlignmentDirective() {
    return this.alignmentDirective;
  }

  public synchronized GridBagConstraints[] getConstraints() {
    return (GridBagConstraints[]) this.constraints
        .toArray(new GridBagConstraints[0]);
  }

  public synchronized LayoutPane[] getLayoutPanes() {
    return (LayoutPane[]) this.layoutPanes.toArray(new LayoutPane[0]);
  }

  public int getOrderDirective() {
    return this.orderDirective;
  }

  public synchronized void removeLayoutPane(int i) {
    this.layoutPanes.remove(i);
    this.constraints.remove(i);
  }

  public synchronized void removeLayoutPane(LayoutPane layoutPane) {
    this.removeLayoutPane(this.layoutPanes.indexOf(layoutPane));
  }

  public int setAlignmentDirective(int alignmentDirective) {
    return alignmentDirective = alignmentDirective;
  }

  public int setOrderDirective(int orderDirective) {
    return orderDirective = orderDirective;
  }

  public synchronized void updateLayout() {
    this.alignPanes();
    this.validate();
  }
}
