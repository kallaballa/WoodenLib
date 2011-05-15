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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;

public abstract class ConfigurationFrame extends JFrame {
  private Object queryLock;

  private JComponent customPane;
  private ConfigurationPane confPane;
  private GridBagLayout layout;
  private GridBagConstraints constrCustom;
  private GridBagConstraints constrConf;
  private boolean vertical;
  private boolean customFirst;

  public ConfigurationFrame() {
    this.queryLock = new Object();
    this.layout = new GridBagLayout();
    this.vertical = true;
    this.customFirst = true;
  }
}
