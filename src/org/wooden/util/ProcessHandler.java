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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import org.wooden.io.ByteStreamPipe;

public class ProcessHandler {

  private static final Runtime runtime = Runtime.getRuntime();

  public static String[] determineCommandInterpreter() {
    String osName = System.getProperty("os.name");
    String cmd[] = new String[2];
    if (osName.equals("Windows NT") || osName.equals("Windows XP")
        || osName.equals("Windows 2000")) {
      cmd[0] = "cmd.exe";
      cmd[1] = "/C";
    } else if (osName.equals("Windows 95") || osName.equals("Windows 98")
        || osName.equals("Windows ME")) {
      cmd[0] = "command.com";
      cmd[1] = "/C";
    } else {
      cmd[0] = "bash";
      cmd[1] = "-c";
    }
    return cmd;
  }

  public static void main(String args[]) {
    try {
      ProcessHandler ph = new ProcessHandler();
      ph.execShellCommand(new String[] { "dir" });
      ph.pipeStreams(System.out, System.err, null, 4096, true, true, 100L);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private String commandInterpreterInvocation[];

  private Process process;

  private InputStream in;

  private InputStream err;

  private OutputStream out;

  public ProcessHandler() {
    this(determineCommandInterpreter());
  }

  public ProcessHandler(String customCommandInterpreterInvocation[]) {
    this.commandInterpreterInvocation = customCommandInterpreterInvocation;
  }

  private void ensureOpen() {
    if (!this.isStarted())
      throw new IllegalStateException("Job was not started");
    else
      return;
  }

  public void exec(String arrCommand[]) throws IOException {
    StringBuffer sb = new StringBuffer();
    for (String element : arrCommand)
      sb.append(element).append(' ');

    System.out.println(sb);
    this.process = runtime.exec(arrCommand);
  }

  public void execShellCommand(String arrCommand[]) throws IOException {
    String arrShellCommand[] = new String[arrCommand.length
        + this.commandInterpreterInvocation.length];
    System.arraycopy(this.commandInterpreterInvocation, 0, arrShellCommand, 0,
        this.commandInterpreterInvocation.length);
    System.arraycopy(arrCommand, 0, arrShellCommand,
        this.commandInterpreterInvocation.length, arrCommand.length);
    StringBuffer sb = new StringBuffer();
    for (String element : arrShellCommand)
      sb.append(element).append(' ');

    System.out.println(sb);
    this.process = runtime.exec(arrShellCommand);
  }

  public InputStream getInputStream(boolean errorStream) throws IOException {
    this.ensureOpen();
    if (errorStream && this.err == null)
      this.err = this.process.getErrorStream();
    else if (this.in == null)
      this.in = this.process.getInputStream();
    return errorStream ? this.err : this.in;
  }

  public OutputStream getOutputStream() throws IOException {
    this.ensureOpen();
    if (this.out == null)
      this.out = this.process.getOutputStream();
    return this.out;
  }

  public boolean isRunning() {
    if (this.isStarted())
      try {
        this.process.exitValue();
      } catch (IllegalThreadStateException e) {
        return true;
      }
    return false;
  }

  public boolean isStarted() {
    return this.process != null;
  }

  public void join() {
    try {
      this.process.waitFor();
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }

  public void kill() {
    this.ensureOpen();
    try {
      if (this.out != null)
        this.out.close();
    } catch (IOException ioexception) {}
    try {
      if (this.in != null)
        this.in.close();
    } catch (IOException ioexception1) {}
    this.process.destroy();
  }

  public void pipeStreams(OutputStream oppositeIn, OutputStream oppositeErr,
      InputStream oppositeOut, int bufferSize, boolean waitFor,
      boolean autoCloseStreams, long autoFlushInterval) throws IOException,
      InterruptedException, InvocationTargetException {
    ByteStreamPipe pOut = null;
    ByteStreamPipe pIn = null;
    ByteStreamPipe pErr = null;
    if (oppositeIn != null)
      pIn = new ByteStreamPipe(this.getInputStream(false), oppositeIn,
          bufferSize, autoFlushInterval, autoCloseStreams);
    if (oppositeErr != null)
      pErr = new ByteStreamPipe(this.getInputStream(true), oppositeErr,
          bufferSize, autoFlushInterval, autoCloseStreams);
    if (oppositeOut != null)
      pOut = new ByteStreamPipe(oppositeOut, this.getOutputStream(),
          bufferSize, autoFlushInterval, autoCloseStreams);
    if (pIn != null) {
      if (waitFor)
        pIn.join();
      if (pIn.isAborted())
        throw pIn.abortionCause();
    }
    if (pErr != null) {
      if (waitFor)
        pErr.join();
      if (pErr.isAborted())
        throw pErr.abortionCause();
    }
    if (pOut != null) {
      if (waitFor)
        pOut.join();
      if (pOut.isAborted())
        throw pOut.abortionCause();
    }
  }

}
