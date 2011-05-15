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

package org.wooden.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SecureSocketCreator {

  public static SSLSocket createSSLSocket(Socket s, String host, int port,
      String keyStore, String passPhrase, String protokoll) throws IOException {
    SSLSocketFactory factory = null;
    try {
      KeyStore ks = getKeystore(keyStore, passPhrase);
      char passphrase[] = passPhrase.toCharArray();
      SSLContext ctx = SSLContext.getInstance(protokoll);
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, passphrase);
      ctx.init(kmf.getKeyManagers(), null, null);
      factory = ctx.getSocketFactory();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
    SSLSocket socket = (SSLSocket) factory.createSocket(s, host, port, true);
    socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
    socket.setUseClientMode(false);
    socket.setWantClientAuth(false);
    socket.setNeedClientAuth(false);
    socket.startHandshake();
    return socket;
  }

  public static SSLSocket createSSLSocket(String host, int port,
      String keyStore, String passPhrase, String protokoll) throws IOException {
    SSLSocketFactory factory = null;
    try {
      KeyStore ks = getKeystore(keyStore, passPhrase);
      char passphrase[] = passPhrase.toCharArray();
      SSLContext ctx = SSLContext.getInstance(protokoll);
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, passphrase);
      ctx.init(kmf.getKeyManagers(), null, null);
      factory = ctx.getSocketFactory();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
    SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
    socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
    socket.setWantClientAuth(false);
    socket.setNeedClientAuth(false);
    socket.startHandshake();
    return socket;
  }

  public static KeyStore getKeystore(String keystore, String passphrase)
      throws KeyStoreException {
    keystore.trim();
    passphrase.trim();
    KeyStore ks = null;
    try {
      ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream(keystore), passphrase.toCharArray());
    } catch (Exception e) {
      throw new KeyStoreException(e.getMessage());
    }
    return ks;
  }

  public SecureSocketCreator() {}
}
