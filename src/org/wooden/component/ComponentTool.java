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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;

public class ComponentTool {

  public static final int LEFT = 0;

  public static final int CENTER = 1;

  public static final int RIGHT = 2;

  public static final int TOP = 3;

  public static final int BOTTOM = 4;

  public static void centerOnScreen(Window wnd) {
    placeOnScreen(wnd, 1, 1);
  }

  public static VolatileImage drawVolatileImage(Graphics2D g,
      VolatileImage img, int x, int y, Image orig) {
    final int MAX_TRIES = 100;
    for (int i = 0; i < MAX_TRIES; i++) {
      if (img != null) {
        // Draw the volatile image
        g.drawImage(img, x, y, null);

        // Check if it is still valid
        if (!img.contentsLost()) {
          return img;
        }
      } else {
        // Create the volatile image
        img = g.getDeviceConfiguration().createCompatibleVolatileImage(
            orig.getWidth(null), orig.getHeight(null));
      }

      // Determine how to fix the volatile image
      switch (img.validate(g.getDeviceConfiguration())) {
      case VolatileImage.IMAGE_OK:
        // This should not happen
        break;
      case VolatileImage.IMAGE_INCOMPATIBLE:
        // Create a new volatile image object;
        // this could happen if the component was moved to another
        // device
        img.flush();
        img = g.getDeviceConfiguration().createCompatibleVolatileImage(
            orig.getWidth(null), orig.getHeight(null));
      case VolatileImage.IMAGE_RESTORED:
        // Copy the original image to accelerated image memory
        Graphics2D gc = img.createGraphics();
        gc.drawImage(orig, 0, 0, null);
        gc.dispose();
        break;
      }
    }

    // The image failed to be drawn after MAX_TRIES;
    // draw with the non-accelerated image
    g.drawImage(orig, x, y, null);
    return img;
  }

  public static Component findParentComponent(Component cmp, Class parentClass) {
    return findParentComponent(cmp, parentClass, true);
  }

  public static Component findParentComponent(Component cmp, Class parentClass,
      boolean root) {
    Component found = null;
    for (Component parent = null; cmp != null
        && (parent = cmp.getParent()) != null; cmp = parent) {
      if (!parentClass.isInstance(parent))
        continue;
      found = parent;
      if (!root)
        break;
    }

    return found;
  }

  public static Dialog getRootDialog(Component cmp) {
    return (Dialog) findParentComponent(cmp, java.awt.Dialog.class);
  }

  public static Frame getRootFrame(Component cmp) {
    return (Frame) findParentComponent(cmp, java.awt.Frame.class);
  }

  public static Window getRootWindow(Component cmp) {
    return (Window) findParentComponent(cmp, java.awt.Window.class);
  }

  public static void placeOnScreen(Window wnd, int halign, int valign) {
    Dimension screen = wnd.getToolkit().getScreenSize();
    int x;
    if (halign == 1)
      x = (int) (screen.getWidth() / 2D) - wnd.getWidth() / 2;
    else if (halign == 0)
      x = 0;
    else if (halign == 2)
      x = (int) screen.getWidth() - wnd.getWidth();
    else
      throw new IllegalArgumentException((new StringBuilder(
          "Illegal horizontal alignment: ")).append(halign).toString());
    int y;
    if (valign == 1)
      y = (int) (screen.getHeight() / 2D) - wnd.getHeight() / 2;
    else if (valign == 3)
      y = 0;
    else if (halign == 4)
      y = (int) screen.getHeight() - wnd.getHeight();
    else
      throw new IllegalArgumentException((new StringBuilder(
          "Illegal vertical alignment: ")).append(valign).toString());
    wnd.setLocation(x, y);
  }

  public static void setFullScreen(GraphicsDevice device, JFrame frame) {
    boolean isFullScreen = device.isFullScreenSupported();
    frame.setUndecorated(isFullScreen);
    frame.setResizable(!isFullScreen);
    if (isFullScreen && !frame.isDisplayable()) {
      // Full-screen mode
      device.setFullScreenWindow(frame);
      frame.validate();
    } else {
      // Windowed mode
      frame.pack();
      frame.setVisible(true);
    }
  }

  public static void updateLayoutHierachy(Component cmp) {
    Window wnd = getRootWindow(cmp);
    if (wnd != null)
      wnd.validate();
    else
      cmp.validate();
  }

  public ComponentTool() {}
}
