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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.wooden.util.SortedVector;

public class CharacterSeparatedTable extends SortedVector {

  public static final int NO_SORTING = 0;

  public static final int ASCENDING_SORTING = -1;

  public static final int DESCENDING_SORTING = 1;

  private String columns[];

  private char delim;

  private int sortingColumn;

  private int sortingDirective;

  public CharacterSeparatedTable(InputStream in, char delim) throws IOException {
    this.sortingColumn = -1;
    this.sortingDirective = 0;
    this.delim = delim;
    this.readTable(in, delim);
  }

  public void addDataSet(DataSet set) {
    this.add(set);
  }

  public String[] columns() {
    return this.columns;
  }

  public int findColumn(String name) {
    String arrColumns[] = this.columns();
    for (int i = 0; i < arrColumns.length; i++)
      if (arrColumns[i].equals(name))
        return i;

    return -1;
  }

  public DataSet getDataSet(int i) {
    return (DataSet) this.get(i);
  }

  public DataSet[] getDataSets() {
    return (DataSet[]) this.toArray(new DataSet[0]);
  }

  public DataSet[] getDataSets(int sortingColumn) {
    synchronized (this.getSortLock()) {
      return this.getDataSets(sortingColumn, -1);
    }
  }

  public DataSet[] getDataSets(int sortingColumn, int sortingDirective) {
    synchronized (this.getSortLock()) {
      DataSet sets[];
      int oldColumn = this.getSortingColumn();
      int oldDirective = this.getSortingDirective();
      this.setSortingDirective(sortingDirective);
      this.setSortingColumn(sortingColumn);
      this.sort();
      sets = this.getDataSets();
      this.setSortingDirective(oldDirective);
      this.setSortingColumn(oldColumn);
      this.sort();
      return sets;
    }
  }

  public DataSet[] getDataSetWhereIs(int column, String value) {
    return this.getDataSetWhereIs(this.columns[column], value);
  }

  public DataSet[] getDataSetWhereIs(String column, String value) {
    DataSet sets[] = this.getDataSets();
    Vector found = new Vector();
    for (DataSet set : sets)
      if (set.getValue(column).equals(value))
        found.add(set);

    return (DataSet[]) found.toArray(new DataSet[0]);
  }

  public int getSortingColumn() {
    synchronized (this.getSortLock()) {
      return this.sortingColumn;
    }
  }

  public int getSortingDirective() {
    synchronized (this.getSortLock()) {
      return this.sortingDirective;
    }
  }

  private void readTable(InputStream in, char delim) throws IOException {
    TableReader reader = new TableReader(in, String.valueOf(delim));
    this.columns = reader.readArray();
    String values[];
    while ((values = reader.readArray()) != null)
      this.addDataSet(new DataSet(this, values));
    reader.close();
  }

  public void removeDataSet(DataSet set) {
    this.remove(set);
  }

  public void setSortingColumn(int i) {
    synchronized (this.getSortLock()) {
      this.sortingColumn = i;
      this.sort();
    }
  }

  public void setSortingDirective(int sd) {
    synchronized (this.getSortLock()) {
      if (sd >= -1 && sd <= 1) {
        this.enableSorting(sd != 0);
        this.sortingDirective = sd;
      } else {
        throw new IllegalArgumentException((new StringBuilder(
            "Unknown sorting directive: ")).append(sd).toString());
      }
      this.sort();
    }
  }

  public void writeTable(File f) throws IOException {
    TableWriter writer = new TableWriter(new FileWriter(f),
        String.valueOf(this.delim));
    DataSet sets[] = this.getDataSets();
    writer.writeArray(this.columns);
    for (DataSet set : sets)
      writer.writeArray(set.getValues());

    writer.close();
  }
}
