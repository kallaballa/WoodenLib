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
package org.wooden.component.untested;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.wooden.component.ControlBarBorder;
import org.wooden.event.ComponentDragAdapter;
import org.wooden.event.ComponentResizeAdapter;

public class CustomizableFrame extends JFrame {

  public static void main(String args[]) {
    CustomizableFrame f = new CustomizableFrame("hallo");
    f.setVisible(true);
    f.setSize(400, 300);
    f.setLocation(400, 300);
  }

  private ComponentResizeAdapter resizer;

  private JPanel controlBar;

  private JPanel contentPane;

  public CustomizableFrame(String title) throws HeadlessException {
    super(title);
    this.controlBar = new JPanel();
    this.resizer = new ComponentResizeAdapter(this);
    this.controlBar.setSize(400, 100);
    this.controlBar.setBackground(Color.RED);
    this.controlBar.setLayout(null);
    this.controlBar.addMouseMotionListener(new ComponentDragAdapter(this));
    JButton b = new JButton("HALLO");
    b.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println(e);
      }
    });
    b.setSize(100, 25);
    b.setLocation(10, 10);
    this.controlBar.add(b);
    this.contentPane = (JPanel) this.getContentPane();
    this.contentPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
        .createEtchedBorder(0), new ControlBarBorder(this.contentPane,
        this.controlBar, 5)));
    this.contentPane.addMouseListener(this.resizer);
    this.contentPane.addMouseMotionListener(this.resizer);
    this.doLayout();
    this.pack();
    this.validate();
  }

  @Override
  public void paint(Graphics g) {
    if (!this.resizer.isActive())
      super.paint(g);
  }

  @Override
  public void print(Graphics g) {
    if (!this.resizer.isActive())
      super.print(g);
  }

  @Override
  public void setContentPane(Container c) {
    this.contentPane.removeAll();
    this.contentPane.add(c);
  }

  @Override
  public void update(Graphics g) {
    if (!this.resizer.isActive())
      super.update(g);
  }
}
