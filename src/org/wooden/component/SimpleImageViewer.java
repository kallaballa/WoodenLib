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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.RasterFormatException;
import java.io.File;

import javax.swing.Box;

public class SimpleImageViewer extends Box {

  private Image back;

  private Color bg_color;

  public int halign;

  public int valign;
  public static final int BOTTOM = 0;
  public static final int CENTER = 1;
  public static final int LEFT = 2;
  public static final int RIGHT = 3;
  public static final int TOP = 4;

  public SimpleImageViewer(File input, int halign, int valign, Color bg) {
    this(Toolkit.getDefaultToolkit().getImage(input.getAbsolutePath()), halign,
        valign, bg);
  }

  public SimpleImageViewer(Image back, int halign, int valign, Color bg) {
    super(2);
    this.halign = halign;
    this.valign = valign;
    this.bg_color = bg;
    this.setBackground(this.bg_color);
    this.back = back;
    for (int i = 0; i < 10 && back.getHeight(this) < 0; i++)
      try {
        Thread.sleep(100);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

    if (back.getHeight(this) < 0) {
      throw new RasterFormatException("Illegal Image Format");
    } else {
      this.setVisible(true);
      this.setBorder(null);
      return;
    }
  }

  @Override
  public void paint(Graphics g) {
    int b_w = this.back.getWidth(this);
    int b_h = this.back.getHeight(this);
    int c_w = this.getWidth();
    int c_h = this.getHeight();
    int b_x;
    if (c_w <= b_w)
      b_x = 0;
    else if (this.halign == 2)
      b_x = 0;
    else if (this.halign == 3)
      b_x = c_w - b_w;
    else if (this.halign == 1)
      b_x = (c_w - b_w) / 2;
    else
      throw new IllegalArgumentException("Illegal Horizontal Alignment");
    int b_y;
    if (c_h <= b_h)
      b_y = 0;
    else if (this.valign == 4)
      b_y = 0;
    else if (this.valign == 0)
      b_y = c_h - b_h;
    else if (this.valign == 1)
      b_y = (c_h - b_h) / 2;
    else
      throw new IllegalArgumentException("Illegal Vertical Alignment");
    System.out
        .println((new StringBuilder(String.valueOf(b_x))).append("|")
            .append(b_y).append("|").append(b_w).append("|").append(b_h)
            .toString());
    this.setForeground(this.bg_color);
    this.setBackground(this.bg_color);
    g.setColor(this.bg_color);
    g.drawRect(0, 0, c_w, c_h);
    while (!g.drawImage(this.back, b_x, b_y, b_w, b_h, this))
      ;
    g.dispose();
  }
}
