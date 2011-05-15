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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class DBContainer extends Box implements ActionListener, KeyListener {

  private static final String COMMAND_ADD = "Erstellen";

  private static final String COMMAND_REMOVE = "Entfernen";

  private JPanel searchContainer;

  private JPanel buttonContainer;

  private JScrollPane tableContainer;

  private JComboBox cmbSearchFields;

  private JTextField txtSearch;

  private JButton btnAdd;
  private JButton btnRemove;
  private JTable table;
  private DataBaseTableModel model;

  public DBContainer(DataBaseTableModel model) {
    super(1);
    this.searchContainer = new JPanel();
    this.buttonContainer = new JPanel();
    this.tableContainer = new JScrollPane(20, 30);
    this.cmbSearchFields = new JComboBox();
    this.txtSearch = new JTextField();
    this.btnAdd = new JButton("Erstellen");
    this.btnRemove = new JButton("Entfernen");
    this.table = new JTable();
    this.model = model;
    this.init();
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    (new Thread() {

      @Override
      public void run() {
        if (e.getActionCommand().equals("Erstellen"))
          DBContainer.this.model.createNewDataSet();
        else if (e.getActionCommand().equals("Entfernen"))
          try {
            int selRows[] = DBContainer.this.table.getSelectedRows();
            for (int selRow : selRows)
              DBContainer.this.model.removeDataSet(selRow);

          } catch (Exception ex) {
            ex.printStackTrace();
          }
      }
      // DECOMPILER GARBAGE??
      // final DBContainer this$0;
      // private final ActionEvent val$e;
      //
      //
      // {
      // this$0 = DBContainer.this;
      // e = actionevent;
      // super();
      // }
    }).start();
  }

  private void init() {
    this.table.setModel(this.model);
    int cc = this.model.getColumnCount();
    for (int i = 0; i < cc; i++)
      this.cmbSearchFields.addItem(this.model.getColumnName(i));

    this.table.setRowHeight(20);
    this.txtSearch.setColumns(15);
    this.txtSearch.addKeyListener(this);
    this.btnAdd.addActionListener(this);
    this.btnRemove.addActionListener(this);
    this.searchContainer.add(this.txtSearch);
    this.searchContainer.add(this.cmbSearchFields);
    this.tableContainer.setViewportView(this.table);
    this.buttonContainer.add(this.btnAdd);
    this.buttonContainer.add(this.btnRemove);
    this.add(this.searchContainer);
    this.add(this.tableContainer);
    this.add(this.buttonContainer);
    this.doLayout();
  }

  @Override
  public void keyPressed(KeyEvent keyevent) {}

  @Override
  public void keyReleased(KeyEvent e) {
    this.updateSearch();
  }

  @Override
  public void keyTyped(KeyEvent keyevent) {}

  public void updateSearch() {}

}
