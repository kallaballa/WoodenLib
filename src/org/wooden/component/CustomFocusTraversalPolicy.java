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
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.FocusManager;

public class CustomFocusTraversalPolicy extends FocusTraversalPolicy {

  private HashMap cycleRoots;

  private FocusTraversalPolicy parentPolicy;

  private Container defaultCycleRoot;

  private boolean delegateToParent;

  public CustomFocusTraversalPolicy() {
    this(FocusManager.getCurrentManager().getDefaultFocusTraversalPolicy(),
        true);
  }

  public CustomFocusTraversalPolicy(boolean delegateToParent) {
    this(FocusManager.getCurrentManager().getDefaultFocusTraversalPolicy(),
        delegateToParent);
  }

  public CustomFocusTraversalPolicy(FocusTraversalPolicy parentPolicy,
      boolean delegateToParent) {
    this.cycleRoots = new HashMap();
    this.parentPolicy = parentPolicy;
    this.delegateToParent = delegateToParent;
  }

  public void addComponent(Component aComponent) {
    this.getFocusCycle(this.defaultCycleRoot, true).add(aComponent);
  }

  public void addComponent(Container focusCycleRoot, Component aComponent) {
    this.getFocusCycle(focusCycleRoot, true).add(aComponent);
  }

  public Component getComponentAfter(Component aComponent) {
    return this.getComponentAfter(this.defaultCycleRoot, aComponent);
  }

  @Override
  public Component getComponentAfter(Container focusCycleRoot,
      Component aComponent) {
    Component cmp = null;
    Vector cycle = this.getFocusCycle(focusCycleRoot);
    int index;
    int size;
    if (cycle != null && (size = cycle.size()) != 0
        && (index = cycle.indexOf(aComponent)) != -1) {
      index = index >= size ? 0 : index + 1;
      cmp = (Component) cycle.get(index);
    } else if (this.delegateToParent)
      this.getComponentAfter(focusCycleRoot, aComponent);
    return cmp;
  }

  public Component getComponentBefore(Component aComponent) {
    return this.getComponentAfter(this.defaultCycleRoot, aComponent);
  }

  @Override
  public Component getComponentBefore(Container focusCycleRoot,
      Component aComponent) {
    Component cmp = null;
    Vector cycle = this.getFocusCycle(focusCycleRoot);
    int index;
    int size;
    if (cycle != null && (size = cycle.size()) != 0
        && (index = cycle.indexOf(aComponent)) != -1) {
      index = index <= 0 ? size - 1 : index - 1;
      cmp = (Component) cycle.get(index);
    } else if (this.delegateToParent)
      this.getComponentBefore(focusCycleRoot, aComponent);
    return cmp;
  }

  @Override
  public Component getDefaultComponent(Container focusCycleRoot) {
    return this.parentPolicy.getDefaultComponent(focusCycleRoot);
  }

  public Component getFirstComponent() {
    return this.getFirstComponent(this.defaultCycleRoot);
  }

  @Override
  public Component getFirstComponent(Container focusCycleRoot) {
    Component cmp = null;
    Vector cycle = this.getFocusCycle(focusCycleRoot);
    if (cycle != null && cycle.size() != 0)
      cmp = (Component) cycle.get(0);
    else if (this.delegateToParent)
      this.getFirstComponent(focusCycleRoot);
    return cmp;
  }

  private Vector getFocusCycle(Container focusCycleRoot) {
    return this.getFocusCycle(focusCycleRoot, false);
  }

  private Vector getFocusCycle(Container focusCycleRoot, boolean create) {
    Vector cycle = (Vector) this.cycleRoots.get(focusCycleRoot);
    if (cycle == null && create) {
      cycle = new Vector();
      this.cycleRoots.put(focusCycleRoot, cycle);
    }
    return cycle;
  }

  public Component getLastComponent() {
    return this.getLastComponent(this.defaultCycleRoot);
  }

  @Override
  public Component getLastComponent(Container focusCycleRoot) {
    Component cmp = null;
    Vector cycle = this.getFocusCycle(focusCycleRoot);
    int size;
    if (cycle != null && (size = cycle.size()) != 0)
      cmp = (Component) cycle.get(size - 1);
    else if (this.delegateToParent)
      this.getLastComponent(focusCycleRoot);
    return cmp;
  }

  public void insertComponent(Component aComponent, int index) {
    this.getFocusCycle(this.defaultCycleRoot, true).insertElementAt(aComponent,
        index);
  }

  public void insertComponent(Container focusCycleRoot, Component aComponent,
      int index) {
    this.getFocusCycle(focusCycleRoot, true).insertElementAt(aComponent, index);
  }

  public void insertComponentAfter(Component indexComponent,
      Component aComponent) {
    this.insertComponentAfter(this.defaultCycleRoot, indexComponent, aComponent);
  }

  public void insertComponentAfter(Container focusCycleRoot,
      Component indexComponent, Component aComponent) {
    Vector cycle = this.getFocusCycle(focusCycleRoot, true);
    int index;
    if (cycle != null && (index = cycle.indexOf(indexComponent)) != -1) {
      index = index >= cycle.size() ? 0 : index + 1;
      this.insertComponent(focusCycleRoot, aComponent, index);
    }
  }

  public void insertComponentBefore(Component indexComponent,
      Component aComponent) {
    this.insertComponentBefore(this.defaultCycleRoot, indexComponent,
        aComponent);
  }

  public void insertComponentBefore(Container focusCycleRoot,
      Component indexComponent, Component aComponent) {
    Vector cycle = this.getFocusCycle(focusCycleRoot, true);
    int index;
    if (cycle != null && (index = cycle.indexOf(indexComponent)) != -1) {
      index = index <= 0 ? cycle.size() - 1 : index - 1;
      this.insertComponent(focusCycleRoot, aComponent, index);
    }
  }

  public void setDefaultFocusCycleRoot(Container defaultCycleRoot) {
    this.defaultCycleRoot = defaultCycleRoot;
  }
}
