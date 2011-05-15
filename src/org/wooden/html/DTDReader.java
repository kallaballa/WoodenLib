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

package org.wooden.html;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.text.html.parser.AttributeList;
import javax.swing.text.html.parser.ContentModel;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Element;
import javax.swing.text.html.parser.Entity;

public class DTDReader extends DTD {

  public DTDReader() {
    super("html32");
  }

  @Override
  protected AttributeList defAttributeList(String name, int type, int modifier,
      String value, String values, AttributeList atts) {
    Vector vals = null;
    if (values != null) {
      vals = new Vector();
      for (StringTokenizer s = new StringTokenizer(values, "|"); s
          .hasMoreTokens();) {
        String str = s.nextToken();
        if (str.length() > 0)
          vals.addElement(str);
      }

    }
    return new AttributeList(name, type, modifier, value, vals, atts);
  }

  @Override
  protected ContentModel defContentModel(int type, Object obj, ContentModel next) {
    return new ContentModel(type, obj, next);
  }

  @Override
  protected Element defElement(String name, int type, boolean omitStart,
      boolean omitEnd, ContentModel content, String exclusions[],
      String inclusions[], AttributeList atts) {
    BitSet excl = null;
    if (exclusions != null && exclusions.length > 0) {
      excl = new BitSet();
      for (String str : exclusions) {
        if (str.length() > 0)
          excl.set(this.getElement(str).getIndex());
      }

    }
    BitSet incl = null;
    if (inclusions != null && inclusions.length > 0) {
      incl = new BitSet();
      for (String str : inclusions) {
        if (str.length() > 0)
          incl.set(this.getElement(str).getIndex());
      }

    }
    return this.defineElement(name, type, omitStart, omitEnd, content, excl,
        incl, atts);
  }

  @Override
  protected Entity defEntity(String name, int type, String str) {
    int len = str.length();
    char data[] = new char[len];
    str.getChars(0, len, data, 0);
    return this.defineEntity(name, type, data);
  }

  @Override
  public void read(DataInputStream in) throws IOException {
    in.readInt();
    String names[] = new String[in.readShort()];
    System.out.println((new StringBuilder("names: ")).append(names.length)
        .toString());
    for (int i = 0; i < names.length; i++) {
      names[i] = in.readUTF();
      System.out.println(names[i]);
    }

    int num = in.readShort();
    for (int i = 0; i < num; i++) {
      short nameId = in.readShort();
      int type = in.readByte();
      String name = in.readUTF();
      this.defEntity(names[nameId], type | 0x10000, name);
    }

    num = in.readShort();
    for (int i = 0; i < num; i++) {
      short nameId = in.readShort();
      int type = in.readByte();
      byte flags = in.readByte();
      System.out.println((new StringBuilder("name: ")).append(names[nameId])
          .toString());
      System.out.println((new StringBuilder("index: ")).append(nameId)
          .toString());
      System.out.println((new StringBuilder("type: ")).append(type).toString());
      System.out
          .println((new StringBuilder("flag: ")).append(flags).toString());
      ContentModel m = this.readContentModel(in, names);
      String exclusions[] = this.readNameArray(in, names);
      String inclusions[] = this.readNameArray(in, names);
      AttributeList atts = this.readAttributeList(in, names);
      this.defElement(names[nameId], type, (flags & 1) != 0, (flags & 2) != 0,
          m, exclusions, inclusions, atts);
    }

  }

  private AttributeList readAttributeList(DataInputStream in, String names[])
      throws IOException {
    AttributeList result = null;
    int num = in.readByte();
    System.out.println((new StringBuilder("\tlists: ")).append(num).toString());
    for (; num > 0; num--) {
      short nameId = in.readShort();
      int type = in.readByte();
      int modifier = in.readByte();
      short valueId = in.readShort();
      String value = valueId != -1 ? names[valueId] : null;
      Vector values = null;
      System.out.println((new StringBuilder("\tid: ")).append(nameId)
          .toString());
      System.out.println((new StringBuilder("\ttype: ")).append(type)
          .toString());
      System.out.println((new StringBuilder("\tmod: ")).append(modifier)
          .toString());
      System.out.println((new StringBuilder("\tvalue: ")).append(valueId)
          .toString());
      short numValues = in.readShort();
      System.out.println((new StringBuilder("\tvalues: ")).append(numValues)
          .toString());
      if (numValues > 0) {
        values = new Vector(numValues);
        for (int i = 0; i < numValues; i++) {
          int index = in.readShort();
          values.addElement(names[index]);
        }

      }
      result = new AttributeList(names[nameId], type, modifier, value, values,
          result);
    }

    return result;
  }

  private ContentModel readContentModel(DataInputStream in, String names[])
      throws IOException {
    byte flag = in.readByte();
    switch (flag) {
    case 0: // '\0'
    {
      return null;
    }

    case 1: // '\001'
    {
      int type = in.readByte();
      ContentModel m = this.readContentModel(in, names);
      ContentModel next = this.readContentModel(in, names);
      return this.defContentModel(type, m, next);
    }

    case 2: // '\002'
    {
      int type = in.readByte();
      int i = in.readShort();
      Element el = this.getElement(names[i]);
      ContentModel next = this.readContentModel(in, names);
      return this.defContentModel(type, el, next);
    }
    }
    throw new IOException("bad bdtd");
  }

  private String[] readNameArray(DataInputStream in, String names[])
      throws IOException {
    int num = in.readShort();
    System.out.println((new StringBuilder("\t\tnames: ")).append(num)
        .toString());
    if (num == 0)
      return null;
    String result[] = new String[num];
    for (int i = 0; i < num; i++) {
      int index = in.readShort();
      System.out.println((new StringBuilder("\t\t\tindex: ")).append(index)
          .toString());
      result[i] = names[index];
    }

    return result;
  }
}
