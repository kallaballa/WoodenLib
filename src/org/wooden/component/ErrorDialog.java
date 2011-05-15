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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.wooden.io.Sema;
import org.wooden.io.StandardFileFilter;

public class ErrorDialog extends SplitLayoutFrame implements ActionListener {

  public static void main(String args[]) {
    try {
      throwException(45);
    } catch (Exception ex) {
      (new ErrorDialog("test", ex)).open(false);
    }
  }

  private static void throwException(int cnt) throws Exception {
    if (cnt++ == 50) {
      throw new Exception("blabla");
    } else {
      throwException(cnt);
      return;
    }
  }

  private JLabel lblError;

  private JTextField txtReportPath;

  private JTextArea txtDetail;

  private JButton btnSave;

  private JButton btnSaveAs;

  private JButton btnExit;

  private JLabel lblStatus;

  private String report;

  private String message;

  private Sema err_lock;

  public ErrorDialog(String title, Throwable error) throws HeadlessException {
    super(title);
    this.lblError = new JLabel();
    this.txtReportPath = new JTextField();
    this.txtDetail = new JTextArea();
    this.btnSave = new JButton();
    this.btnSaveAs = new JButton();
    this.btnExit = new JButton();
    this.lblStatus = new JLabel();
    this.err_lock = new Sema(0);
    this.report = this.createReport(error);
    this.message = error.getMessage();
    try {
      this.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src instanceof JButton)
      if (src == this.btnSave)
        this.doSave();
      else if (src == this.btnSaveAs)
        this.doSaveAs();
      else if (src == this.btnExit)
        this.doClose();
  }

  private String createReport(Throwable t) {
    StringWriter error;
    t.printStackTrace(new PrintWriter(error = new StringWriter()));
    return (new StringBuffer(error.toString())).toString();
  }

  private void doClose() {
    this.setVisible(false);
    this.dispose();
  }

  public void doExit(ActionEvent e) {
    this.err_lock.V();
    this.doClose();
  }

  private void doSave() {
    File outputFile = new File(this.txtReportPath.getText());
    if (outputFile != null)
      try {
        this.saveReport(outputFile);
        this.setStatusText("Report successfully saved");
      } catch (IOException ex) {
        this.setStatusText((new StringBuilder("Can't save report in: "))
            .append(outputFile.getAbsolutePath()).toString());
      }
    else
      this.setStatusText("Invalid output path");
  }

  private void doSaveAs() {
    ConfigurationChooser cc = new ConfigurationChooser(this,
        this.txtReportPath, 1, new StandardFileFilter("*.txt (Textdatei)",
            ".txt"));
    File outputFile = cc.open();
    if (outputFile != null)
      try {
        this.saveReport(outputFile);
        this.setStatusText("Report successfully saved");
      } catch (IOException ex) {
        this.setStatusText((new StringBuilder("Can't save report in: "))
            .append(outputFile.getAbsolutePath()).toString());
      }
    else
      this.setStatusText("Invalid output path");
  }

  private void init() throws Exception {
    this.setSize(500, 300);
    this.txtReportPath.setText((new StringBuilder(String.valueOf(System
        .getProperty("user.home"))))
        .append(System.getProperty("file.separator")).append("report.txt")
        .toString());
    this.txtDetail.setEditable(false);
    this.txtDetail.setText(this.report);
    this.txtDetail.setVisible(true);
    this.txtDetail.doLayout();
    this.lblError.setText("Error Report:");
    this.btnSave.setText("Save");
    this.btnSave.addActionListener(this);
    this.btnSaveAs.setText("Save as");
    this.btnSaveAs.addActionListener(this);
    this.btnExit.setHorizontalAlignment(0);
    this.btnExit.setSelected(false);
    this.btnExit.setText("Exit");
    this.btnExit.addActionListener(this);
    this.lblStatus.setFont(new Font("Arial", 0, 10));
    this.lblStatus.setForeground(Color.red);
    this.lblStatus.setBorder(BorderFactory.createEtchedBorder());
    this.lblStatus.setHorizontalAlignment(0);
    this.lblStatus.setHorizontalTextPosition(0);
    this.lblStatus.setText(" ");
    this.setForeground(Color.lightGray);
    GridBagConstraints c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridwidth = 0;
    c.weightx = 1.0D;
    c.weighty = 1.0D;
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = 1;
    this.getLayoutPane()
        .addLayoutLine(1.0D, 0.0D)
        .addLayoutComponent("lblError", this.lblError, 0, c)
        .addLayoutLine(1.0D, 1.0D)
        .addLayoutComponent("txtDetail", this.txtDetail, 1, c)
        .addLayoutLine(1.0D, 0.0D)
        .addLayoutComponent("txtReportPath", this.txtReportPath, 2, c)
        .addLayoutLine(1.0D, 0.0D)
        .addLineSpacer(3, 2D, 1.0D)
        .addLayoutComponent("btnSave", this.btnSave, 3, LayoutPane.SHRINKER)
        .addLayoutComponent("btnSaveAs", this.btnSaveAs, 3, LayoutPane.SHRINKER)
        .addLayoutComponent("btnExit", this.btnExit, 3, LayoutPane.SHRINKER)
        .addLayoutLine(1.0D, 0.0D)
        .addLayoutComponent("lblStatus", this.lblStatus, 4, c);
    this.updateLayout();
    ComponentTool.centerOnScreen(this);
  }

  public void open() {
    this.open(true);
  }

  public void open(boolean lock) {
    this.setVisible(true);
    if (lock) {
      this.err_lock.P();
      this.setVisible(false);
      this.dispose();
    }
  }

  private void saveReport(File outputFile) throws IOException {
    FileWriter out = new FileWriter(outputFile);
    out.write(this.report);
    out.close();
  }

  public void setStatusText(String text) {
    this.lblStatus.setText(text);
  }

  @Override
  public void setVisible(boolean v) {
    ComponentTool.centerOnScreen(this);
    super.setVisible(v);
  }
}
