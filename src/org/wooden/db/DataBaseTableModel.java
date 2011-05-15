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
package org.wooden.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class DataBaseTableModel extends AbstractTableModel {

  private QueryBuilder qb;

  private Class dbColumnTypes[];

  private String dbColumns[];

  private String tableColumns[];

  private DataBaseSet arrSets[];

  public DataBaseTableModel(QueryBuilder qb, Class dbsClass,
      String tableColumns[]) throws NoSuchMethodException,
      InvocationTargetException, IllegalAccessException {
    this.qb = null;
    this.dbColumnTypes = null;
    this.dbColumns = null;
    this.tableColumns = null;
    this.arrSets = null;
    if (!DataBaseSet.isChild(dbsClass)) {
      throw new IllegalArgumentException(
          "Provided class is not a sub class of DataBaseSet");
    } else {
      this.qb = qb;
      this.dbColumns = this.retrieveColumnNames(dbsClass);
      this.dbColumnTypes = this.retrieveColumnTypes(dbsClass);
      this.tableColumns = tableColumns;
      return;
    }
  }

  public void createNewDataSet() {
    this.fireTableDataChanged();
  }

  @Override
  public Class getColumnClass(int i) {
    return this.dbColumnTypes[i];
  }

  @Override
  public int getColumnCount() {
    return this.getTableColumnNames().length;
  }

  @Override
  public String getColumnName(int i) {
    return this.getTableColumnNames()[i];
  }

  public String[] getDataBaseColumnNames() {
    return this.dbColumns;
  }

  @Override
  public int getRowCount() {
    return this.arrSets == null ? 0 : this.arrSets.length;
  }

  public String getTableColumnName(int i) {
    return this.getTableColumnNames()[i];
  }

  public String[] getTableColumnNames() {
    return this.tableColumns;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (this.arrSets != null) {
      DataBaseSet set = this.arrSets[rowIndex];
      return set.getValue(set.getColumnName(columnIndex));
    } else {
      return null;
    }
  }

  @Override
  public boolean isCellEditable(int r, int c) {
    return true;
  }

  public void removeDataSet(int r) throws SQLException {
    this.fireTableDataChanged();
  }

  private String[] retrieveColumnNames(Class dataBaseSetClass)
      throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException {
    return (String[]) dataBaseSetClass
        .getMethod("getColumnNames", new Class[0]).invoke(dataBaseSetClass,
            new Object[0]);
  }

  private Class[] retrieveColumnTypes(Class dataBaseSetClass)
      throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException {
    int dbTypes[] = (int[]) dataBaseSetClass.getMethod("getColumnTypes",
        new Class[0]).invoke(dataBaseSetClass, new Object[0]);
    Class javaTypes[] = new Class[dbTypes.length];
    for (int i = 0; i < dbTypes.length; i++)
      switch (dbTypes[i]) {
      case 0: // '\0'
        javaTypes[i] = java.lang.String.class;
        break;

      case 1: // '\001'
        javaTypes[i] = java.sql.Date.class;
        break;

      case 2: // '\002'
        javaTypes[i] = java.lang.Integer.class;
        break;

      case 3: // '\003'
        javaTypes[i] = java.lang.Boolean.class;
        break;

      default:
        throw new IllegalArgumentException((new StringBuilder(
            "Uknown type retrieved from DataBaseSet impl: "))
            .append(dbTypes[i]).toString());
      }

    return javaTypes;
  }

  public void search(Class c, int column, String value) throws SQLException {
    this.arrSets = this.qb.queryWhere(c, DataBaseSet.getColumnNames(c)[column],
        value, "LIKE", " AND ");
    this.fireTableDataChanged();
  }

  @Override
  public void setValueAt(Object obj, int rowIndex, int columnIndex) {
    if (this.arrSets != null) {
      DataBaseSet set = this.arrSets[rowIndex];
      set.setValue(set.getColumnName(columnIndex), obj);
      try {
        this.qb.updateDataBaseSet(set);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
