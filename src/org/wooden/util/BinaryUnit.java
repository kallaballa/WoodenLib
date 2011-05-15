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

package org.wooden.util;

public class BinaryUnit {

  public static final int BYTE = 0;

  public static final int KILOBYTE = 1;

  public static final int MEGABYTE = 2;

  public static final int GIGABYTE = 3;

  public static final int TERABYTE = 4;

  private static final String UNITS[] = { "B", "KB", "MB", "GB", "TB" };
  private static final double KILO = 1024D;
  private double val;
  private int unit;

  public BinaryUnit(double v, int u) {
    this.val = v;
    for (this.unit = u; this.unit < 4 && (v /= 1024D) > 1.0D; this.unit++)
      this.val = v;

  }

  public int getMaxUnit() {
    return this.unit;
  }

  public String getMaxUnitString() {
    return UNITS[this.unit];
  }

  public double getMaxVal() {
    return this.val;
  }

  @Override
  public String toString() {
    return (float) this.val + ' ' + UNITS[this.unit];
  }

}
