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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class StatusDialog extends LayoutDialog implements Runnable {
  private class Show extends Thread {

    private JDialog d;

    public Show(JDialog d) {
      super();
      this.d = d;
    }

    @Override
    public void run() {
      this.d.setModal(true);
      this.d.setVisible(true);
    }
  }

  public static void main(String args[]) {
    StatusDialog sd = new StatusDialog("Halli");
    sd.setLocked(true);
  }

  private JButton btnAbort;

  private JTextField txtStatus;

  private static final String THREE_DOTS = "...";

  private static final String THREE_BLANKS = "   ";

  private String statusMsg;

  public StatusDialog(Dialog owner, String status) throws HeadlessException {
    super(owner, status);
    this.btnAbort = new JButton("abort");
    this.txtStatus = new JTextField();
    this.init(status);
  }

  public StatusDialog(Dialog owner, String status, boolean modal)
      throws HeadlessException {
    super(owner, status, modal);
    this.btnAbort = new JButton("abort");
    this.txtStatus = new JTextField();
    this.init(status);
  }

  public StatusDialog(Frame owner, String status) throws HeadlessException {
    super(owner);
    this.btnAbort = new JButton("abort");
    this.txtStatus = new JTextField();
    this.init(status);
  }

  public StatusDialog(Frame owner, String status, boolean modal)
      throws HeadlessException {
    super(owner, status, modal);
    this.btnAbort = new JButton("abort");
    this.txtStatus = new JTextField();
    this.init(status);
  }

  public StatusDialog(String status) throws HeadlessException {
    this.btnAbort = new JButton("abort");
    this.txtStatus = new JTextField();
    this.init(status);
  }

  public void addActionListener(ActionListener l) {
    this.btnAbort.addActionListener(l);
  }

  private void init(String statusMsg) {
    if (statusMsg == null) {
      throw new NullPointerException("status message can't be null");
    } else {
      this.setResizable(false);
      (new Thread(this)).start();
      this.statusMsg = statusMsg;
      this.txtStatus.setEditable(false);
      this.txtStatus.setFont(new Font("Arial", 1, 11));
      this.txtStatus.setHorizontalAlignment(0);
      this.txtStatus.setColumns(statusMsg.length() + 6);
      System.out.println(this.txtStatus.getPreferredSize().width
          + this.btnAbort.getPreferredSize().width);
      Dimension dim = new Dimension(this.txtStatus.getPreferredSize().width
          + this.btnAbort.getPreferredSize().width, 60);
      this.setSize(dim);
      this.setPreferredSize(dim);
      this.setMinimumSize(dim);
      java.awt.GridBagConstraints shrink = LayoutPane.SHRINKER;
      java.awt.GridBagConstraints fill = LayoutPane.FILLER;
      this.getLayoutPane().addLayoutLine()
          .addLayoutComponent("txtStatus", this.txtStatus, 0, fill)
          .addLayoutComponent("btnAbort", this.btnAbort, 0, shrink);
      return;
    }
  }

  @Override
  public void run() {
    int i = 0;
    do {
      synchronized (this) {
        this.txtStatus.setText((new StringBuilder("   "))
            .append(this.statusMsg).append("...".substring(0, i))
            .append("   ".substring(i)).toString());
      }
      try {
        Thread.sleep(500L);
      } catch (Exception exception) {}
      if (i == 3)
        i = -1;
      i++;
    } while (true);
  }

  public synchronized void setLocked(boolean v) {
    if (v) {
      ComponentTool.centerOnScreen(this);
      (new Show(this)).start();
      try {
        Thread.sleep(100L);
      } catch (InterruptedException interruptedexception) {}
    } else {
      this.setVisible(false);
      this.setModal(false);
    }
  }

  public void setStatusMessage(String msg) {
    synchronized (this) {
      this.statusMsg = msg;
    }
  }

  public void setStatusMessage(String msg, int delay) {
    this.setStatusMessage(msg);
    try {
      Thread.sleep(delay);
    } catch (Exception exception) {}
  }
}
