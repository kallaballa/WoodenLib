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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.wooden.event.ComponentDragAdapter;

public class ConfirmationDialog extends JDialog implements ActionListener {

  public static void main(String args1[]) {}

  private JPanel rootContainer;

  private JPanel textContainer;

  private JPanel buttonContainer;

  private JButton buttons[];

  private String actionnames[];

  private Action actions[];

  private JLabel lbl;

  public ConfirmationDialog(String message, String actionnames[],
      Action actions[]) throws HeadlessException {
    this(message, actionnames, actions, true);
  }

  public ConfirmationDialog(String message, String actionnames[],
      Action actions[], boolean decorated) throws HeadlessException {
    this.rootContainer = new JPanel(new FlowLayout());
    this.textContainer = new JPanel(new FlowLayout(1));
    this.lbl = new JLabel();
    this.setUndecorated(decorated);
    this.actions = actions;
    this.actionnames = actionnames;
    this.setMessage(message);
    this.initComponents(actionnames);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    for (int i = 0; i < this.buttons.length; i++)
      if (src == this.buttons[i] && this.actions[i] != null)
        this.actions[i].actionPerformed(e);

    this.setModal(false);
    this.setVisible(false);
    this.dispose();
  }

  public String getMessage() {
    return this.lbl.getText();
  }

  public JPanel getRootPanel() {
    return this.rootContainer;
  }

  private void initComponents(String actionnames[]) {
    this.addWindowFocusListener(new WindowFocusListener() {

      @Override
      public void windowGainedFocus(WindowEvent windowevent) {}

      @Override
      public void windowLostFocus(WindowEvent e) {
        e.getWindow().toFront();
        e.getWindow().requestFocus();
      }
    });
    this.rootContainer.addMouseMotionListener(new ComponentDragAdapter(this));
    this.buttonContainer = new JPanel(new GridLayout(1, actionnames.length, 4,
        4));
    this.buttonContainer.setVisible(true);
    this.buttons = new JButton[actionnames.length];
    for (int i = 0; i < actionnames.length; i++) {
      this.buttons[i] = new JButton(actionnames[i]);
      this.buttons[i].addActionListener(this);
      this.buttons[i].setActionCommand(actionnames[i]);
      this.buttons[i].setVisible(true);
      this.buttonContainer.add(this.buttons[i]);
    }

    this.textContainer.add(this.lbl);
    this.textContainer.setBorder(null);
    this.textContainer.setVisible(true);
    this.rootContainer.add(this.textContainer);
    this.rootContainer.add(this.buttonContainer);
    this.setContentPane(this.rootContainer);
    this.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        ConfirmationDialog.this.dispose();
      }
    });
    this.doLayout();
    this.pack();
  }

  public void query() {
    ComponentTool.centerOnScreen(this);
    this.setVisible(false);
    this.setModal(true);
    this.setVisible(true);
  }

  @Override
  public void setBackground(Color c) {
    super.setBackground(c);
    this.getContentPane().setBackground(c);
    this.textContainer.setBackground(c);
    this.lbl.setBackground(c);
    this.buttonContainer.setBackground(c);
    for (JButton button : this.buttons)
      button.setBackground(c);

  }

  @Override
  public void setForeground(Color c) {
    super.setForeground(c);
    this.getContentPane().setForeground(c);
    this.textContainer.setForeground(c);
    this.lbl.setForeground(c);
    this.buttonContainer.setForeground(c);
    for (JButton button : this.buttons)
      button.setForeground(c);

  }

  public void setMessage(String text) {
    this.lbl.setText(text);
  }
}
