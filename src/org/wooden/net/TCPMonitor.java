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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMonitor extends Thread {
  public class MonitoredPipe extends Thread {

    private InputStream in;

    private OutputStream out;
    private String messagePrefix;

    public MonitoredPipe(InputStream in, OutputStream out, String messagePrefix) {
      super();
      this.in = in;
      this.out = out;
      this.messagePrefix = messagePrefix;
    }

    @Override
    public void run() {
      try {
        StringBuffer lineBuffer = new StringBuffer(this.messagePrefix);
        int d;
        while ((d = this.in.read()) != -1) {
          if ((char) d == '\n') {
            System.out.println(lineBuffer.toString());
            lineBuffer = new StringBuffer(this.messagePrefix);
          } else {
            lineBuffer.append((char) d);
          }
          this.out.write(d);
          this.out.flush();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        this.out.close();
      } catch (IOException ioexception) {}
      try {
        this.in.close();
      } catch (IOException ioexception1) {}
    }
  }

  public static void main(String args[]) {
    try {
      (new TCPMonitor(Integer.parseInt(args[0]), args[1],
          Integer.parseInt(args[2]))).start();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private int listenerPort;

  private String targetHost;

  private int targetPort;
  private ServerSocket server;

  public TCPMonitor(int listenerPort, String targetHost, int targetPort)
      throws IOException {
    this.listenerPort = listenerPort;
    this.targetHost = targetHost;
    this.targetPort = targetPort;
    this.server = new ServerSocket(listenerPort);
  }

  @Override
  public void run() {
    try {
      while (true) {
        Socket listenerSocket = this.server.accept();
        System.err.println("Listener connected...");
        Socket targetSocket = new Socket(this.targetHost, this.targetPort);
        System.err.println("Target connected...");
        InputStream lin = listenerSocket.getInputStream();
        OutputStream lout = listenerSocket.getOutputStream();
        InputStream tin = targetSocket.getInputStream();
        OutputStream tout = targetSocket.getOutputStream();
        MonitoredPipe lPipe = new MonitoredPipe(lin, tout, ">> ");
        MonitoredPipe tPipe = new MonitoredPipe(tin, lout, "<< ");
        lPipe.start();
        tPipe.start();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
