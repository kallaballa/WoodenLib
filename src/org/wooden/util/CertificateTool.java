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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.wooden.net.SecureSocketCreator;

public class CertificateTool {

  public static boolean certificateAvailable(String keystore, String id,
      String passphrase) {
    if (keystore == null || id == null || passphrase == null)
      return false;
    Certificate cert = null;
    try {
      cert = getCertificate(keystore, id, passphrase);
    } catch (Exception e) {
      return false;
    }
    return cert != null;
  }

  public static boolean checkValidity(Certificate c, Date today)
      throws IOException, ParseException {
    BufferedReader certReader = new BufferedReader(new StringReader(
        c.toString()));
    String line;
    while ((line = certReader.readLine()) != null)
      if (line.trim().startsWith("Validity:"))
        break;
    String f = line;
    String t = certReader.readLine();
    f = f.substring(f.indexOf("From:") + 5, f.indexOf(',')).trim();
    t = t.substring(t.indexOf("To:") + 3, t.indexOf(']')).trim();
    SimpleDateFormat dFormat = new SimpleDateFormat(
        "EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
    Date from = dFormat.parse(f, new ParsePosition(0));
    Date to = dFormat.parse(t, new ParsePosition(0));
    return from.before(today) && to.after(today);
  }

  public static String extractID(Certificate c) {
    String cert = c.toString();
    int begin = cert.indexOf("Subject: CN=") + 12;
    int end = cert.indexOf(",", begin);
    String id = cert.substring(begin, end);
    return id;
  }

  public static Certificate getCertificate(String keystore, String id,
      String passphrase) throws KeyStoreException {
    KeyStore ks = SecureSocketCreator.getKeystore(keystore, passphrase);
    if (ks == null)
      return null;
    else
      return ks.getCertificate(id);
  }

  public static void writeCertificate(OutputStream out, Certificate cert)
      throws IOException {
    if (cert == null) {
      throw new IOException("kein g\uFFFDltiges Zertifikat gefunden");
    } else {
      (new ObjectOutputStream(out)).writeObject(cert);
      return;
    }
  }

  public CertificateTool() {}
}
