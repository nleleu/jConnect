package com.jconnect.util;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class FilePreferencesFactory implements PreferencesFactory
{
  private static final Logger log = Logger.getLogger(FilePreferencesFactory.class.getName());
 
  Preferences rootPreferences;
  public static final String SYSTEM_PROPERTY_FILE =
    "com.jconnect.util.prefs.FilePreferencesFactory.file";
 
  public Preferences systemRoot()
  {
    return userRoot();
  }
 
  public Preferences userRoot()
  {
    if (rootPreferences == null) {
      log.finer("Instantiating root preferences");
 
      rootPreferences = new FilePreferences(null, "");
    }
    return rootPreferences;
  }
 
  private static File preferencesFile;
 
  public static File getPreferencesFile()
  {
    if (preferencesFile == null) {
      String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);
      if (prefsFile == null || prefsFile.length() == 0) {
        prefsFile = System.getProperty("user.home") + File.separator + ".fileprefs";
      }
      preferencesFile = new File(prefsFile).getAbsoluteFile();
      log.finer("Preferences file is " + preferencesFile);
    }
    return preferencesFile;
  }
 
}
