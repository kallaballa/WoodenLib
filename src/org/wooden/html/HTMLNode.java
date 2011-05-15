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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class HTMLNode extends Vector {

  private String NODE_TEXT;

  private String NODE_TYPE;

  private HashMap HASH_PARAMS;

  public HTMLNode() {
    this.HASH_PARAMS = new HashMap();
  }

  public HTMLNode(char arrNode[]) {
    this.HASH_PARAMS = new HashMap();
    this.readNode(arrNode);
  }

  public HTMLNode(String strNode) {
    this(strNode.toCharArray());
  }

  private String addHyphens(String strTemp) {
    return (new StringBuilder("\"")).append(strTemp).append("\"").toString();
  }

  public void addNode(HTMLNode node) {
    this.add(node);
  }

  public void applyNode(HTMLNode node) {
    this.setText(node.getText());
    this.setType(node.getType());
    this.setParameters(node.getParameters());
    for (int iSubNodes = 0; iSubNodes < node.getNodeCount(); iSubNodes++)
      this.addNode(node.getNode(iSubNodes));

  }

  public void applyParameters(Iterator keys, Iterator entries) {
    HashMap hashNewParams = new HashMap();
    for (; keys.hasNext(); hashNewParams.put(keys.next(), entries.next()))
      ;
    this.HASH_PARAMS = hashNewParams;
  }

  public HTMLNode cloneNode() {
    HTMLNode nodeCloned = (HTMLNode) this.clone();
    nodeCloned.applyParameters(this.getParameters().keySet().iterator(), this
        .getParameters().values().iterator());
    return nodeCloned;
  }

  private String elemenateNull(String strNull) {
    if (strNull == null)
      return "";
    else
      return strNull;
  }

  public String getJAction() {
    return this.getParameter("JAction");
  }

  private char getNextChar(char arrChars[], int intOffset) {
    for (int i = intOffset + 1; i < arrChars.length; i++)
      if (arrChars[i] != ' ')
        return arrChars[i];

    return '\uFFFF';
  }

  public HTMLNode getNode(int i) {
    return (HTMLNode) this.get(i);
  }

  public int getNodeCount() {
    return this.size();
  }

  public String getParameter(String name) {
    return (String) this.HASH_PARAMS.get(name);
  }

  public HashMap getParameters() {
    return this.HASH_PARAMS;
  }

  public String getParameterString() {
    Object arrParams[] = this.HASH_PARAMS.keySet().toArray();
    StringBuffer sbParams = new StringBuffer();
    for (Object arrParam : arrParams) {
      String strParam = arrParam.toString();
      sbParams.append(" ").append(strParam).append("=")
          .append(this.addHyphens(this.getParameter(strParam))).append(" ");
    }

    return sbParams.toString();
  }

  private char[] getRest(char oldArray[], int intOffset) {
    char newArray[] = new char[oldArray.length - intOffset];
    System.arraycopy(oldArray, intOffset, newArray, 0, newArray.length);
    return newArray;
  }

  public String getText() {
    return this.NODE_TEXT;
  }

  public String getType() {
    return this.NODE_TYPE;
  }

  public boolean hasJAction() {
    return this.getParameter("JAction") != null;
  }

  public void putParameter(String name, String value) {
    this.HASH_PARAMS.put(name, value);
  }

  public char[] readNode(char arrNode[]) {
    arrNode = this.readText(this.readParameters(this.readType(arrNode)));
    return arrNode;
  }

  private char[] readParameters(char arrNode[]) {
    int iParams;
    for (iParams = 0; iParams < arrNode.length; iParams++) {
      if (arrNode[iParams] == '>')
        break;
      StringBuffer sbName = new StringBuffer();
      StringBuffer sbValue = new StringBuffer();
      for (; iParams < arrNode.length; iParams++) {
        if (arrNode[iParams] == '='
            && this.getNextChar(arrNode, iParams) == '"')
          break;
        sbName.append(arrNode[iParams]);
      }

      for (; iParams < arrNode.length; iParams++) {
        if (arrNode[iParams] != '"')
          continue;
        for (iParams++; iParams < arrNode.length; iParams++) {
          if (arrNode[iParams] == '"')
            break;
          sbValue.append(arrNode[iParams]);
        }

        break;
      }

      this.setParameter(sbName.toString().trim(), sbValue.toString().trim());
    }

    return this.getRest(arrNode, iParams);
  }

  private char[] readText(char arrNode[]) {
    int iText = 0;
    StringBuffer sbText = new StringBuffer();
    for (; iText < arrNode.length; iText++) {
      if (arrNode[iText] == '<')
        break;
      sbText.append(arrNode[iText]);
    }

    this.setText(sbText.toString().trim());
    return this.getRest(arrNode, iText);
  }

  private char[] readType(char arrNode[]) {
    StringBuffer sbType = new StringBuffer();
    for (int iBytes = 0; iBytes < arrNode.length; iBytes++) {
      if (arrNode[iBytes] == '>')
        break;
      if (arrNode[iBytes] == '<') {
        for (; arrNode[iBytes] == ' '; iBytes++)
          ;
        int iType;
        for (iType = 0; arrNode[iType] != ' ' && arrNode[iType] != '>'; iType++)
          sbType.append(arrNode[iType]);

        this.setType(sbType.toString());
        return this.getRest(arrNode, iBytes + iType);
      }
    }

    return null;
  }

  public void removeAllNodes() {
    this.removeAllElements();
  }

  public void removeParameter(String name) {
    this.HASH_PARAMS.remove(name);
  }

  public void replaceNode(int pos, HTMLNode newNode) {
    this.remove(pos);
    this.add(pos, newNode);
  }

  private void setParameter(String name, String value) {
    this.HASH_PARAMS.put(name, value);
  }

  public void setParameters(HashMap hashParameters) {
    this.HASH_PARAMS = hashParameters;
  }

  public void setText(String newText) {
    this.NODE_TEXT = newText;
  }

  public void setType(String strType) {
    this.NODE_TYPE = strType;
  }
}
