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

import java.util.Vector;

public class ProgressTracker extends Thread {
  private class ProgressObjectWrapper {

    private ProgressObject prgObj;

    public ProgressObjectWrapper(ProgressObject prgObj) {
      super();
      this.prgObj = prgObj;
    }

    public int currentPosition() {
      return this.prgObj.currentPosition();
    }

    public Throwable error() {
      return this.prgObj.error();
    }

    public boolean errorOccured() {
      return this.prgObj.errorOccured();
    }

    public boolean hasFinished() {
      return this.errorOccured()
          || this.currentPosition() == this.maxPosition();
    }

    public boolean isRunning() {
      return this.maxPosition() > 0 && !this.hasFinished();
    }

    public int maxPosition() {
      return this.prgObj.maxPosition();
    }

    public ProgressObject progressObject() {
      return this.prgObj;
    }
  }

  private ProgressObjectWrapper prgObj;

  private Vector listeners;

  public ProgressTracker(ProgressObject prgObj) {
    this.prgObj = null;
    this.listeners = new Vector();
    if (prgObj == null) {
      throw new NullPointerException();
    } else {
      this.prgObj = new ProgressObjectWrapper(prgObj);
      return;
    }
  }

  public void addProgressListener(ProgressTrackerListener l) {
    this.listeners.add(l);
  }

  public ProgressTrackerListener[] getProgressListeners() {
    return (ProgressTrackerListener[]) this.listeners
        .toArray(new ProgressTrackerListener[0]);
  }

  @Override
  public void run() {
    try {
      for (; !this.prgObj.isRunning(); Thread.sleep(200L))
        ;
      ProgressTrackerListener arrListeners[] = this.getProgressListeners();
      int lastPos = 0;
      int lastMax = 0;
      int currPos = this.prgObj.currentPosition();
      for (int currMax = this.prgObj.maxPosition(); this.prgObj.isRunning(); currMax = this.prgObj
          .maxPosition()) {
        if (lastPos < currPos || lastMax < currMax) {
          for (ProgressTrackerListener arrListener : arrListeners)
            arrListener.progressEvent(new ProgressTrackerEvent(this.prgObj
                .progressObject(), currPos, currMax, this.prgObj.error()));

        }
        if (this.prgObj.errorOccured())
          break;
        lastPos = currPos;
        lastMax = currMax;
        currPos = this.prgObj.currentPosition();
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
