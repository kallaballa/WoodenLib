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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import org.wooden.util.Configuration;
import org.wooden.util.StringTool;

// Referenced classes of package org.wooden.component:
//            LayoutPane, FileSelector, ErrorDialog, ConfigurationChooser

public abstract class ConfigurationPane extends LayoutPane implements
    ActionListener {

  private GridBagLayout layout;

  private JPanel actionContainer;

  private Box containers;

  private Box textfieldContainer;

  private Box lableContainer;

  private Box buttonContainer;

  private JButton commit;

  private JButton cancel;

  private JLabel status;

  private JMenuBar menuBar;

  private JMenu menuDatei;

  private JMenu menuRecent;

  private JMenuItem itemNew;

  private JMenuItem itemOpen;

  private JMenuItem itemSaveAs;

  private JMenuItem itemSave;

  private JMenuItem itemExit;

  private Object query_lock;

  private HashMap filequeries;

  private HashMap observedComponents;

  private FileFilter menufilter;

  private Configuration conf;

  private Vector recent;

  private File recentConfigurations;

  private boolean autoContinue;

  private boolean waiting;

  public ConfigurationPane() {
    this.layout = new GridBagLayout();
    this.actionContainer = new JPanel();
    this.containers = new Box(0);
    this.textfieldContainer = new Box(1);
    this.lableContainer = new Box(1);
    this.buttonContainer = new Box(1);
    this.commit = new JButton();
    this.cancel = new JButton();
    this.status = new JLabel();
    this.menuBar = new JMenuBar();
    this.menuDatei = new JMenu("Konfiguration");
    this.menuRecent = new JMenu("Zuletzt ge\uFFFDffnet");
    this.itemNew = new JMenuItem();
    this.itemOpen = new JMenuItem();
    this.itemSaveAs = new JMenuItem();
    this.itemSave = new JMenuItem();
    this.itemExit = new JMenuItem();
    this.query_lock = new Object();
    this.filequeries = new HashMap();
    this.observedComponents = new HashMap();
    this.recentConfigurations = new File("recent");
    this.waiting = false;
  }

  public ConfigurationPane(File defaults) throws IOException {
    this(defaults, true);
  }

  public ConfigurationPane(File defaults, boolean autocontinuation)
      throws IOException {
    this.layout = new GridBagLayout();
    this.actionContainer = new JPanel();
    this.containers = new Box(0);
    this.textfieldContainer = new Box(1);
    this.lableContainer = new Box(1);
    this.buttonContainer = new Box(1);
    this.commit = new JButton();
    this.cancel = new JButton();
    this.status = new JLabel();
    this.menuBar = new JMenuBar();
    this.menuDatei = new JMenu("Konfiguration");
    this.menuRecent = new JMenu("Zuletzt ge\uFFFDffnet");
    this.itemNew = new JMenuItem();
    this.itemOpen = new JMenuItem();
    this.itemSaveAs = new JMenuItem();
    this.itemSave = new JMenuItem();
    this.itemExit = new JMenuItem();
    this.query_lock = new Object();
    this.filequeries = new HashMap();
    this.observedComponents = new HashMap();
    this.recentConfigurations = new File("recent");
    this.waiting = false;
    this.recent = this.loadRecent();
    this.updateRecent();
    this.autoContinue = autocontinuation;
    if (defaults != null)
      this.conf = new Configuration(defaults);
    else
      this.conf = new Configuration();
    try {
      this.jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    this.checkForContinuation();
    this.addComponentListener(new LayoutPane.ResizeAdapter());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this.query_lock) {
      Object src = e.getSource();
      if (src instanceof JButton) {
        JButton b = (JButton) src;
        if (b == this.commit)
          this.applyConfiguration();
        else
          this.exit();
        if (this.waiting)
          this.query_lock.notifyAll();
      } else if (src instanceof JMenuItem) {
        JMenuItem item = (JMenuItem) src;
        if (item == this.itemNew)
          this.new_dialog();
        if (item == this.itemOpen)
          try {
            this.open_dialog();
          } catch (Exception ex) {
            (new ErrorDialog("Couldn't open configuration", ex)).open(false);
          }
        else if (item == this.itemSaveAs)
          try {
            this.save_dialog();
          } catch (Exception ex) {
            (new ErrorDialog("Couldn't save configuration", ex)).open(false);
          }
        else if (item == this.itemSave)
          try {
            this.saveConfiguration(true);
          } catch (Exception ex) {
            (new ErrorDialog("Couldn't save configuration", ex)).open(false);
          }
        else if (item == this.itemExit)
          this.exit();
        else
          try {
            File c = new File(item.getText());
            if (c.exists())
              this.openConfiguration(c);
            else
              throw new IOException((new StringBuilder("File not found: "))
                  .append(c.getAbsolutePath()).toString());
          } catch (IOException ex1) {
            this.setStatus(ex1.getMessage());
          }
      }
    }
  }

  public void addCustomActionButton(JButton btn, int pos) {
    this.actionContainer.add(btn, pos);
  }

  public void addFileSelector(String name, File defaultPath) {
    this.addFileSelector(name, defaultPath, null);
  }

  public void addFileSelector(String name, File defaultPath, FileFilter filter) {
    FileSelector fq = new FileSelector(name, defaultPath, filter, this);
    this.filequeries.put(name, fq);
    this.lableContainer.add(fq.label());
    this.textfieldContainer.add(fq.textfield());
    this.buttonContainer.add(fq.button());
    this.updateLayout();
  }

  @Override
  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line) {
    return this.addLayoutComponent(name, cmp, line, true);
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line,
      boolean observeData) {
    if (observeData)
      this.observedComponents.put(name, cmp);
    return super.addLayoutComponent(name, cmp, line);
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line,
      double weightx, double weighty, boolean observeData) {
    if (observeData)
      this.observedComponents.put(name, cmp);
    return super.addLayoutComponent(name, cmp, line, weightx, weighty);
  }

  public LayoutPane addLayoutComponent(String name, JComponent cmp, int line,
      GridBagConstraints constraints, boolean observeData) {
    if (observeData)
      this.observedComponents.put(name, cmp);
    return super.addLayoutComponent(name, cmp, line, constraints);
  }

  private void addRecent(File f) throws IOException {
    String name = f.getAbsolutePath();
    if (!this.recent.contains(name)) {
      if (this.recent.size() == 5)
        this.recent.remove(4);
      this.recent.add(name);
      this.saveRecent();
    }
  }

  public void allowContinuation(boolean allow) {
    this.commit.setEnabled(allow);
  }

  public void applyConfiguration() {
    FileSelector arrFileQueries[] = (FileSelector[]) this.filequeries.values()
        .toArray(new FileSelector[0]);
    String arrCustomKeys[] = (String[]) this.observedComponents.keySet()
        .toArray(new String[0]);
    for (FileSelector arrFileQuerie : arrFileQueries)
      this.conf.putProperty(arrFileQuerie.name(), arrFileQuerie.textfield()
          .getText());

    for (String arrCustomKey : arrCustomKeys) {
      String text = this.gatherText((JComponent) this.observedComponents
          .get(arrCustomKey));
      if (text != null)
        this.conf.putProperty(arrCustomKey, text);
    }

  }

  public void checkForContinuation() {
    if (!this.autoContinue)
      return;
    try {
      this.applyConfiguration();
      this.test();
      this.allowContinuation(true);
    } catch (IllegalArgumentException ex) {
      this.allowContinuation(false);
    }
  }

  public void close(boolean save) throws IOException {
    if (save)
      this.saveConfiguration(true);
    this.setVisible(false);
  }

  private void exit() {
    System.exit(0);
  }

  public void fillForm() {
    FileSelector arrFileQueries[] = (FileSelector[]) this.filequeries.values()
        .toArray(new FileSelector[0]);
    String arrCustomKeys[] = (String[]) this.observedComponents.keySet()
        .toArray(new String[0]);
    for (FileSelector arrFileQuerie : arrFileQueries)
      arrFileQuerie.textfield().setText(
          StringTool.eliminateNull(this.conf.getString(arrFileQuerie.name(),
              false)));

    for (String arrCustomKey : arrCustomKeys)
      this.setText((JComponent) this.observedComponents.get(arrCustomKey),
          StringTool.eliminateNull(this.conf.getString(arrCustomKey, false)));

  }

  private String gatherText(JComponent cmp) {
    if (cmp instanceof JTextComponent)
      return ((JTextComponent) cmp).getText();
    if (cmp instanceof JLabel)
      return ((JLabel) cmp).getText();
    if (cmp instanceof JSpinner)
      return ((JSpinner) cmp).getValue().toString();
    if (cmp instanceof AbstractButton)
      return ((AbstractButton) cmp).getText();
    if (cmp instanceof JScrollPane)
      return this.gatherText((JComponent) ((JScrollPane) cmp).getViewport()
          .getView());
    else
      return null;
  }

  public JButton getActionButton(int pos) {
    return (JButton) this.actionContainer.getComponent(pos);
  }

  public Container getActionContainer() {
    return this.actionContainer;
  }

  public Configuration getConfiguration() {
    return this.conf;
  }

  public JMenuBar getConfigurationMenuBar() {
    return this.menuBar;
  }

  private void jbInit() throws Exception {
    this.lableContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    this.textfieldContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
        5));
    this.buttonContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    this.status.setFont(this.commit.getFont());
    this.status.setBorder(BorderFactory.createEtchedBorder());
    this.status.setHorizontalAlignment(0);
    this.status.setHorizontalTextPosition(0);
    this.status.setVisible(true);
    this.commit.setText("OK");
    this.commit.addActionListener(this);
    this.cancel.setText("Cancel");
    this.cancel.addActionListener(this);
    this.actionContainer.setLayout(new FlowLayout());
    this.setLayout(this.layout);
    this.itemNew.setText("Neu");
    this.itemOpen.setText("\uFFFDffnen");
    this.itemSaveAs.setText("Speichern unter");
    this.itemSave.setText("Speichern");
    this.itemExit.setText("Beenden");
    this.containers.add(this.lableContainer);
    this.containers.add(this.textfieldContainer);
    this.containers.add(this.buttonContainer);
    this.actionContainer.add(this.commit);
    this.actionContainer.add(this.cancel);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridheight = 1;
    constraints.gridwidth = 0;
    constraints.weightx = 1.0D;
    constraints.weighty = 0.0D;
    constraints.fill = 1;
    this.layout.addLayoutComponent(this.containers, constraints);
    this.layout.addLayoutComponent(this.actionContainer, constraints);
    this.layout.addLayoutComponent(this.status, constraints);
    this.add(this.containers, null);
    this.add(this.actionContainer, null);
    this.add(this.status, null);
    this.menuBar.add(this.menuDatei);
    this.itemNew.addActionListener(this);
    this.itemOpen.addActionListener(this);
    this.itemSaveAs.addActionListener(this);
    this.itemSave.addActionListener(this);
    this.itemExit.addActionListener(this);
    this.menuDatei.add(this.itemNew);
    this.menuDatei.add(this.itemOpen);
    this.menuDatei.add(this.itemSave);
    this.menuDatei.add(this.itemSaveAs);
    this.menuDatei.add(this.menuRecent);
    this.menuDatei.add(this.itemExit);
    this.setStatus("Configuration not set...");
    this.updateLayout();
  }

  private Vector loadRecent() throws IOException {
    Vector r = new Vector();
    if (!this.recentConfigurations.exists()) {
      this.recentConfigurations.createNewFile();
    } else {
      BufferedReader in = new BufferedReader(new FileReader(
          this.recentConfigurations));
      String line;
      while ((line = in.readLine()) != null)
        r.add(line);
      in.close();
    }
    return r;
  }

  private void new_dialog() {
    this.conf = new Configuration();
    try {
      this.fillForm();
    } catch (IllegalArgumentException ex) {
      this.setStatus(ex.getMessage());
    }
  }

  private void open_dialog() throws IOException {
    File f = (new ConfigurationChooser(this.findRootFrame(), null, 0,
        this.menufilter)).open();
    this.openConfiguration(f);
  }

  public void openConfiguration(File f) throws IOException {
    if (f != null) {
      this.addRecent(f);
      this.updateRecent();
      this.conf = new Configuration(f);
      try {
        this.fillForm();
        this.test();
        this.setStatus("Configuration set");
      } catch (IllegalArgumentException ex) {
        this.setStatus(ex.getMessage());
      }
    }
    this.updateLayout();
  }

  public void queryConfiguration() throws IllegalArgumentException {
    if (!this.isVisible())
      throw new IllegalStateException("ConfigurationPane is not visible");
    synchronized (this.query_lock) {
      this.waiting = true;
      try {
        this.query_lock.wait();
      } catch (InterruptedException interruptedexception) {}
      this.waiting = false;
    }
  }

  public void removeActionButton(int pos) {
    this.actionContainer.remove(pos);
  }

  private void save_dialog() throws IOException {
    File c = this.getConfiguration().getConfigurationFile();
    String actualFile;
    if (c == null)
      actualFile = (new File("./")).getAbsolutePath();
    else
      actualFile = c.getAbsolutePath();
    File f = (new ConfigurationChooser(this.findRootFrame(), new JTextField(
        actualFile), 1, this.menufilter)).open();
    this.saveConfiguration(f, false);
  }

  public void saveConfiguration(boolean dialogOnFail) throws IOException {
    this.saveConfiguration(this.getConfiguration().getConfigurationFile(),
        dialogOnFail);
  }

  public void saveConfiguration(File f, boolean dialogOnFail)
      throws IOException {
    if (f != null)
      this.getConfiguration().saveConfiguration(f);
    else if (dialogOnFail)
      this.save_dialog();
  }

  private void saveRecent() throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(
        this.recentConfigurations));
    String arrRecent[] = (String[]) this.recent.toArray(new String[0]);
    for (String element : arrRecent) {
      out.write(element);
      out.newLine();
    }

    out.close();
  }

  public void setActionComponentColor(Color fore, Color back) {
    this.setRecursiveColor(this.actionContainer, fore, back);
  }

  public void setActionContainerColor(Color fore, Color back) {
    if (back != null)
      this.actionContainer.setBackground(back);
    if (fore != null)
      this.actionContainer.setForeground(fore);
  }

  public void setDefaultActionText(String commit, String abort) {
    this.commit.setText(commit);
    this.cancel.setText(abort);
  }

  public void setFileSelectorColor(Color lbl_fore, Color lbl_back,
      Color txt_fore, Color txt_back, Color btn_fore, Color btn_back) {
    FileSelector arrFileSelector[] = (FileSelector[]) this.filequeries.values()
        .toArray(new FileSelector[0]);
    for (FileSelector f : arrFileSelector) {
      if (btn_back != null)
        f.button().setBackground(btn_back);
      if (btn_fore != null)
        f.button().setForeground(btn_fore);
      if (lbl_back != null)
        f.label().setBackground(lbl_back);
      if (lbl_fore != null)
        f.label().setForeground(lbl_fore);
      if (txt_back != null)
        f.textfield().setBackground(txt_back);
      if (txt_fore != null)
        f.textfield().setForeground(txt_fore);
    }

  }

  public void setStatus(String message) {
    this.status.setText(message);
    this.updateLayout();
  }

  public void setStatusColor(Color fore, Color back) {
    if (back != null)
      this.status.setBackground(back);
    if (fore != null)
      this.status.setForeground(fore);
  }

  private void setText(JComponent cmp, String text) {
    if (cmp instanceof JTextComponent)
      ((JTextComponent) cmp).setText(text);
    else if (cmp instanceof JLabel)
      ((JLabel) cmp).setText(text);
    else if (cmp instanceof JSpinner)
      ((JSpinner) cmp).setValue(text);
    else if (cmp instanceof AbstractButton)
      ((AbstractButton) cmp).setText(text);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
  }

  public void showMenu(FileFilter mfilter) {
    this.menufilter = mfilter;
  }

  public void showStatus(boolean show) {
    this.status.setVisible(show);
  }

  public abstract void test() throws IllegalArgumentException;

  private void updateRecent() throws IOException {
    if (this.recent.size() > 0) {
      String arrRecent[] = (String[]) this.recent.toArray(new String[0]);
      this.menuRecent.setVisible(true);
      this.menuRecent.removeAll();
      for (String element : arrRecent) {
        JMenuItem item = new JMenuItem(element);
        item.addActionListener(this);
        this.menuRecent.add(item);
      }

    } else {
      this.menuRecent.setVisible(false);
    }
  }
}
