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

import java.io.*;
import org.apache.commons.net.bsd.RExecClient;

public class RexecProcess extends Thread
{

    public RexecProcess(String host, int port)
        throws IOException
    {
        process = new RExecClient();
        this.host = host;
        this.port = port;
    }

    public void exec(String username, String password, String command)
        throws IOException
    {
        if(process.isConnected())
        {
            throw new IllegalStateException("RexecProcess is already connected");
        } else
        {
            process.connect(host, port);
            process.rexec(username, password, command, true);
            return;
        }
    }

    public void terminate()
        throws IOException
    {
        if(!process.isConnected())
        {
            throw new IllegalStateException("RexecProcess is not connected");
        } else
        {
            process.disconnect();
            return;
        }
    }

    public InputStream getErrorStream()
    {
        return process.getErrorStream();
    }

    public InputStream getInputStream()
    {
        return process.getInputStream();
    }

    public OutputStream getOutputStream()
    {
        return process.getOutputStream();
    }

    private RExecClient process;
    private String host;
    private int port;
}
