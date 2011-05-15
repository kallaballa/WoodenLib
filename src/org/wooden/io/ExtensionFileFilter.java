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

public class ExtensionFileFilter extends FileFilter implements
    java.io.FileFilter {

  private String extension;

  private boolean allowDirectories;

  private String description;

  public ExtensionFileFilter(String extension, boolean allowDirectories) {
    this(extension, (new StringBuilder(String.valueOf(extension))).append(
        " Files").toString(), allowDirectories);
  }

  public ExtensionFileFilter(String extension, String description,
      boolean allowDirectories) {
    this.extension = extension;
    this.allowDirectories = allowDirectories;
    this.description = description;
  }

  @Override
  public boolean accept(File f) {
    if (!this.allowDirectories && f.isDirectory())
      return false;
    else
      return f.getName().endsWith(this.extension);
  }

  public boolean accept(String path) {
    return this.accept(new File(path));
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  public String getExtension() {
    return this.extension;
  }
}
