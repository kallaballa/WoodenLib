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

import java.util.EventObject;

public class ProgressTrackerEvent extends EventObject {

  private ProgressObject prgObj;

  private Throwable error;

  private int currPos;

  private int maxPos;

  public ProgressTrackerEvent(ProgressObject prgObj, int currPos, int maxPos,
      Throwable error) {
    super(prgObj);
    this.prgObj = null;
    this.error = null;
    this.currPos = -1;
    this.maxPos = -1;
    this.prgObj = prgObj;
    this.error = error;
    this.currPos = currPos;
    this.maxPos = maxPos;
  }

  public int currentPosition() {
    return this.currPos;
  }

  public Throwable error() {
    return this.error;
  }

  public boolean errorOccured() {
    return this.error != null;
  }

  public int maxPosition() {
    return this.maxPos;
  }

  public ProgressObject progressObject() {
    return this.prgObj;
  }

  @Override
  public String toString() {
    return (new StringBuilder("PrgObj: "))
        .append(this.progressObject().getClass()).append(", currPos: ")
        .append(this.currentPosition()).append(", maxPos: ")
        .append(this.maxPosition()).append(", error: ").append(this.error())
        .toString();
  }
}
