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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class StringTransferable implements Transferable, ClipboardOwner {

  public static final DataFlavor plainTextFlavor;

  public static final DataFlavor localStringFlavor;

  public static final DataFlavor flavors[];

  private static final java.util.List flavorList;

  private String string;

  static {
    plainTextFlavor = DataFlavor.plainTextFlavor;
    localStringFlavor = DataFlavor.stringFlavor;
    flavors = (new DataFlavor[] { plainTextFlavor, localStringFlavor });
    flavorList = Arrays.asList(flavors);
  }

  public StringTransferable(String string) {
    this.string = string;
  }

  private void dumpFlavor(DataFlavor flavor) {
    System.out.println((new StringBuilder("getMimeType ")).append(
        flavor.getMimeType()).toString());
    System.out.println((new StringBuilder("getHumanPresentableName ")).append(
        flavor.getHumanPresentableName()).toString());
    System.out.println((new StringBuilder("getRepresentationClass ")).append(
        flavor.getRepresentationClass().getName()).toString());
    System.out.println((new StringBuilder("isMimeTypeSerializedObject "))
        .append(flavor.isMimeTypeSerializedObject()).toString());
    System.out.println((new StringBuilder("isRepresentationClassInputStream "))
        .append(flavor.isRepresentationClassInputStream()).toString());
    System.out
        .println((new StringBuilder("isRepresentationClassSerializable "))
            .append(flavor.isRepresentationClassSerializable()).toString());
    System.out.println((new StringBuilder("isRepresentationClassRemote "))
        .append(flavor.isRepresentationClassRemote()).toString());
    System.out.println((new StringBuilder("isFlavorSerializedObjectType "))
        .append(flavor.isFlavorSerializedObjectType()).toString());
    System.out.println((new StringBuilder("isFlavorRemoteObjectType ")).append(
        flavor.isFlavorRemoteObjectType()).toString());
    System.out.println((new StringBuilder("isFlavorJavaFileListType ")).append(
        flavor.isFlavorJavaFileListType()).toString());
  }

  @Override
  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    System.err.println("getTransferData(): ");
    this.dumpFlavor(flavor);
    return new ByteArrayInputStream(this.string.getBytes("Unicode"));
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return true;
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    System.out.println((new StringBuilder(
        "StringTransferable lost ownership of ")).append(clipboard.getName())
        .toString());
    System.out.println((new StringBuilder("data: ")).append(contents)
        .toString());
  }

  @Override
  public String toString() {
    return "StringTransferable";
  }
}
