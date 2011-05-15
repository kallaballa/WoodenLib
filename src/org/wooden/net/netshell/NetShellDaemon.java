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
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.wooden.encryption.DesEncryption;
import org.wooden.util.ProcessHandler;

public class NetShellDaemon
{
    public class TelnetConnection extends Thread
    {

        public void run()
        {
            InputStream in = null;
            OutputStream out = null;
            try
            {
                if(encryption != null)
                {
                    in = new CipherInputStream(s.getInputStream(), encryption.getDecryptionCipher());
                    out = new CipherOutputStream(s.getOutputStream(), encryption.getEncryptionCipher());
                } else
                {
                    in = s.getInputStream();
                    out = s.getOutputStream();
                }
                shell.pipeStreams(out, out, in, s.getReceiveBufferSize(), true, false, 100L);
            }
            catch(Throwable t)
            {
                t.printStackTrace();
                try
                {
                    t.printStackTrace(new PrintStream(out));
                }
                catch(Throwable t1)
                {
                    t1.printStackTrace();
                }
                try
                {
                    in.close();
                    out.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private Socket s;
        private ProcessHandler shell;
        private DesEncryption encryption;
        final NetShellDaemon this$0;

        public TelnetConnection(Socket s, ProcessHandler shell, DesEncryption encryption)
        {
            this$0 = NetShellDaemon.this;
            super();
            if(!shell.isStarted())
            {
                throw new IllegalArgumentException("The shell process must be already running!");
            } else
            {
                this.s = s;
                this.shell = shell;
                this.encryption = encryption;
                return;
            }
        }
    }


    public NetShellDaemon(int port, String arrShellInvocationCommand[], String passphrase)
        throws IOException
    {
        encryption = null;
        listener = new ServerSocket(port);
        this.arrShellInvocationCommand = arrShellInvocationCommand;
        if(passphrase != null)
            encryption = new DesEncryption(passphrase);
        listen();
    }

    public void listen()
    {
        do
        {
            ProcessHandler shell;
            if(arrShellInvocationCommand != null)
                shell = new ProcessHandler(arrShellInvocationCommand);
            else
                shell = new ProcessHandler();
            try
            {
                shell.execShellCommand(new String[] {
                    "cmd"
                });
                (new TelnetConnection(listener.accept(), shell, encryption)).start();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        } while(true);
    }

    public static void main(String args[])
    {
        try
        {
            new NetShellDaemon(9999, null, "password");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private ServerSocket listener;
    private String arrShellInvocationCommand[];
    private DesEncryption encryption;
}
