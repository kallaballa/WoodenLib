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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferTest
{

    public FileTransferTest()
    {
    }

    private static Object[] parseArguments(String args[])
        throws Exception
    {
        File f;
        File f1;
        Socket s;
        Boolean server = null;
        f = new File(args[0]);
        f1 = new File(args[1]);
        s = null;
        if(args.length == 4)
        {
            int port = Integer.parseInt(args[2]);
            server = new Boolean(args[3].trim().equalsIgnoreCase("true"));
            if(server.booleanValue())
                s = (new ServerSocket(port)).accept();
            else
                s = new Socket("localhost", port);
        }
        return (new Object[] {
            f, f1, s
        });
        Exception ex;
        ex;
        printUsage();
        throw ex;
    }

    private static void printUsage()
    {
        System.out.println("USAGE: FileTransferTest <SOURCE FILE> <DESTINATION FILE> (optional:) <PORT> <SERVER>");
        System.out.flush();
    }

    private static void copyStreamConservative(InputStream src, OutputStream dest, int bs)
        throws IOException
    {
        byte buffer[] = new byte[bs];
        BufferedInputStream in = new BufferedInputStream(src);
        BufferedOutputStream out = new BufferedOutputStream(dest);
        int len;
        while((len = in.read(buffer)) > -1) 
            out.write(buffer, 0, len);
        in.close();
        out.close();
    }

    private static void startReport()
        throws IOException
    {
        report = new PrintWriter(new FileWriter((new StringBuilder("D:\\report")).append(System.currentTimeMillis()).append(".txt").toString()));
    }

    private static void logSeries(int seriesNr)
    {
        report.println();
        report.println((new StringBuilder("TEST SERIES: ")).append(seriesNr).toString());
    }

    private static void logTestResult(double time, double len, int ms, int rs, int ws)
    {
        report.println(time + '\t' + ms + '\t' + rs + '\t' + ws);
        report.flush();
    }

    private static void endReport()
    {
        report.close();
    }

    public static void main(String args[])
    {
        try
        {
            Object parameters[] = parseArguments(args);
            File f = (File)parameters[0];
            File f1 = (File)parameters[1];
            Socket s = (Socket)parameters[2];
            double len = (double)f.length() / 1024D;
            System.out.println((new StringBuilder("Starting: ")).append(len).append(" kb").toString());
            startReport();
            for(int j = 1; j < 4; j++)
            {
                logSeries(j);
                int ms = 0x100000 * j;
                for(int i = 5; i < 11; i++)
                {
                    int rs = 1024 * i;
                    int ws = 1024 * i;
                    System.gc();
                    start = System.currentTimeMillis();
                    copyStreamConservative(new FileInputStream(f), new FileOutputStream(f1), rs);
                    end = System.currentTimeMillis();
                    time = end - start;
                    logTestResult(time, len, ms, rs, ws);
                }

            }

            endReport();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static double end;
    private static double time = -1D;
    private static double start;
    private static PrintWriter report;

}
