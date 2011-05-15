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

package org.wooden.io;

import java.util.HashMap;

public class DataSet extends HashMap implements Comparable {

  private CharacterSeparatedTable table;

  private boolean isModified;

  public DataSet(CharacterSeparatedTable table) {
    this.isModified = false;
    this.table = table;
  }

  public DataSet(CharacterSeparatedTable table, String values[]) {
    this(table);
    this.putData(this.columns(), values);
  }

  public String[] columns() {
    return this.table.columns();
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof DataSet) {
      int d = this.table.getSortingDirective();
      int c = this.table.getSortingColumn();
      if (c < 0 || d == 0) {
        return 0;
      } else {
        String sortingColumn = this.columns()[c];
        int r = this.getValue(sortingColumn).compareToIgnoreCase(
            ((DataSet) o).getValue(sortingColumn));
        return r * d;
      }
    } else {
      return 1;
    }
  }

  public boolean containsColumn(String column) {
    return this.containsKey(column);
  }

  public String getValue(String column) {
    return (String) this.get(column);
  }

  public String[] getValues() {
    return (String[]) this.values().toArray(new String[0]);
  }

  public boolean isModified() {
    return this.isModified;
  }

  private void putData(String columns[], String values[]) {
    for (int i = 0; i < columns.length; i++)
      this.put(columns[i], values[i]);

  }

  public void setValue(String column, String value) {
    this.put(column, value);
    this.isModified = true;
  }
}
