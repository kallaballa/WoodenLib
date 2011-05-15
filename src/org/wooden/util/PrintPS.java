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

import java.io.FileInputStream;
import java.io.IOException;

import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Finishings;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;

public class PrintPS {

  public static void main(String args[]) {
    PrintPS ps = new PrintPS(args[0]);
  }

  public PrintPS(String filename) {
    javax.print.DocFlavor flavor = javax.print.DocFlavor.INPUT_STREAM.AUTOSENSE;
    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
    aset.add(MediaSizeName.ISO_A4);
    aset.add(new Copies(2));
    aset.add(Sides.TWO_SIDED_LONG_EDGE);
    aset.add(Finishings.STAPLE);
    PrintService pservices = PrintServiceLookup.lookupDefaultPrintService();
    System.out.println((new StringBuilder("selected printer ")).append(
        pservices.getName()).toString());
    DocPrintJob pj = pservices.createPrintJob();
    try {
      FileInputStream fis = new FileInputStream(filename);
      javax.print.Doc doc = new SimpleDoc(fis, flavor, null);
      pj.print(doc, aset);
    } catch (IOException ie) {
      ie.printStackTrace();
    } catch (PrintException e) {
      e.printStackTrace();
    }
  }
}
