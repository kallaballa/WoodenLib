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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public abstract class DataBaseSet extends HashMap {

  public static final int TYPE_STRING = 0;

  public static final int TYPE_DATE = 1;

  public static final int TYPE_INT = 2;

  public static final int TYPE_BOOLEAN = 3;

  public static DataBaseSet createInstance(Class c, ResultSet rs) {
    throw new UnsupportedOperationException("Decompiler garbage");
    // if(!isChild(c))
    // break MISSING_BLOCK_LABEL_42;
    // return (DataBaseSet)c.getConstructor(new Class[] {
    // java.sql.ResultSet.class
    // }).newInstance(new Object[] {
    // rs
    // });
    // Exception ex;
    // ex;
    // ex.printStackTrace();
    // return null;
    // throw new
    // IllegalArgumentException("Provided class is not a sub class of DataBaseSet");
  }

  public static String[] getColumnNames() {
    throw new UnsupportedOperationException(
        "getColumnNames() must be overwritten by sub class");
  }

  public static String[] getColumnNames(Class c) {
    throw new UnsupportedOperationException("Decompiler garbage");
    // if(!isChild(c))
    // break MISSING_BLOCK_LABEL_36;
    // return (String[])c.getMethod("getColumnNames", new Class[0]).invoke(c,
    // new Object[0]);
    // Exception ex;
    // ex;
    // ex.printStackTrace();
    // return null;
    // throw new
    // IllegalArgumentException("Provided class is not a sub class of DataBaseSet");
  }

  public static int[] getColumnTypes() {
    throw new UnsupportedOperationException(
        "getColumnTypes() must be overwritten by sub class");
  }

  public static int[] getColumnTypes(Class c) {
    throw new UnsupportedOperationException("Decompiler garbage");
    // if(!isChild(c))
    // break MISSING_BLOCK_LABEL_36;
    // return (int[])c.getMethod("getColumnTypes", new Class[0]).invoke(c, new
    // Object[0]);
    // Exception ex;
    // ex;
    // ex.printStackTrace();
    // return null;
    // throw new
    // IllegalArgumentException("Provided class is not a sub class of DataBaseSet");
  }

  public static String getTableName() {
    throw new UnsupportedOperationException(
        "getColumnNames() must be overwritten by sub class");
  }

  public static String getTableName(Class c) {
    throw new UnsupportedOperationException("Decompiler garbage");
    // if(!isChild(c))
    // break MISSING_BLOCK_LABEL_36;
    // return (String)c.getMethod("getTableName", new Class[0]).invoke(c, new
    // Object[0]);
    // Exception ex;
    // ex;
    // ex.printStackTrace();
    // return null;
    // throw new
    // IllegalArgumentException("Provided class is not a sub class of DataBaseSet");
  }

  public static boolean isChild(Class child) {
    for (Class tmp = child; (tmp = tmp.getSuperclass()) != null;)
      if (tmp.equals(org.wooden.db.DataBaseSet.class))
        return true;

    return false;
  }

  public DataBaseSet() {}

  public String createColumnString() {
    char comma = ',';
    StringBuffer sb = new StringBuffer();
    String allColumns[] = this.getNonNullColumns();
    for (int i = 0; i < allColumns.length; i++) {
      sb.append(allColumns[i]);
      if (i < allColumns.length - 1)
        sb.append(comma);
    }

    return sb.toString();
  }

  public String createColumnValuePairedString() {
    char comma = ',';
    StringBuffer sb = new StringBuffer();
    String allColumns[] = this.getNonNullColumns();
    for (int i = 0; i < allColumns.length; i++) {
      Object val = this.getValue(allColumns[i]);
      if (val instanceof Boolean) {
        boolean b = ((Boolean) val).booleanValue();
        if (b)
          val = "1";
        else
          val = "0";
      }
      sb.append(allColumns[i]).append(" = ").append('\'').append(val)
          .append('\'');
      if (i < allColumns.length - 1)
        sb.append(comma);
    }

    return sb.toString();
  }

  public String createValueString() {
    char comma = ',';
    StringBuffer sb = new StringBuffer();
    String allColumns[] = this.getNonNullColumns();
    for (int i = 0; i < allColumns.length; i++) {
      sb.append('\'').append(this.getValue(allColumns[i])).append('\'');
      if (i < allColumns.length - 1)
        sb.append(comma);
    }

    return sb.toString();
  }

  public String getColumnName(int i) {
    return getColumnNames(this.getClass())[i];
  }

  public int getColumnType(int i) {
    return getColumnTypes(this.getClass())[i];
  }

  public String[] getNonNullColumns() {
    String allColumns[] = getColumnNames(this.getClass());
    Vector foundColumns = new Vector();
    for (String allColumn : allColumns)
      if (this.getValue(allColumn) != null)
        foundColumns.add(allColumn);

    return (String[]) foundColumns.toArray(new String[0]);
  }

  public Object getValue(String column) {
    return super.get(column);
  }

  protected void init(Class c, ResultSet rs) throws SQLException {
    if (getColumnNames(c).length != getColumnTypes(c).length) {
      throw new RuntimeException(
          "Number of columnnames and columntypes don't match");
    } else {
      this.readResultSet(c, rs);
      return;
    }
  }

  private void readResultSet(Class c, ResultSet rs) throws SQLException {
    String columns[] = getColumnNames(c);
    int types[] = getColumnTypes(c);
    for (int i = 0; i < columns.length; i++)
      switch (types[i]) {
      case 0: // '\0'
        this.setValue(columns[i], rs.getString(columns[i]));
        break;

      case 1: // '\001'
        this.setValue(columns[i], rs.getDate(columns[i]));
        break;

      case 2: // '\002'
        this.setValue(columns[i], new Integer(rs.getInt(columns[i])));
        break;

      case 3: // '\003'
        this.setValue(columns[i], new Boolean(rs.getBoolean(columns[i])));
        break;

      default:
        throw new IllegalArgumentException((new StringBuilder(
            "Uknown type retrieved from DataBaseSet impl: ")).append(types[i])
            .toString());
      }

  }

  public void setValue(String column, Object value) {
    super.put(column, value);
  }

  @Override
  public String toString() {
    return this.createColumnValuePairedString();
  }
}
