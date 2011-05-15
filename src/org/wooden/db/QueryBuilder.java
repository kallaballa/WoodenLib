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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class QueryBuilder implements Serializable {

  private String host;

  private Connection DBConnection;

  public static final String LINK_AND = " AND ";

  public static final String LINK_OR = " OR ";

  public static final String OPERATOR_IS = "=";

  public static final String OPERATOR_LIKE = "LIKE";

  public static final String OPERATOR_GREATER = "<";

  public static final String OPERATOR_LESSER = ">";

  public QueryBuilder(String host) {
    this.host = host;
    this.DBConnection = null;
  }

  public void close() throws SQLException {
    this.DBConnection.close();
  }

  public int deleteLibraryDataWhere(String operator, String table,
      String column, String value) throws SQLException {
    StringBuffer sb = (new StringBuffer("DELETE FROM ")).append(table)
        .append(" WHERE ").append(column).append(operator).append('\'')
        .append(value).append('\'');
    System.err.println(sb);
    return this.update(sb.toString());
  }

  public int insertDataBaseSet(DataBaseSet d) throws SQLException {
    StringBuffer sb = (new StringBuffer("INSERT INTO "))
        .append(DataBaseSet.getTableName(d.getClass())).append(" (")
        .append(d.createColumnString()).append(')').append("VALUES")
        .append('(').append(d.createValueString()).append(')');
    System.err.println(sb);
    return this.update(sb.toString());
  }

  public void open(String name, String user, String pass) throws SQLException {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception ex) {
      throw new SQLException(ex.getMessage());
    }
    this.DBConnection = DriverManager.getConnection(
        (new StringBuilder("jdbc:mysql://")).append(this.host).append("/")
            .append(name).toString(), user, pass);
  }

  public ResultSet query(String q) throws SQLException {
    return this.DBConnection.createStatement().executeQuery(q);
  }

  public ResultSet queryTable(String table) throws SQLException {
    StringBuffer sb = (new StringBuffer("SELECT * FROM ")).append(table);
    System.err.println(sb);
    return this.query(sb.toString());
  }

  public DataBaseSet[] queryWhere(Class c, String column, String value,
      String operator, String link) throws SQLException {
    return this.queryWhere(c, new String[] { column }, new String[] { value },
        new String[] { operator }, new String[] { link });
  }

  public DataBaseSet[] queryWhere(Class c, String columns[], String values[],
      String operators[], String links[]) throws SQLException {
    ResultSet rs = this.queryWhere(DataBaseSet.getTableName(c), columns,
        values, operators, links);
    Vector sets = new Vector();
    for (; rs.next(); sets.add(DataBaseSet.createInstance(c, rs)))
      ;
    return (DataBaseSet[]) sets.toArray(new DataBaseSet[0]);
  }

  private ResultSet queryWhere(String table, String columns[], String values[],
      String operators[], String links[]) throws SQLException {
    StringBuffer sb = (new StringBuffer("SELECT * FROM ")).append(table)
        .append(" WHERE ");
    for (int i = 0; i < columns.length; i++) {
      sb.append(columns[i]).append(' ').append(operators[i]);
      if (operators[i].equals("LIKE"))
        sb.append(" '%").append(values[i]).append("%'");
      else
        sb.append(" '").append(values[i]).append("'");
      if (i < columns.length - 1)
        sb.append(links[i]);
    }

    System.err.println(sb);
    return this.query(sb.toString());
  }

  public int update(String q) throws SQLException {
    return this.DBConnection.createStatement().executeUpdate(q);
  }

  public int updateDataBaseSet(DataBaseSet d) throws SQLException {
    StringBuffer sb = (new StringBuffer("UPDATE "))
        .append(DataBaseSet.getTableName(d.getClass())).append(" SET ")
        .append(d.createColumnValuePairedString()).append(" WHERE UID = '")
        .append(d.getValue(DataBaseSet.getColumnNames(d.getClass())[0]))
        .append("'");
    System.err.println(sb);
    return this.update(sb.toString());
  }
}
