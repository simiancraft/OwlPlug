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

package com.owlplug.host;

import com.owlplug.host.util.LibraryLoader;

public class NativeHostJNI {

  private static final String LIB_NAME = "owlplug-host";
  private static final String LIB_VERSION = "0.1.2";
  private static final String LIB_ID = LIB_NAME + "-" + LIB_VERSION;
  
  private static boolean isNativeLibraryLoaded;
  
  static {
    isNativeLibraryLoaded = LibraryLoader.load(LIB_ID, NativeHostJNI.class, false);
    
  }
  
  public static boolean isNativeLibraryLoaded() {
    return isNativeLibraryLoaded;
  }
  
  public native NativePlugin loadPlugin(String path);
  
  
}
