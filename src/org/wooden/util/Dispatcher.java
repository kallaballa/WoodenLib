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

import org.wooden.io.Sema;

public class Dispatcher extends Thread {

  private boolean terminated;

  private boolean discardOperations;

  private Sema s;

  private DispatcherWorker worker;

  public Dispatcher(DispatcherWorker worker) {
    this("Dispatcher", worker);
  }

  public Dispatcher(String name, DispatcherWorker worker) {
    super(name);
    this.terminated = false;
    this.discardOperations = false;
    this.s = new Sema(0);
    this.worker = worker;
    this.start();
  }

  @Override
  public void finalize() throws Throwable {
    this.terminate();
    super.finalize();
  }

  public boolean isTerminated() {
    return this.terminated;
  }

  public void operate() {
    if (this.isTerminated()) {
      throw new IllegalStateException("Dispatcher was terminated");
    } else {
      this.s.V();
      return;
    }
  }

  @Override
  public void run() {
    while (true) {
      this.s.P();
      if (this.isTerminated())
        return;
      try {
        if (!this.discardOperations || !this.worker.isWorking())
          this.worker.work();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void terminate() {
    this.terminated = true;
    this.s.V();
  }
}
