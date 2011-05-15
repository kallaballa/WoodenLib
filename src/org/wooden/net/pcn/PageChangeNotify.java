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

package org.wooden.net.pcn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.wooden.component.ConfigurationPane;
import org.wooden.component.ErrorDialog;
import org.wooden.net.http.HTTPDownloader;

public class PageChangeNotify extends ConfigurationPane implements
    ActionListener {
  public class Notifier extends Thread {

    public Notifier() {
      super();
    }

    @Override
    public void run() {
      try {
        while (true) {
          String compare = new String(HTTPDownloader.download(new URL(
              PageChangeNotify.this.txtUrl.getText())));
          if (!compare.equals(PageChangeNotify.this.page)) {
            (new ErrorDialog("PAGE CHANGED", new RuntimeException()))
                .open(false);
            PageChangeNotify.this.page = compare;
          }
          Thread.sleep(1000L);
        }
      } catch (Throwable e) {
        (new ErrorDialog("Error", e)).open();
      }
    }
  }

  public static void main(String args[]) {
    try {
      JFrame frame = new JFrame();
      frame.setContentPane(new PageChangeNotify());
      frame.setSize(400, 75);
      frame.setVisible(true);
      frame.validate();
      frame.setDefaultCloseOperation(3);
      frame = frame;
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private String page;

  public JTextField txtUrl;

  public JButton btnStart;

  public JButton btnHide;

  private static JFrame frame;

  public PageChangeNotify() {
    this.txtUrl = new JTextField(" ");
    this.btnStart = new JButton("Start");
    this.btnHide = new JButton("Hide");
    this.init();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      if (e.getSource() == this.btnStart) {
        this.page = new String(HTTPDownloader.download(new URL(this.txtUrl
            .getText())));
        System.out.println("1");
        (new Notifier()).start();
      } else {
        frame.setVisible(false);
      }
    } catch (Exception e1) {
      (new ErrorDialog("Error", e1)).open();
    }
  }

  public void init() {
    this.btnStart.addActionListener(this);
    this.btnHide.addActionListener(this);
    this.addLayoutLine().addLayoutComponent("url", this.txtUrl, 0)
        .addLayoutLine()
        .addLayoutComponent("start", this.btnStart, 1, 2D, 0.0D)
        .addLayoutComponent("hide", this.btnHide, 1, 0.0D, 0.0D);
    this.setSize(400, 70);
    this.updateLayout();
  }

  @Override
  public void test() {}
}
