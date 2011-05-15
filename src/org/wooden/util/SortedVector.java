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

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class SortedVector extends Vector {

  private Object sort_lock;

  private boolean enableSorting;

  public SortedVector() {
    this.sort_lock = new Object();
    this.enableSorting(true);
  }

  public SortedVector(Collection c, boolean enableSorting) {
    super(c);
    this.sort_lock = new Object();
    this.enableSorting(enableSorting);
    this.sort();
  }

  public SortedVector(int initialCapacity) {
    this(initialCapacity, 0, true);
  }

  public SortedVector(int initialCapacity, int capacityIncrement) {
    this(initialCapacity, capacityIncrement, true);
  }

  public SortedVector(int initialCapacity, int capacityIncrement,
      boolean enableSorting) {
    super(initialCapacity, capacityIncrement);
    this.sort_lock = new Object();
    this.enableSorting(enableSorting);
  }

  @Override
  public void add(int index, Object o) {
    synchronized (this.getSortLock()) {
      super.add(index, o);
      this.sort();
    }
  }

  @Override
  public boolean add(Object o) {
    synchronized (this.getSortLock()) {
      boolean b;

      b = super.add(o);
      this.sort();
      return b;
    }
  }

  @Override
  public boolean addAll(Collection c) {
    synchronized (this.getSortLock()) {
      boolean b;
      b = super.addAll(c);
      this.sort();
      return b;
    }
  }

  @Override
  public boolean addAll(int index, Collection c) {
    synchronized (this.getSortLock()) {
      boolean b;
      b = super.addAll(index, c);
      this.sort();
      return b;
    }
  }

  @Override
  public void addElement(Object o) {
    synchronized (this.getSortLock()) {
      super.addElement(o);
      this.sort();
    }
  }

  public void enableSorting(boolean enableSorting) {
    synchronized (this.getSortLock()) {
      this.enableSorting = enableSorting;
    }
  }

  protected Object getSortLock() {
    return this.sort_lock;
  }

  @Override
  public void insertElementAt(Object obj, int index) {
    synchronized (this.getSortLock()) {
      super.insertElementAt(obj, index);
      this.sort();
    }
  }

  public boolean isSortingEnabled() {
    synchronized (this.getSortLock()) {
      return this.enableSorting;
    }
  }

  @Override
  public Object set(int index, Object element) {
    synchronized (this.getSortLock()) {
      Object o;
      o = super.set(index, element);
      this.sort();
      return o;
    }
  }

  @Override
  public void setElementAt(Object element, int index) {
    synchronized (this.getSortLock()) {
      super.setElementAt(element, index);
      this.sort();
    }
  }

  protected void sort() {
    synchronized (this.getSortLock()) {
      if (this.isSortingEnabled()) {
        Object arrTmp[] = new Object[this.size()];
        System.arraycopy(((this.elementData)), 0, ((arrTmp)), 0, arrTmp.length);
        Arrays.sort(arrTmp);
        System.arraycopy(((arrTmp)), 0, ((this.elementData)), 0, arrTmp.length);
      }
    }
  }
}
