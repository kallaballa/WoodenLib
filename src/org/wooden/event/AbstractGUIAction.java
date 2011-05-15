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
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.AbstractAction;

public abstract class AbstractGUIAction extends AbstractAction implements
    MouseListener, KeyListener, FocusListener {
  public class FileDropTargetListenerImpl extends FileDropTargetListener {

    private Component cmp;

    public FileDropTargetListenerImpl(Component cmp, int acceptableActions) {
      super(cmp, acceptableActions);
      this.cmp = cmp;
    }

    @Override
    public void filesDropped(File files[]) {
      AbstractGUIAction.this.filesDropped(this.cmp, files);
    }
  }

  public static final String MOUSE_CLICKED_ACTION = "mouseclicked";

  public static final String MOUSE_PRESSED_ACTION = "mousepressed";

  public static final String MOUSE_RELEASED_ACTION = "mousereleased";

  public static final String MOUSE_ENTERED_ACTION = "mouseentered";

  public static final String MOUSE_EXITED_ACTION = "mouseexited";

  public static final String KEY_PRESSED_ACTION = "keypressed";

  public static final String KEY_RELEASED_ACTION = "keyreleased";

  public static final String KEY_TYPED_ACTION = "keytyped";

  public static final String FOCUS_GAINED_ACTION = "focusgained";

  public static final String FOCUS_LOST_ACTION = "focuslost";

  public AbstractGUIAction() {}

  public void filesDropped(Component component, File afile[]) {}

  @Override
  public void focusGained(FocusEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "focusgained"));
  }

  @Override
  public void focusLost(FocusEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "focuslost"));
  }

  @Override
  public void keyPressed(KeyEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "keypressed"));
  }

  @Override
  public void keyReleased(KeyEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "keyreleased"));
  }

  @Override
  public void keyTyped(KeyEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "keytyped"));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "mouseclicked"));
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "mouseentered"));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "mouseexited"));
  }

  @Override
  public void mousePressed(MouseEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "mousepressed"));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    this.actionPerformed(new ActionEvent(e, e.getID(), "mousereleased"));
  }

  public void registerDropTarget(Component cmp, int acceptableActions) {
    new FileDropTargetListenerImpl(cmp, acceptableActions);
  }
}
