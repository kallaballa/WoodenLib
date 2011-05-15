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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

public class TextHolder extends HashMap {

  private int VALUES_MAX;

  private int PAGE_MAX;

  private String ARR_VARIABLES[];

  private BufferedReader READER;

  private String FILE_NAME;

  public TextHolder() {}

  private Vector addValue(String Name, String Value) {
    Vector vecTmp = (Vector) this.get(Name);
    vecTmp.add(Value);
    return vecTmp;
  }

  private boolean containsVariable(String name) {
    return this.containsKey(name);
  }

  public String getFileName() {
    return this.FILE_NAME;
  }

  public String[] getNames() {
    return this.getStringArray(this.keySet().toArray());
  }

  public int getPageMax() {
    return this.PAGE_MAX;
  }

  private BufferedReader getReader() {
    return this.READER;
  }

  public String[] getStringArray(Object arrObj[]) {
    String arrString[] = new String[arrObj.length];
    for (int i = 0; i < arrObj.length; i++)
      arrString[i] = (String) arrObj[i];

    return arrString;
  }

  public String getText(String name, int i)
      throws ArrayIndexOutOfBoundsException {
    if (name.trim().equals("[VARCOUNT]")) {
      return String.valueOf(this.VALUES_MAX);
    } else {
      String arrTmp[] = this.getTextArray(name);
      return arrTmp[i];
    }
  }

  public String[] getTextArray(String name) {
    return this.getStringArray(((Vector) this.get(name)).toArray());
  }

  public int getValuesMax() {
    return this.VALUES_MAX;
  }

  public String[] getVariables() {
    return this.ARR_VARIABLES;
  }

  public boolean hasMoreText(String name, int iSize) {
    return ((Vector) this.get(name)).size() > iSize + 1;
  }

  private Vector initValue(String Name, String Value) {
    Vector vecTmp = new Vector();
    vecTmp.add(Value);
    return vecTmp;
  }

  public void load(String strFileName) throws IOException {
    this.setReader(new BufferedReader(new InputStreamReader(
        new FileInputStream(strFileName))));
    this.setVariables(this.loadVariables());
    this.loadValues();
    this.setValuesMax(this.getTextArray(this.getVariables()[0]).length);
    this.setFileName(strFileName);
  }

  private void loadValues() throws IOException {
    String arrVars[] = this.getVariables();
    BufferedReader brText = this.getReader();
    int i = 0;
    String line;
    while ((line = brText.readLine()) != null) {
      this.putValue(arrVars[i], line);
      if (++i == arrVars.length)
        i = 0;
    }
  }

  private String[] loadVariables() throws IOException {
    BufferedReader brText = this.getReader();
    Vector vecVars = new Vector();
    String line;
    for (; !(line = brText.readLine()).startsWith("[DEC:END]"); vecVars
        .add(line.substring(1, line.length() - 1)))
      ;
    return (String[]) vecVars.toArray(new String[0]);
  }

  private void putValue(String Name, String Value) {
    if (this.containsKey(Name))
      this.put(Name, this.addValue(Name, Value));
    else
      this.put(Name, this.initValue(Name, Value));
  }

  public void setFileName(String strFilename) {
    this.FILE_NAME = strFilename;
  }

  private void setPageMax(int intPageMax) {
    this.PAGE_MAX = intPageMax;
  }

  private void setReader(BufferedReader brReader) {
    this.READER = brReader;
  }

  private void setValuesMax(int intValMax) {
    this.VALUES_MAX = intValMax;
  }

  private void setVariables(String arrVarliables[]) {
    this.ARR_VARIABLES = arrVarliables;
  }
}
