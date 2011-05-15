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

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ColoredTextPane extends JTextPane {

  private DefaultStyledDocument m_defaultStyledDocument;

  private Color defaultTextColor;

  public ColoredTextPane() {
    this(Color.black);
  }

  public ColoredTextPane(Color defaultTextColor) {
    this.m_defaultStyledDocument = new DefaultStyledDocument();
    this.setDocument(this.m_defaultStyledDocument);
    this.defaultTextColor = defaultTextColor;
  }

  public void append(String string) throws BadLocationException {
    this.append(string, this.defaultTextColor);
  }

  public void append(String string, Color color) throws BadLocationException {
    SimpleAttributeSet attr = new SimpleAttributeSet();
    StyleConstants.setForeground(attr, color);
    this.m_defaultStyledDocument.insertString(
        this.m_defaultStyledDocument.getLength(), string, attr);
  }
}
