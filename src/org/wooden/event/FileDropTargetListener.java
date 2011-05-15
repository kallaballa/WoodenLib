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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;

public abstract class FileDropTargetListener implements DropTargetListener {

  private int acceptableActions;

  public FileDropTargetListener(Component cmp, int a) {
    this.acceptableActions = 0;
    if (a != 0 && a != 1 && a != 2 && a != 3 && a != 0x40000000) {
      throw new IllegalArgumentException((new StringBuilder("action"))
          .append(a).toString());
    } else {
      this.acceptableActions = a;
      new DropTarget(cmp, this.acceptableActions, this, true);
      return;
    }
  }

  @Override
  public void dragEnter(DropTargetDragEvent e) {
    if (!this.isDragOk(e)) {
      e.rejectDrag();
      return;
    } else {
      e.acceptDrag(e.getDropAction());
      return;
    }
  }

  @Override
  public void dragExit(DropTargetEvent droptargetevent) {}

  @Override
  public void dragOver(DropTargetDragEvent e) {
    if (!this.isDragOk(e)) {
      e.rejectDrag();
      return;
    } else {
      e.acceptDrag(e.getDropAction());
      return;
    }
  }

  @Override
  public void drop(DropTargetDropEvent event) {
    try {
      Transferable transferable = event.getTransferable();
      if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        event.acceptDrop(1);
        java.util.List fileList = (java.util.List) transferable
            .getTransferData(DataFlavor.javaFileListFlavor);
        this.filesDropped((File[]) fileList.toArray(new File[0]));
        event.getDropTargetContext().dropComplete(true);
      } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        event.acceptDrop(2);
        String s = (String) transferable
            .getTransferData(DataFlavor.stringFlavor);
        event.getDropTargetContext().dropComplete(true);
      } else {
        event.rejectDrop();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
      event.rejectDrop();
    } catch (UnsupportedFlavorException ufException) {
      ufException.printStackTrace();
      event.rejectDrop();
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent e) {
    if (!this.isDragOk(e)) {
      e.rejectDrag();
      return;
    } else {
      e.acceptDrag(e.getDropAction());
      return;
    }
  }

  public abstract void filesDropped(File afile[]);

  private boolean isDragFlavorSupported(DropTargetDragEvent e) {
    return true;
  }

  private boolean isDragOk(DropTargetDragEvent e) {
    if (!this.isDragFlavorSupported(e))
      return false;
    int da = e.getDropAction();
    return (da & this.acceptableActions) != 0;
  }
}
