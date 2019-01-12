package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.PluginFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSXPluginCollector implements NativePluginCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private PluginFormat pluginFormat;

  public OSXPluginCollector(PluginFormat pluginFormat) {
    this.pluginFormat = pluginFormat;
  }

  @Override
  public List<File> collect(String path) {

    ArrayList<File> fileList = new ArrayList<File>();
    File dir = new File(path);

    if (dir.isDirectory()) {
      List<File> baseFiles = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.TRUE, TrueFileFilter.TRUE);

      for (File file : baseFiles) {

        if (pluginFormat == PluginFormat.VST2) {
          if (file.getAbsolutePath().endsWith(".vst")) {
            fileList.add(file);
          }
        } else if (pluginFormat == PluginFormat.VST3) {
          if (file.getAbsolutePath().endsWith(".vst3")) {
            fileList.add(file);
          }
        }
      }
    } else {
      log.error("Plugin root is not a directory");
    }
    return fileList;
  }

}
