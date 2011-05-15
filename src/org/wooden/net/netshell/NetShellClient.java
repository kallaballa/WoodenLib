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

package org.wooden.net.netshell;

import java.io.*;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import org.wooden.encryption.DesEncryption;
import org.wooden.io.ByteStreamPipe;

public class NetShellClient
{
    public class CommandConnection extends Thread
    {

        public void run()
        {
            String line;
            try
            {
                while((line = reader.readLine()) != null) 
                {
                    int padding = encCipher.getBlockSize() - line.length();
                    for(int i = 0; i < padding; i++)
                        line = (new StringBuilder(String.valueOf(line))).append(" ").toString();

                    out.write(encCipher.doFinal(line.getBytes()));
                    out.flush();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        private OutputStream out;
        private Cipher encCipher;
        private BufferedReader reader;
        private static final String MAX_PADDING = "                                                                                                                                                                                                                                                                   ";
        final NetShellClient this$0;

        public CommandConnection(OutputStream out, Cipher encCipher)
        {
            this$0 = NetShellClient.this;
            super();
            reader = new BufferedReader(new InputStreamReader(System.in));
            this.out = out;
            this.encCipher = encCipher;
        }
    }


    public NetShellClient(String host, int port, String password)
        throws Throwable
    {
        encryption = null;
        this.host = host;
        this.port = port;
        if(password != null)
            encryption = new DesEncryption(password);
    }

    public void connect()
        throws Throwable
    {
        Socket s = new Socket(host, port);
        java.io.InputStream in = null;
        OutputStream out = null;
        in = new CipherInputStream(s.getInputStream(), encryption.getDecryptionCipher());
        ByteStreamPipe pOut = null;
        (new CommandConnection(s.getOutputStream(), encryption.getEncryptionCipher())).start();
        pOut = new ByteStreamPipe(in, System.out, s.getReceiveBufferSize(), 100L, true);
        pOut.join();
        if(pOut.isAborted())
            throw pOut.abortionCause();
        else
            return;
    }

    public static void main(String args[])
    {
        try
        {
            (new NetShellClient("localhost", 9999, "password")).connect();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    private String host;
    private int port;
    private DesEncryption encryption;
}
