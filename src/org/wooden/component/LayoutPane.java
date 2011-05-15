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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LayoutPane extends JPanel {
  public class ResizeAdapter extends ComponentAdapter {

    public ResizeAdapter() {
      super();
    }

    @Override
    public void componentResized(ComponentEvent e) {
      int width = e.getComponent().getWidth() - 5;
      Component lines[] = (Component[]) LayoutPane.this.layoutLines
          .toArray(new Component[0]);
      for (Component line : lines) {
        Dimension dim = new Dimension(width, line.getHeight());
        line.setSize(dim);
        line.setPreferredSize(dim);
      }

      LayoutPane.this.validate();
    }
  }

  public static final GridBagConstraints SHRINKER;

  public static final GridBagConstraints FILLER;

  private GridBagLayout layout;

  private Vector layoutLines;

  private HashMap layoutComponent;

  static {
    SHRINKER = new GridBagConstraints();
    FILLER = new GridBagConstraints();
    SHRINKER.weightx = 0.0D;
    SHRINKER.insets = new Insets(5, 5, 5, 5);
    SHRINKER.fill = 1;
    FILLER.weightx = 2D;
    FILLER.weighty = 2D;
    FILLER.insets = new Insets(5, 5, 5, 5);
    FILLER.fill = 1;
  }

  public LayoutPane() {
    this.layout = new GridBagLayout();
    this.layoutLines = new Vector();
    this.layoutComponent = new HashMap();
    this.init();
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line) {
    return this.addLayoutComponent(name, cmp, line, 1.0D, 1.0D);
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line,
      double weightx, double weighty) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridheight = 1;
    constraints.gridwidth = 0;
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.fill = 1;
    this.addLayoutComponent(name, cmp, line, constraints);
    return this;
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line,
      GridBagConstraints constraints) {
    this.layoutComponent.put(name, cmp);
    Container l = (Container) this.layoutLines.get(line);
    Component arrCmp[] = l.getComponents();
    GridBagLayout layout = (GridBagLayout) l.getLayout();
    GridBagConstraints tmpConstraints = null;
    for (Component element : arrCmp) {
      tmpConstraints = layout.getConstraints(element);
      tmpConstraints.gridwidth = arrCmp.length + 1;
      layout.setConstraints(element, tmpConstraints);
    }

    constraints.gridwidth = 0;
    layout.addLayoutComponent(cmp, constraints);
    l.add(cmp);
    arrCmp = l.getComponents();
    for (int i = 0; i < arrCmp.length; i++)
      System.out.println((new StringBuilder(String.valueOf(i))).append(": ")
          .append(layout.getConstraints(arrCmp[i]).gridwidth).append(" | ")
          .append(layout.getConstraints(arrCmp[i]).weightx).append(" | ")
          .append(arrCmp[i]).toString());

    System.out.println();
    this.updateLayout();
    return this;
  }

  public LayoutPane addLayoutLine() {
    return this.addLayoutLine(null);
  }

  public LayoutPane addLayoutLine(double weightx, double weighty) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridheight = 1;
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.fill = 1;
    constraints.gridwidth = 0;
    return this.addLayoutLine(constraints);
  }

  public LayoutPane addLayoutLine(GridBagConstraints constraints) {
    JPanel line = new JPanel(new GridBagLayout());
    if (constraints == null) {
      constraints = new GridBagConstraints();
      constraints.gridheight = 1;
      constraints.weightx = 1.0D;
      constraints.weighty = 0.0D;
      constraints.fill = 1;
    }
    constraints.gridwidth = 0;
    this.layout.addLayoutComponent(line, constraints);
    this.layoutLines.add(line);
    this.add(line);
    return this;
  }

  public LayoutPane addLineSpacer(int line, double weightx, double weighty) {
    return this.addLayoutComponent(null, new JPanel(), line, weightx, weighty);
  }

  public LayoutPane addLineSpacer(int line, GridBagConstraints constraints) {
    return this.addLayoutComponent(null, new JPanel(), line, constraints);
  }

  protected JFrame findRootFrame() {
    return (JFrame) ComponentTool.findParentComponent(this,
        javax.swing.JFrame.class);
  }

  public JComponent getLayoutComponent(String name) {
    return (JComponent) this.layoutComponent.get(name);
  }

  public Container getLayoutLine(int line) {
    return (Container) this.layoutLines.get(line);
  }

  public Component[] getLayoutLines() {
    return (Component[]) this.layoutLines.toArray(new Component[0]);
  }

  private void init() {
    this.setLayout(this.layout);
    this.addComponentListener(new ResizeAdapter());
    this.updateLayout();
  }

  public void setLayoutLineColor(Color fore, Color back, int line) {
    Container l = (Container) this.layoutLines.get(line);
    if (back != null)
      l.setBackground(back);
    if (fore != null)
      l.setForeground(fore);
  }

  public void setRecursiveColor(Container cont, Color fore, Color back) {
    Vector cmps = new Vector();
    Vector conts = new Vector();
    Component arrCmps[] = cont.getComponents();
    for (Component component : arrCmps) {
      if (component instanceof Component)
        cmps.add(component);
      else if (component instanceof Container)
        conts.add(component);
    }

    for (int i = 0; i < conts.size(); i++) {
      Container container = (Container) conts.get(i);
      arrCmps = container.getComponents();
      for (int j = 0; j < arrCmps.length;) {
        Component component = arrCmps[i];
        cmps.add(component);
        if (component instanceof Container)
          conts.add(component);
        i++;
      }

    }

    arrCmps = (Component[]) cmps.toArray(new Component[0]);
    Container arrConts[] = (Container[]) conts.toArray(new Container[0]);
    for (Component arrCmp : arrCmps) {
      if (back != null)
        arrCmp.setBackground(back);
      if (fore != null)
        arrCmp.setForeground(fore);
    }

    for (Container arrCont : arrConts) {
      if (back != null)
        arrCont.setBackground(back);
      if (fore != null)
        arrCont.setForeground(fore);
    }

  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
  }

  public void updateLayout() {
    super.validate();
  }

}
