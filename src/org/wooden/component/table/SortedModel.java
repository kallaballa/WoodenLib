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
package org.wooden.component.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SortedModel extends AbstractTableModel {
  private static class Arrow implements Icon {

    private boolean descending;

    private int size;

    private int priority;

    public Arrow(boolean descending, int size, int priority) {
      this.descending = descending;
      this.size = size;
      this.priority = priority;
    }

    @Override
    public int getIconHeight() {
      return this.size;
    }

    @Override
    public int getIconWidth() {
      return this.size;
    }

    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x,
        int y) {
      java.awt.Color color = c != null ? c.getBackground()
          : java.awt.Color.GRAY;
      int dx = (int) ((this.size / 2) * Math.pow(0.80000000000000004D,
          this.priority));
      int dy = this.descending ? dx : -dx;
      y = y + (5 * this.size) / 6 + (this.descending ? -dy : 0);
      int shift = this.descending ? 1 : -1;
      g.translate(x, y);
      g.setColor(color.darker());
      g.drawLine(dx / 2, dy, 0, 0);
      g.drawLine(dx / 2, dy + shift, 0, shift);
      g.setColor(color.brighter());
      g.drawLine(dx / 2, dy, dx, 0);
      g.drawLine(dx / 2, dy + shift, dx, shift);
      if (this.descending)
        g.setColor(color.darker().darker());
      else
        g.setColor(color.brighter().brighter());
      g.drawLine(dx, 0, 0, 0);
      g.setColor(color);
      g.translate(-x, -y);
    }
  }

  private static class Directive {

    private int column;
    private int direction;

    public Directive(int column, int direction) {
      this.column = column;
      this.direction = direction;
    }
  }

  private class MouseHandler extends java.awt.event.MouseAdapter {

    private MouseHandler() {
      super();
    }

    MouseHandler(MouseHandler mousehandler) {
      this();
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
      JTableHeader h = (JTableHeader) e.getSource();
      TableColumnModel columnModel = h.getColumnModel();
      int viewColumn = columnModel.getColumnIndexAtX(e.getX());
      int column = columnModel.getColumn(viewColumn).getModelIndex();
      if (column != -1) {
        int status = SortedModel.this.getSortingStatus(column);
        if (!e.isControlDown())
          SortedModel.this.cancelSorting();
        status += e.isShiftDown() ? -1 : 1;
        status = (status + 4) % 3 - 1;
        SortedModel.this.setSortingStatus(column, status);
      }
    }
  }

  private class Row implements Comparable {

    private int modelIndex;

    public Row(int index) {
      super();
      this.modelIndex = index;
    }

    @Override
    public int compareTo(Object o) {
      int row1 = this.modelIndex;
      int row2 = ((Row) o).modelIndex;
      for (Iterator it = SortedModel.this.sortingColumns.iterator(); it
          .hasNext();) {
        Directive directive = (Directive) it.next();
        int column = directive.column;
        Object o1 = SortedModel.this.tableModel.getValueAt(row1, column);
        Object o2 = SortedModel.this.tableModel.getValueAt(row2, column);
        int comparison = 0;
        if (o1 == null && o2 == null)
          comparison = 0;
        else if (o1 == null)
          comparison = -1;
        else if (o2 == null)
          comparison = 1;
        else if (o1.equals("..")) {
          if (directive.direction == -1)
            comparison = 1;
          else
            comparison = -1;
        } else if (o2.equals("..")) {
          if (directive.direction == -1)
            comparison = -1;
          else
            comparison = 1;
        } else {
          comparison = SortedModel.this.getComparator(column).compare(o1, o2);
        }
        if (comparison != 0)
          return directive.direction != -1 ? comparison : -comparison;
      }

      return 0;
    }
  }

  private class SortableHeaderRenderer implements TableCellRenderer {

    private TableCellRenderer tableCellRenderer;

    public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
      super();
      this.tableCellRenderer = tableCellRenderer;
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      java.awt.Component c = this.tableCellRenderer
          .getTableCellRendererComponent(table, value, isSelected, hasFocus,
              row, column);
      if (c instanceof JLabel) {
        JLabel l = (JLabel) c;
        l.setHorizontalTextPosition(2);
        int modelColumn = table.convertColumnIndexToModel(column);
        l.setIcon(SortedModel.this.getHeaderRendererIcon(modelColumn, l
            .getFont().getSize()));
      }
      return c;
    }
  }

  private class TableModelHandler implements TableModelListener {

    private TableModelHandler() {
      super();
    }

    TableModelHandler(TableModelHandler tablemodelhandler) {
      this();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
      if (!SortedModel.this.isSorting()) {
        SortedModel.this.clearSortingState();
        SortedModel.this.fireTableChanged(e);
        return;
      }
      if (e.getFirstRow() == -1) {
        SortedModel.this.cancelSorting();
        SortedModel.this.fireTableChanged(e);
        return;
      }
      int column = e.getColumn();
      if (e.getFirstRow() == e.getLastRow() && column != -1
          && SortedModel.this.getSortingStatus(column) == 0
          && SortedModel.this.modelToView != null) {
        int viewIndex = SortedModel.this.getModelToView()[e.getFirstRow()];
        SortedModel.this.fireTableChanged(new TableModelEvent(SortedModel.this,
            viewIndex, viewIndex, column, e.getType()));
        return;
      } else {
        SortedModel.this.clearSortingState();
        SortedModel.this.fireTableDataChanged();
        return;
      }
    }
  }

  protected TableModel tableModel;

  public static final int DESCENDING = -1;

  public static final int NOT_SORTED = 0;

  public static final int ASCENDING = 1;

  private static Directive EMPTY_DIRECTIVE = new Directive(-1, 0);

  public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {

    @Override
    public int compare(Object o1, Object o2) {
      return ((Comparable) o1).compareTo(o2);
    }

  };

  public static final Comparator LEXICAL_COMPARATOR = new Comparator() {

    @Override
    public int compare(Object o1, Object o2) {
      return o1.toString().compareTo(o2.toString());
    }

  };

  private Row viewToModel[];

  private int modelToView[];

  private JTableHeader tableHeader;

  private java.awt.event.MouseListener mouseListener;

  private TableModelListener tableModelListener;

  private Map columnComparators;

  private List sortingColumns;

  public SortedModel() {
    this.columnComparators = new HashMap();
    this.sortingColumns = new ArrayList();
    this.mouseListener = new MouseHandler(null);
    this.tableModelListener = new TableModelHandler(null);
  }

  public SortedModel(int sortingColumn, int status) {
    this();
    this.setSortingStatus(sortingColumn, status);
  }

  public SortedModel(TableModel tableModel) {
    this();
    this.setTableModel(tableModel);
  }

  public SortedModel(TableModel tableModel, JTableHeader tableHeader) {
    this();
    this.setTableHeader(tableHeader);
    this.setTableModel(tableModel);
  }

  private void cancelSorting() {
    this.sortingColumns.clear();
    this.sortingStatusChanged();
  }

  private void clearSortingState() {
    this.viewToModel = null;
    this.modelToView = null;
  }

  @Override
  public Class getColumnClass(int column) {
    return this.tableModel.getColumnClass(column);
  }

  @Override
  public int getColumnCount() {
    return this.tableModel != null ? this.tableModel.getColumnCount() : 0;
  }

  @Override
  public String getColumnName(int column) {
    return this.tableModel.getColumnName(column);
  }

  protected Comparator getComparator(int column) {
    Class columnType = this.tableModel.getColumnClass(column);
    Comparator comparator = (Comparator) this.columnComparators.get(columnType);
    if (comparator != null)
      return comparator;
    if (java.lang.Comparable.class.isAssignableFrom(columnType))
      return COMPARABLE_COMAPRATOR;
    else
      return LEXICAL_COMPARATOR;
  }

  private Directive getDirective(int column) {
    for (int i = 0; i < this.sortingColumns.size(); i++) {
      Directive directive = (Directive) this.sortingColumns.get(i);
      if (directive.column == column)
        return directive;
    }

    return EMPTY_DIRECTIVE;
  }

  protected Icon getHeaderRendererIcon(int column, int size) {
    Directive directive = this.getDirective(column);
    if (directive == EMPTY_DIRECTIVE)
      return null;
    else
      return new Arrow(directive.direction == -1, size,
          this.sortingColumns.indexOf(directive));
  }

  private int[] getModelToView() {
    if (this.modelToView == null) {
      int n = this.getViewToModel().length;
      this.modelToView = new int[n];
      for (int i = 0; i < n; i++)
        this.modelToView[this.modelIndex(i)] = i;

    }
    return this.modelToView;
  }

  @Override
  public int getRowCount() {
    return this.tableModel != null ? this.tableModel.getRowCount() : 0;
  }

  public int getSortingStatus(int column) {
    return this.getDirective(column).direction;
  }

  public JTableHeader getTableHeader() {
    return this.tableHeader;
  }

  public TableModel getTableModel() {
    return this.tableModel;
  }

  @Override
  public Object getValueAt(int row, int column) {
    return this.tableModel.getValueAt(this.modelIndex(row), column);
  }

  private Row[] getViewToModel() {
    if (this.viewToModel == null) {
      int tableModelRowCount = this.tableModel.getRowCount();
      this.viewToModel = new Row[tableModelRowCount];
      for (int row = 0; row < tableModelRowCount; row++)
        this.viewToModel[row] = new Row(row);

      if (this.isSorting())
        Arrays.sort(this.viewToModel);
    }
    return this.viewToModel;
  }

  public void install(JTable table) {
    this.setTableHeader(table.getTableHeader());
    this.setTableModel(table.getModel());
    table.setModel(this);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return this.tableModel.isCellEditable(this.modelIndex(row), column);
  }

  public boolean isSorting() {
    return this.sortingColumns.size() != 0;
  }

  public int modelIndex(int viewIndex) {
    try {
      return this.getViewToModel()[viewIndex].modelIndex;
    } catch (Exception ex) {
      return 0;
    }
  }

  public void setColumnComparator(Class type, Comparator comparator) {
    if (comparator == null)
      this.columnComparators.remove(type);
    else
      this.columnComparators.put(type, comparator);
  }

  public void setSortingStatus(int column, int status) {
    Directive directive = this.getDirective(column);
    if (directive != EMPTY_DIRECTIVE)
      this.sortingColumns.remove(directive);
    if (status != 0)
      this.sortingColumns.add(new Directive(column, status));
    this.sortingStatusChanged();
  }

  public void setTableHeader(JTableHeader tableHeader) {
    if (this.tableHeader != null) {
      this.tableHeader.removeMouseListener(this.mouseListener);
      TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
      if (defaultRenderer instanceof SortableHeaderRenderer)
        this.tableHeader
            .setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
    }
    this.tableHeader = tableHeader;
    if (this.tableHeader != null) {
      this.tableHeader.addMouseListener(this.mouseListener);
      this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
          this.tableHeader.getDefaultRenderer()));
    }
  }

  public void setTableModel(TableModel tableModel) {
    if (this.tableModel != null)
      this.tableModel.removeTableModelListener(this.tableModelListener);
    this.tableModel = tableModel;
    if (this.tableModel != null)
      this.tableModel.addTableModelListener(this.tableModelListener);
    this.clearSortingState();
    this.fireTableStructureChanged();
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    this.tableModel.setValueAt(aValue, this.modelIndex(row), column);
  }

  private void sortingStatusChanged() {
    this.clearSortingState();
    this.fireTableDataChanged();
    if (this.tableHeader != null)
      this.tableHeader.repaint();
  }

}
