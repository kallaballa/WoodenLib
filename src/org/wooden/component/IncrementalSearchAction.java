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
import java.awt.TextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import org.wooden.event.UIAction;
import org.wooden.util.IncrementalSearch;

public class IncrementalSearchAction extends UIAction {

  private IncrementalSearch incrementalSearch;

  public IncrementalSearchAction(IncrementalSearch incrementalSearch) {
    super(8);
    this.incrementalSearch = incrementalSearch;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getActionCommand().equals("keyreleased")) {
      Component cmp = (Component) ((KeyEvent) event.getSource()).getSource();
      String text = null;
      if (cmp instanceof TextComponent)
        text = ((TextComponent) cmp).getText();
      else if (cmp instanceof JTextComponent)
        text = ((JTextComponent) cmp).getText();
      this.incrementalSearch.updateSearch(text);
    }
  }
}
