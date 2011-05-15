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

package org.wooden.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class DesEncryption {

  private Cipher ecipher;

  private Cipher dcipher;

  private byte buf[];

  private byte salt[] = { -87, -101, -56, 50, 86, 53, -29, 3 };

  private int iterationCount;

  public DesEncryption(String passPhrase) throws DesEncryptionException {
    this.buf = new byte[1024];
    this.iterationCount = 19;
    try {
      this.initCypherSuite(passPhrase);
    } catch (Exception e) {
      throw new DesEncryptionException(e);
    }
  }

  public void decrypt(InputStream in, OutputStream out) throws IOException {
    in = new CipherInputStream(in, this.getDecryptionCipher());
    for (int numRead = 0; (numRead = in.read(this.buf)) >= 0;)
      out.write(this.buf, 0, numRead);

    out.close();
    in.close();
  }

  public void encrypt(InputStream in, OutputStream out) throws IOException {
    out = new CipherOutputStream(out, this.getEncryptionCipher());
    for (int numRead = 0; (numRead = in.read(this.buf)) >= 0;)
      out.write(this.buf, 0, numRead);

    out.close();
    in.close();
  }

  public Cipher getDecryptionCipher() {
    return this.dcipher;
  }

  public Cipher getEncryptionCipher() {
    return this.ecipher;
  }

  private void initCypherSuite(String passPhrase) throws Exception {
    java.security.spec.KeySpec keySpec = new PBEKeySpec(
        passPhrase.toCharArray(), this.salt, this.iterationCount);
    SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
        .generateSecret(keySpec);
    this.ecipher = Cipher.getInstance(key.getAlgorithm());
    this.dcipher = Cipher.getInstance(key.getAlgorithm());
    java.security.spec.AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
        this.salt, this.iterationCount);
    this.ecipher.init(1, key, paramSpec);
    this.dcipher.init(2, key, paramSpec);
  }
}
