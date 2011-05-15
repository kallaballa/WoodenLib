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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;

public class HTMLDocument extends HTMLNode {

  private Stack STACK_NODES;

  private String PARENT_PATH;

  private static final String BREAKING_NODES[] = { "meta", "br", "img", "link",
      "input" };

  private static Vector VEC_BREAKING_NODES = new Vector();

  public HTMLDocument(File fileInput) {
    this.STACK_NODES = new Stack();
    try {
      VEC_BREAKING_NODES = this.toVector(BREAKING_NODES);
      this.PARENT_PATH = fileInput.getParent();
      char arrFile[] = this.loadFile(new FileInputStream(fileInput));
      HTMLNode rootNode = new HTMLNode();
      arrFile = rootNode.readNode(arrFile);
      this.applyNode(rootNode);
      this.pushNode(this);
      this.parseNodes(arrFile, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public HTMLDocument cloneDocument() {
    return (HTMLDocument) this.cloneNode();
  }

  private int getStackSize() {
    return this.STACK_NODES.size();
  }

  public boolean isBreakingNode(String strType) {
    String strNodeType = strType.substring(1);
    return VEC_BREAKING_NODES.contains(strNodeType);
  }

  public char[] loadFile(InputStream inDocu) throws IOException {
    DataInputStream in = new DataInputStream(inDocu);
    StringBuffer sb = new StringBuffer();
    int c;
    while ((c = in.readChar()) != '\uFFFF')
      sb.append(c);
    return sb.toString().toCharArray();
  }

  private void parseNodes(char arrDocument[], int iVar) throws Exception {
    HTMLNode nodeActual = new HTMLNode();
    arrDocument = nodeActual.readNode(arrDocument);
    String strType = nodeActual.getType();
    if (this.isBreakingNode(strType)) {
      this.peekNode().addNode(nodeActual);
      this.pushNode(nodeActual);
      this.popNode();
      if (this.stackIsEmpty())
        return;
    } else if (strType.startsWith("</")) {
      this.popNode();
      if (this.stackIsEmpty())
        return;
    } else {
      this.peekNode().addNode(nodeActual);
      this.pushNode(nodeActual);
    }
    this.parseNodes(arrDocument, 0);
  }

  private HTMLNode peekNode() {
    return (HTMLNode) this.STACK_NODES.peek();
  }

  private HTMLNode popNode() {
    return (HTMLNode) this.STACK_NODES.pop();
  }

  private void pushNode(HTMLNode node) {
    this.STACK_NODES.push(node);
  }

  private boolean stackIsEmpty() {
    return this.STACK_NODES.isEmpty();
  }

  private Vector toVector(String arrString[]) {
    Vector vecTmp = new Vector();
    for (String element : arrString)
      vecTmp.add(element);

    return vecTmp;
  }

}
