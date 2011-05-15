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

package org.wooden.util;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class FinalizerAdapter extends WindowAdapter {

  private static Vector frames = new Vector();

  private static Object mutex = new Object();

  private static FinalizerAdapter finalizer = null;

  public static void addToDisposalQueue(Frame f) {
    synchronized (mutex) {
      if (frames.indexOf(f) < 0) {
        f.addWindowListener(getFinalizerAdapterInstance());
        frames.add(f);
      }
    }
  }

  private static FinalizerAdapter getFinalizerAdapterInstance() {
    if (finalizer == null)
      finalizer = new FinalizerAdapter();
    return finalizer;
  }

  private FinalizerAdapter() {}

  @Override
  public void windowClosing(WindowEvent e) {
    synchronized (mutex) {
      Frame arrFrames[] = (Frame[]) frames.toArray(new Frame[0]);
      for (Frame arrFrame : arrFrames) {
        arrFrame.setVisible(false);
        arrFrame.dispose();
      }

      System.exit(0);
    }
  }

}
