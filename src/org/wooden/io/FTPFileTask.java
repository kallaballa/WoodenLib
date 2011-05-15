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

package org.wooden.io;

import java.io.IOException;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPFileTask extends FileTask
{

    public FTPFileTask(FTPFile rootdir, FTPFile remoteFiles[], FTPClient ftp)
        throws IOException
    {
        this(rootdir, remoteFiles, ftp, true);
    }

    public FTPFileTask(FTPFile rootdir, FTPFile remoteFiles[], FTPClient ftp, boolean resolve)
        throws IOException
    {
        super(0);
        size = 0L;
        state = -1;
        if(!rootdir.isDirectory())
            throw new IOException((new StringBuilder("not a directory: ")).append(rootdir).toString());
        this.remoteFiles = remoteFiles;
        this.ftp = ftp;
        root = rootdir;
        absolutepath = rootdir.getName();
        filename = extractFileName(root);
        if(resolve)
            resolve(remoteFiles);
    }

    protected FTPFileTask(FTPFile rootdir, FTPFile remoteFile, int task, FTPClient ftp)
        throws IOException
    {
        super(task);
        size = 0L;
        state = -1;
        if(!rootdir.isDirectory())
            throw new IOException((new StringBuilder("not a directory: ")).append(rootdir).toString());
        this.ftp = ftp;
        root = rootdir;
        absolutepath = remoteFile.getName();
        filename = extractFileName(remoteFile);
        if(task == 2)
            resolve(remoteFile);
        else
            setState(1);
        size = remoteFile.getSize();
    }

    public int getState()
    {
        return state;
    }

    public void setState(int s)
    {
        state = s;
    }

    public synchronized void resolve()
        throws IOException
    {
        resolve(remoteFiles);
    }

    private synchronized void resolve(FTPFile dir)
        throws IOException
    {
        setState(0);
        try
        {
            parseTasks(dir);
            setState(1);
        }
        catch(Exception ex)
        {
            setState(2);
        }
    }

    private void resolve(FTPFile files[])
        throws IOException
    {
        setState(0);
        try
        {
            parseTasks(files);
            setState(1);
        }
        catch(Exception ex)
        {
            setState(2);
        }
    }

    private FTPFile[] listFiles(String name)
        throws IOException
    {
        Vector tmp = new Vector();
        FTPFile arrFiles[];
        if(!name.endsWith("..") && !name.endsWith("../"))
        {
            arrFiles = ftp.listFiles(name);
            for(int i = 0; i < arrFiles.length; i++)
                if(!arrFiles[i].getName().equals(".."))
                    tmp.add(arrFiles[i]);

            arrFiles = (FTPFile[])tmp.toArray(new FTPFile[0]);
        } else
        {
            arrFiles = new FTPFile[0];
        }
        return arrFiles;
    }

    public FileTask[] createTasks(Object files[])
        throws IOException
    {
        FTPFileTask arrTasks[];
        if(files == null)
            return new FTPFileTask[0];
        arrTasks = new FTPFileTask[files.length];
        for(int i=0; i < files.length; i++)
        {
        FTPFile f = (FTPFile)files[i];
        if(!absolutepath.endsWith("/"))
        {
        	absolutepath = (new StringBuilder(String.valueOf(absolutepath))).append('/').toString();
        }
        
        String name = (new StringBuilder(String.valueOf(absolutepath))).append(f.getName()).toString();
        int t;
        if(f.isDirectory())
        {
            t = 2;
            name = (new StringBuilder(String.valueOf(name))).append("/").toString();
        } else
        {
            t = 1;
        }
        f.setName(name);
        arrTasks[i] = new FTPFileTask(root, f, t, ftp);
        arrTasks[i].waitForResolver();
        }
        return arrTasks;
    }

    public FileTask[] createTasks(Object file)
        throws IOException
    {
        FTPFile f = (FTPFile)file;
        if(f.isDirectory())
        {
            FTPFile arrFiles[] = listFiles(f.getName());
            if(arrFiles != null && arrFiles.length > 0)
                return createTasks(((Object []) (arrFiles)));
        }
        return new FTPFileTask[0];
    }

    public long getFileSize()
    {
        return size;
    }

    public String getAbsolutePath()
    {
        return absolutepath;
    }

    public String getRelativePath()
    {
        return getAbsolutePath();
    }

    public String getFileName()
    {
        return filename;
    }

    private String extractFileName(FTPFile f)
    {
        int t = getType();
        String n = f.getName();
        if(t == 2 || t == 0)
            return n.substring(n.substring(0, n.length() - 1).lastIndexOf('/') + 1);
        else
            return n.substring(n.lastIndexOf('/') + 1);
    }

    public boolean waitForResolver()
    {
        if(getState() != 0)
            break MISSING_BLOCK_LABEL_20;
        resolver.join();
        return true;
        InterruptedException interruptedexception;
        interruptedexception;
        return false;
    }

    public static final int RESOLVING = 0;
    public static final int FINISHED = 1;
    public static final int ABORTED = 2;
    private String absolutepath;
    private String filename;
    private FTPClient ftp;
    private FTPFile root;
    private long size;
    private Thread resolver;
    private FTPFile remoteFiles[];
    private int state;
}
