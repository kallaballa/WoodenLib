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

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class StandardFileFilter extends FileFilter {

  public static final int MATCH_END = 0;

  public static final int MATCH_START = 1;

  public static final int MATCH_WHOLE = 2;

  private String description;

  private String extension;

  private boolean allowDirecties;

  private int matchmode;

  public StandardFileFilter(String description, String extension) {
    this(description, extension, true, 0);
  }

  public StandardFileFilter(String description, String extension,
      boolean allowDirectories) {
    this(description, extension, allowDirectories, 0);
  }

  public StandardFileFilter(String description, String extension,
      boolean allowDirectories, int matchmode) {
    this.description = description;
    this.extension = extension.toLowerCase();
    this.allowDirecties = allowDirectories;
    this.matchmode = matchmode;
  }

  public StandardFileFilter(String description, String extension, int matchmode) {
    this(description, extension, true, matchmode);
  }

  @Override
  public boolean accept(File f) {
    if (this.allowDirecties && f.isDirectory())
      return true;
    switch (this.matchmode) {
    case 0: // '\0'
      return f.getName().toLowerCase().endsWith(this.extension);

    case 1: // '\001'
      return f.getName().toLowerCase().startsWith(this.extension);

    case 2: // '\002'
      return f.getName().equalsIgnoreCase(this.extension);
    }
    return false;
  }

  @Override
  public String getDescription() {
    return this.description;
  }
}
