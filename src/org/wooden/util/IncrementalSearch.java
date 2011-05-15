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

import java.util.Vector;

public class IncrementalSearch implements SearchImplementation {

  private Object data[];

  private Vector listeners;

  private SearchPattern pattern;

  private Vector tmpSearchResults;

  public IncrementalSearch() {
    this.data = null;
    this.listeners = new Vector();
    this.pattern = null;
    this.tmpSearchResults = new Vector();
  }

  public IncrementalSearch(Object data[]) {
    this.data = null;
    this.listeners = new Vector();
    this.pattern = null;
    this.tmpSearchResults = new Vector();
    this.data = data;
  }

  public synchronized void addSearchListener(SearchListener l) {
    this.listeners.add(l);
  }

  private synchronized void dispatchSearchResetEvent() {
    SearchListener arrListeners[] = (SearchListener[]) this.listeners
        .toArray(new SearchListener[0]);
    SearchEvent event = new SearchEvent(this, null, null);
    for (SearchListener arrListener : arrListeners)
      arrListener.searchReset(event);

  }

  private synchronized void dispatchSearchResultUpdatedEvent(
      SearchPattern pattern, SearchResultEntry result[]) {
    SearchListener arrListeners[] = (SearchListener[]) this.listeners
        .toArray(new SearchListener[0]);
    SearchEvent event = new SearchEvent(this, pattern, result);
    for (SearchListener arrListener : arrListeners)
      arrListener.searchResultUpdated(event);

  }

  public synchronized void removeSearchListener(SearchListener l) {
    this.listeners.remove(l);
  }

  public synchronized void setSearchData(Object data[]) {
    this.data = data;
  }

  public synchronized boolean updateSearch() {
    if (this.pattern == null)
      throw new IllegalStateException("No previous search was done");
    else
      return this.updateSearch(this.pattern);
  }

  public synchronized boolean updateSearch(SearchPattern pattern) {
    if (pattern == null) {
      this.dispatchSearchResetEvent();
      return false;
    }
    this.tmpSearchResults.clear();
    for (int i = 0; i < this.data.length; i++)
      if (pattern.matches(this.data[i].toString()))
        this.tmpSearchResults.add(new SearchResultEntry(this.data[i], i));

    this.dispatchSearchResultUpdatedEvent(pattern,
        (SearchResultEntry[]) this.tmpSearchResults
            .toArray(new SearchResultEntry[0]));
    return this.tmpSearchResults.size() > 0;
  }

  public synchronized boolean updateSearch(String strPattern) {
    if (strPattern == null || strPattern.trim().length() == 0)
      this.pattern = null;
    else if (this.pattern == null)
      this.pattern = new SearchPattern(strPattern, 1);
    else
      this.pattern.updatePattern(strPattern, 1);
    return this.updateSearch(this.pattern);
  }
}
