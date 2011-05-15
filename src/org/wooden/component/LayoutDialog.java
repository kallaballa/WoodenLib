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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

public abstract class LayoutDialog extends JDialog {

  private JComponent customPane;

  private LayoutPane layoutPane;

  private GridBagLayout layout;

  private GridBagConstraints constrLayout;

  private GridBagConstraints constrConf;

  private boolean vertical;

  private boolean customPaneFirst;

  public LayoutDialog() throws HeadlessException {
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Dialog owner) throws HeadlessException {
    super(owner, false);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Dialog owner, boolean modal) throws HeadlessException {
    super(owner, modal);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Dialog owner, String title) throws HeadlessException {
    super(owner, title);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Dialog owner, String title, boolean modal)
      throws HeadlessException {
    super(owner, title, modal);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Frame owner) throws HeadlessException {
    super(owner);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Frame owner, String title) {
    super(owner, title);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public LayoutDialog(Frame owner, String title, boolean modal,
      GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customPaneFirst = true;
    this.init();
  }

  public void alignPanes() {
    if (this.constrLayout == null) {
      this.constrLayout = new GridBagConstraints();
      this.constrLayout.weightx = 1.0D;
      this.constrLayout.weighty = 1.0D;
      this.constrLayout.fill = 1;
    }
    if (this.constrConf == null) {
      this.constrConf = new GridBagConstraints();
      this.constrConf.weightx = 0.0D;
      this.constrConf.weighty = 0.0D;
      this.constrConf.fill = 1;
    }
    this.alignPanes(this.vertical, this.customPaneFirst, this.constrLayout,
        this.constrConf);
  }

  public void alignPanes(boolean vertical, boolean customFirst,
      GridBagConstraints constrLayout, GridBagConstraints constrConf) {
    this.constrLayout = constrLayout;
    this.constrConf = constrConf;
    Container contentPane = this.getContentPane();
    JComponent panes[] = { this.customPane, this.layoutPane };
    GridBagConstraints constraints[] = { constrLayout, constrConf };
    if (!customFirst) {
      panes = (new JComponent[] { this.layoutPane, this.customPane });
      constraints = (new GridBagConstraints[] { constrConf, constrLayout });
    }
    if (vertical) {
      constraints[0].gridheight = 2;
      constraints[0].gridwidth = 0;
      constraints[1].gridheight = 0;
      constraints[1].gridwidth = 0;
    } else {
      constraints[0].gridwidth = 1;
      constraints[0].gridheight = 1;
      constraints[1].gridwidth = 0;
      constraints[1].gridheight = 1;
    }
    this.layout.removeLayoutComponent(panes[0]);
    this.layout.removeLayoutComponent(panes[1]);
    contentPane.removeAll();
    this.layout.addLayoutComponent(panes[0], constraints[0]);
    this.layout.addLayoutComponent(panes[1], constraints[1]);
    contentPane.add(panes[0]);
    contentPane.add(panes[1]);
    this.updateLayout();
  }

  public LayoutPane getLayoutPane() {
    return this.layoutPane;
  }

  private void init() {
    this.layoutPane = new LayoutPane();
    this.customPane = new JPanel();
    Container contentPane = super.getContentPane();
    contentPane.setLayout(this.layout);
    this.alignPanes();
  }

  public void setCustomPane(JComponent customPane) {
    this.customPane = customPane;
    this.alignPanes();
  }

  public void updateLayout() {
    super.pack();
    super.validate();
  }
}
