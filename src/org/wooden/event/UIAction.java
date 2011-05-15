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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.AbstractAction;

public abstract class UIAction extends AbstractAction implements MouseListener,
    KeyListener, FocusListener, WindowListener, MouseMotionListener,
    ComponentListener, TextListener {
  public class FileDropTargetListenerImpl extends FileDropTargetListener {

    private Component cmp;

    public FileDropTargetListenerImpl(Component cmp, int acceptableActions) {
      super(cmp, acceptableActions);
      this.cmp = cmp;
    }

    @Override
    public void filesDropped(File files[]) {
      if (UIAction.this.dropEventsEnabled && UIAction.this.isEnabled())
        UIAction.this.filesDropped(this.cmp, files);
    }
  }

  public static final int COMPONENT_EVENTS = 1;

  public static final int MOUSE_EVENTS = 2;

  public static final int MOUSE_MOTION_EVENTS = 4;

  public static final int KEY_EVENTS = 8;

  public static final int FOCUS_EVENTS = 16;

  public static final int WINDOW_EVENTS = 32;

  public static final int DROP_EVENTS = 64;

  public static final int TEXT_EVENTS = 128;

  public static final int ALL_EVENTS = 255;

  public static final String COMPONENT_RESIZED_ACTION = "componentresized";

  public static final String COMPONENT_MOVED_ACTION = "componentmoved";

  public static final String COMPONENT_SHOWN_ACTION = "componentshown";

  public static final String COMPONENT_HIDDEN_ACTION = "componenthidden";

  public static final String MOUSE_DRAGGED_ACTION = "mousedragged";

  public static final String MOUSE_MOVED_ACTION = "mousemoved";

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

  public static final String WINDOW_ACTIVATED_ACTION = "windowactivated";

  public static final String WINDOW_DEACTIVATED_ACTION = "windowdeactivated";

  public static final String WINDOW_CLOSED_ACTION = "windowclosed";

  public static final String WINDOW_CLOSING_ACTION = "windowclosing";
  public static final String WINDOW_OPENED_ACTION = "windowopened";
  public static final String WINDOW_ICONIFIED_ACTION = "windowiconified";
  public static final String WINDOW_DEICONIFIED_ACTION = "windowdeiconified";
  public static final String TEXT_CHANGED_ACTION = "textChanged";
  private boolean componentEventsEnabled;
  private boolean mouseEventsEnabled;
  private boolean mouseMotionEventsEnabled;
  private boolean keyEventsEnabled;
  private boolean focusEventsEnabled;
  private boolean windowEventsEnabled;
  private boolean dropEventsEnabled;
  private boolean textEventsEnabled;

  public UIAction() {
    this(255);
  }

  public UIAction(int eventmask) {
    this.componentEventsEnabled = false;
    this.mouseEventsEnabled = false;
    this.mouseMotionEventsEnabled = false;
    this.keyEventsEnabled = false;
    this.focusEventsEnabled = false;
    this.windowEventsEnabled = false;
    this.dropEventsEnabled = false;
    this.textEventsEnabled = false;
    if ((eventmask & 1) == 1)
      this.componentEventsEnabled = true;
    if ((eventmask & 2) == 2)
      this.mouseEventsEnabled = true;
    if ((eventmask & 4) == 4)
      this.mouseMotionEventsEnabled = true;
    if ((eventmask & 8) == 8)
      this.keyEventsEnabled = true;
    if ((eventmask & 0x10) == 16)
      this.focusEventsEnabled = true;
    if ((eventmask & 0x20) == 32)
      this.windowEventsEnabled = true;
    if ((eventmask & 0x40) == 64)
      this.dropEventsEnabled = true;
    if ((eventmask & 0x80) == 128)
      this.textEventsEnabled = true;
  }

  @Override
  public void componentHidden(ComponentEvent e) {
    if (this.componentEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "componenthidden"));
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    if (this.componentEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "componentmoved"));
  }

  @Override
  public void componentResized(ComponentEvent e) {
    if (this.componentEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "componentresized"));
  }

  @Override
  public void componentShown(ComponentEvent e) {
    if (this.componentEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "componentshown"));
  }

  public void filesDropped(Component component, File afile[]) {}

  @Override
  public void focusGained(FocusEvent e) {
    if (this.focusEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "focusgained"));
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (this.focusEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "focuslost"));
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (this.keyEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "keypressed"));
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (this.keyEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "keyreleased"));
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (this.keyEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "keytyped"));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (this.mouseEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mouseclicked"));
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (this.mouseMotionEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mousedragged"));
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    if (this.mouseEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mouseentered"));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    if (this.mouseEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mouseexited"));
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (this.mouseMotionEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mousemoved"));
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (this.mouseEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mousepressed"));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (this.mouseEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "mousereleased"));
  }

  public void registerDropTarget(Component cmp, int acceptableActions) {
    new FileDropTargetListenerImpl(cmp, acceptableActions);
  }

  @Override
  public void textValueChanged(TextEvent e) {
    if (this.textEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "textChanged"));
  }

  @Override
  public void windowActivated(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowactivated"));
  }

  @Override
  public void windowClosed(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowclosed"));
  }

  @Override
  public void windowClosing(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowclosing"));
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowdeactivated"));
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowdeiconified"));
  }

  @Override
  public void windowIconified(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowiconified"));
  }

  @Override
  public void windowOpened(WindowEvent e) {
    if (this.windowEventsEnabled && this.isEnabled())
      this.actionPerformed(new ActionEvent(e, e.getID(), "windowopened"));
  }

}
