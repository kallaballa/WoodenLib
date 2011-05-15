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

public class WrapperAccess {
  private Object o;

  public WrapperAccess(Object o) {
    this.o = o;
  }

  public boolean getBoolean() {
    return ((Boolean) this.o).booleanValue();
  }

  public byte getByte() {
    return ((Byte) this.o).byteValue();
  }

  public char getCharacter() {
    return ((Character) this.o).charValue();
  }

  public double getDouble() {
    return ((Double) this.o).doubleValue();
  }

  public float getFloat() {
    return ((Float) this.o).floatValue();
  }

  public int getInt() {
    return ((Integer) this.o).intValue();
  }

  public long getLong() {
    return ((Long) this.o).longValue();
  }

  public short getShort() {
    return ((Short) this.o).shortValue();
  }

  public String getString() {
    return this.o.toString();
  }

  public boolean parseBoolean() {
    return Boolean.valueOf(this.getString()).booleanValue();
  }

  public byte parseByte() {
    return Byte.parseByte(this.getString());
  }

  public char parseCharacter() {
    return this.getString().charAt(0);
  }

  public double parseDouble() {
    return Double.parseDouble(this.getString());
  }

  public float parseFloat() {
    return Float.parseFloat(this.getString());
  }

  public int parseInt() {
    return Integer.parseInt(this.getString());
  }

  public long parseLong() {
    return Long.parseLong(this.getString());
  }

  public short parseShort() {
    return Short.parseShort(this.getString());
  }

  @Override
  public String toString() {
    return this.getString();
  }
}
