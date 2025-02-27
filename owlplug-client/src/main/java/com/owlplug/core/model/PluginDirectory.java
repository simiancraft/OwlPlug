/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.model;

import java.util.List;

public class PluginDirectory implements IDirectory {

  protected String name;
  protected String displayName;
  protected String path;
  protected List<Plugin> pluginList;

  public PluginDirectory() {

  }

  public PluginDirectory(String path) {
    this.path = path;
  }

  public PluginDirectory(String name, String path, List<Plugin> pluginList) {
    super();
    this.name = name;
    this.path = path;
    this.pluginList = pluginList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<Plugin> getPluginList() {
    return pluginList;
  }

  public void setPluginList(List<Plugin> pluginList) {
    this.pluginList = pluginList;
  }

  @Override
  public String toString() {
    if (displayName != null) {
      return displayName;
    }
    return name;

  }

  @Override
  public boolean isStale() {
    return false;
  }

}
