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
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class SplitLayoutFrame extends JFrame {

  private ConfigurationPane confPane = new ConfigurationPane() {

    @Override
    public void test() throws IllegalArgumentException {}
  };

  private LayoutPane layoutPane;

  private static GridBagConstraints CONSTR_LAYOUT;

  private static GridBagConstraints CONSTR_CONF;

  static {
    CONSTR_LAYOUT = new GridBagConstraints();
    CONSTR_LAYOUT.weightx = 1.0D;
    CONSTR_LAYOUT.weighty = 1.0D;
    CONSTR_LAYOUT.fill = 1;
    CONSTR_CONF = new GridBagConstraints();
    CONSTR_CONF.weightx = 0.0D;
    CONSTR_CONF.weighty = 0.0D;
    CONSTR_CONF.fill = 1;
  }

  public SplitLayoutFrame(GraphicsConfiguration gc) {
    super(gc);
    this.layoutPane = new LayoutPane();
    this.init();
  }

  public SplitLayoutFrame(String title) throws HeadlessException {
    super(title);
    this.layoutPane = new LayoutPane();
    this.init();
  }

  public ConfigurationPane getConfigurationPane() {
    return this.confPane;
  }

  public LayoutContentPane getLayoutContentPane() {
    Container contentPane = super.getContentPane();
    if (contentPane instanceof LayoutContentPane)
      return (LayoutContentPane) super.getContentPane();
    else
      return null;
  }

  public LayoutPane getLayoutPane() {
    return this.layoutPane;
  }

  private void init() {
    super.setContentPane(new LayoutContentPane(0, 2));
    LayoutContentPane lcp = this.getLayoutContentPane();
    lcp.addLayoutPane(this.layoutPane, CONSTR_LAYOUT);
    lcp.addLayoutPane(this.confPane, CONSTR_CONF);
    lcp.updateLayout();
  }

  public void setConfigurationPane(ConfigurationPane confPane) {
    this.confPane = confPane;
    this.updateLayout();
  }

  @Override
  public void setContentPane(Container container) {
    throw new UnsupportedOperationException(
        "Replacing the content pane of SplitLayoutFrame is not allowed");
  }

  public void setLayoutPane(LayoutPane layoutPane) {
    this.layoutPane = layoutPane;
    this.updateLayout();
  }

  public void updateLayout() {
    LayoutContentPane lcp = this.getLayoutContentPane();
    if (lcp != null)
      lcp.updateLayout();
  }
}
