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

import java.util.HashMap;

public class WrapperAccessMap extends HashMap {

  public WrapperAccessMap() {}

  public WrapperAccessMap(int initSize) {
    super(initSize);
  }

  public WrapperAccessMap(WrapperAccessMap map) {
    super(map);
  }

  public boolean getBoolean(String id) {
    return this.getWrapperAccess(id).getBoolean();
  }

  public byte getByte(String id) {
    return this.getWrapperAccess(id).getByte();
  }

  public char getCharacter(String id) {
    return this.getWrapperAccess(id).getCharacter();
  }

  public double getDouble(String id) {
    return this.getWrapperAccess(id).getDouble();
  }

  public float getFloat(String id) {
    return this.getWrapperAccess(id).getFloat();
  }

  public int getInt(String id) {
    return this.getWrapperAccess(id).getInt();
  }

  public long getLong(String id) {
    return this.getWrapperAccess(id).getLong();
  }

  public short getShort(String id) {
    return this.getWrapperAccess(id).getShort();
  }

  public String getString(String id) {
    return this.getWrapperAccess(id).getString();
  }

  public WrapperAccess getWrapperAccess(String id) {
    return (WrapperAccess) this.get(id);
  }

  public boolean parseBoolean(String id) {
    return Boolean.valueOf(this.getString(id)).booleanValue();
  }

  public byte parseByte(String id) {
    return Byte.parseByte(this.getString(id));
  }

  public char parseCharacter(String id) {
    return this.getString(id).charAt(0);
  }

  public double parseDouble(String id) {
    return Double.parseDouble(this.getString(id));
  }

  public float parseFloat(String id) {
    return Float.parseFloat(this.getString(id));
  }

  public int parseInt(String id) {
    return Integer.parseInt(this.getString(id));
  }

  public long parseLong(String id) {
    return Long.parseLong(this.getString(id));
  }

  public short parseShort(String id) {
    return Short.parseShort(this.getString(id));
  }

  @Override
  public Object put(Object key, Object value) {
    return super.put(key, new WrapperAccess(value));
  }
}
