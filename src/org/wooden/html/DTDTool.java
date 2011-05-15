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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.text.html.parser.AttributeList;
import javax.swing.text.html.parser.ContentModel;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Element;
import javax.swing.text.html.parser.Entity;

public class DTDTool {
  private class NamesMap extends HashMap {

    private int index;

    private NamesMap() {
      super();
    }

    NamesMap(NamesMap namesmap) {
      this();
    }

    public boolean addNameIndex(String name) {
      if (name == null)
        throw new IllegalArgumentException("The name can't be null");
      if (!this.containsKey(name))
        this.put(name, new Integer(this.index++));
      else
        return false;
      return true;
    }

    public int getIndex() {
      return this.index;
    }

    public int getNameIndex(String name) {
      if (name == null)
        throw new IllegalArgumentException("The name can't be null");
      if (this.containsKey(name))
        return ((Integer) this.get(name)).intValue();
      else
        throw new RuntimeException((new StringBuilder("Name index for: "))
            .append(name).append(" not found").toString());
    }

    public String[] getNamesSortedByIndex() {
      int maxIndex = this.getIndex();
      String keys[] = (String[]) this.keySet().toArray(new String[0]);
      String names[] = new String[maxIndex];
      for (int i = 0; i < maxIndex; i++)
        names[this.getNameIndex(keys[i])] = keys[i];

      return names;
    }
  }

  private static PrintStream logger;

  public static void main(String args[]) {
    try {
      DTD dtd = DTD.getDTD("html32");
      dtd.read(new DataInputStream(new FileInputStream(
          "D:/Programming/develop/VADV/html32.bdtd")));
      (new DTDTool()).writeDTD(new DataOutputStream(new FileOutputStream(
          "D:/Programming/develop/VADV/htmlcustom.bdtd")), dtd);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public DTDTool() throws IOException {
    logger = new PrintStream(new FileOutputStream("dtd.out"));
  }

  private int createFlag(boolean omitStart, boolean omitEnd) {
    int flag = 0;
    if (omitStart)
      flag |= 1;
    if (omitEnd)
      flag |= 2;
    return flag;
  }

  private NamesMap createNamesMap(Entity entities[], Element elements[]) {
    NamesMap namesHash = new NamesMap(null);
    for (Entity entitie : entities)
      namesHash.addNameIndex(entitie.getName());

    for (Element element : elements) {
      namesHash.addNameIndex(element.getName());
      AttributeList list = element.getAttributes();
      if (list != null)
        do {
          namesHash.addNameIndex(list.getName());
          String val = list.getValue();
          if (val != null)
            namesHash.addNameIndex(val);
          Enumeration values;
          if ((values = list.getValues()) != null)
            for (; values.hasMoreElements(); namesHash.addNameIndex(values
                .nextElement().toString()))
              ;
        } while ((list = list.getNext()) != null);
    }

    return namesHash;
  }

  private void writeAttibuteList(DataOutputStream out, AttributeList list,
      NamesMap namesHash) throws IOException {
    Vector lists = new Vector();
    int numLists;
    if (list != null) {
      do
        lists.add(list);
      while ((list = list.getNext()) != null);
      numLists = lists.size();
    } else {
      numLists = 0;
    }
    logger.println((new StringBuilder("\t\tLists: ")).append(numLists)
        .toString());
    out.writeByte(numLists);
    for (int i = 0; i < numLists; i++) {
      list = (AttributeList) lists.get(i);
      logger.println((new StringBuilder("\t\t")).append(list.getName())
          .append('/').append(list.getType()).append('/')
          .append(list.getModifier()).toString());
      out.writeShort(namesHash.getNameIndex(list.getName()));
      out.writeByte(list.getType());
      out.writeByte(list.getModifier());
      String val = list.getValue();
      if (val == null)
        out.writeShort(-1);
      else
        out.writeShort(namesHash.getNameIndex(val));
      Vector values = list.values;
      int numValues;
      if (values == null)
        numValues = 0;
      else
        numValues = values.size();
      out.writeShort(numValues);
      if (numValues > 0) {
        for (int j = 0; j < numValues; j++) {
          logger.println((new StringBuilder("\t\t\t")).append(
              values.get(j).toString()).toString());
          out.writeShort(namesHash.getNameIndex(values.get(j).toString()));
        }

      }
    }

  }

  private void writeContentModel(DataOutputStream out, ContentModel m,
      NamesMap namesHash) throws IOException {
    this.writeContentModel(out, m, namesHash, 0);
  }

  private void writeContentModel(DataOutputStream out, ContentModel m,
      NamesMap namesHash, int depth) throws IOException {
    String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    String t = tabs.substring(0, depth + 2);
    depth++;
    byte flag;
    if (m == null)
      flag = 0;
    else if (m.content instanceof ContentModel)
      flag = 1;
    else if (m.content instanceof Element)
      flag = 2;
    else
      throw new RuntimeException();
    if (flag != 0)
      logger.print((new StringBuilder(String.valueOf(t)))
          .append("ContentModel: ").append(flag).append('/').append(m.type)
          .toString());
    out.writeByte(flag);
    switch (flag) {
    case 1: // '\001'
      out.writeByte(m.type);
      this.writeContentModel(out, (ContentModel) m.content, namesHash, depth);
      this.writeContentModel(out, m.next, namesHash, depth);
      break;

    case 2: // '\002'
      logger.println((new StringBuilder("\r\n\t")).append(t)
          .append("Element: ").append(((Element) m.content).getName())
          .toString());
      out.writeByte(m.type);
      out.writeShort(namesHash.getNameIndex(((Element) m.content).getName()));
      this.writeContentModel(out, m.next, namesHash, depth);
      break;
    }
  }

  public void writeDTD(DataOutputStream out, DTD dtd) throws IOException {
    long start = System.currentTimeMillis();
    final Entity entities[] = dtd.entityHash.values().toArray(new Entity[0]);
    final Element elements[] = dtd.elementHash.values().toArray(new Element[0]);
    final NamesMap namesHash = this.createNamesMap(entities, elements);
    ByteArrayOutputStream entityBuffer = new ByteArrayOutputStream();
    ByteArrayOutputStream elementBuffer = new ByteArrayOutputStream();
    final DataOutputStream outEntity = new DataOutputStream(entityBuffer);
    final DataOutputStream outElement = new DataOutputStream(elementBuffer);
    Thread entityWorker = new Thread() {

      @Override
      public void run() {
        try {
          DTDTool.this.writeEntities(outEntity, entities, namesHash);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    };
    Thread elementWorker = new Thread() {

      @Override
      public void run() {
        try {
          DTDTool.this.writeElements(outElement, elements, namesHash);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    };
    this.writeFileVersion(out, dtd);
    this.writeNames(out, namesHash.getNamesSortedByIndex());
    try {
      entityWorker.start();
      entityWorker.join();
      elementWorker.start();
      elementWorker.join();
    } catch (InterruptedException interruptedexception) {}
    out.write(entityBuffer.toByteArray());
    out.write(elementBuffer.toByteArray());
    out.close();
    System.err.println(System.currentTimeMillis() - start);
  }

  private void writeElements(DataOutputStream out, Element elements[],
      NamesMap namesHash) throws IOException {
    logger.println((new StringBuilder("Elements: ")).append(elements.length)
        .toString());
    out.writeShort(elements.length);
    for (Element element : elements) {
      out.writeShort(namesHash.getNameIndex(element.getName()));
      logger.println((new StringBuilder(String.valueOf('\t')))
          .append(element.getName()).append('/').append(element.getType())
          .append('/')
          .append(this.createFlag(element.omitStart(), element.omitEnd()))
          .toString());
      out.writeByte(element.getType());
      out.writeByte(this.createFlag(element.omitStart(), element.omitEnd()));
      this.writeContentModel(out, element.content, namesHash);
      logger.println("\t\tExclusions: ");
      this.writeNameIndexArray(out, element.exclusions, namesHash);
      logger.println("\t\tInclusions: ");
      this.writeNameIndexArray(out, element.inclusions, namesHash);
      this.writeAttibuteList(out, element.atts, namesHash);
    }

  }

  private void writeEntities(DataOutputStream out, Entity entities[],
      NamesMap namesHash) throws IOException {
    logger.println((new StringBuilder("Entities: ")).append(entities.length)
        .toString());
    out.writeShort(entities.length);
    for (Entity entitie : entities) {
      logger.println((new StringBuilder(String.valueOf('\t')))
          .append(entitie.getName()).append('/').append(entitie.getType())
          .append('/').append(entitie.getString()).toString());
      out.writeShort(namesHash.getNameIndex(entitie.getName()));
      out.writeByte(entitie.getType());
      out.writeUTF(entitie.getString());
    }

  }

  private void writeFileVersion(DataOutputStream out, DTD dtd)
      throws IOException {
    out.writeInt(1);
  }

  private void writeNameIndexArray(DataOutputStream out, BitSet set,
      NamesMap namesHash) throws IOException {
    if (set != null) {
      Vector indeces = new Vector();
      String names[] = namesHash.getNamesSortedByIndex();
      for (int bitIndex = 0; (bitIndex = set.nextSetBit(bitIndex)) > 0; bitIndex++)
        indeces.add(new Integer(bitIndex));

      int size = indeces.size();
      out.writeShort(size);
      for (int i = 0; i < size; i++) {
        logger.println((new StringBuilder("\t\t")).append(
            names[((Integer) indeces.get(i)).shortValue()]).toString());
        out.writeShort(((Integer) indeces.get(i)).shortValue());
      }

    } else {
      out.writeShort(0);
    }
  }

  private void writeNames(DataOutputStream out, String names[])
      throws IOException {
    out.writeShort(names.length);
    for (String name : names) {
      out.writeUTF(name);
    }

  }

}
