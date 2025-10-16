package com.example.comp2100miniproject.persistentdata.io;

import android.content.Context;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Android-aware implementation of {@link IOFactory} that stores files inside the app's internal
 * storage. Author: u7283652
 */
public class AndroidIOFactory implements IOFactory {
  private static final String DIRECTORY_NAME = "saved-data";
  private static final String EXTENSION = ".txt";

  private final File baseDirectory;

  public AndroidIOFactory(Context context) {
    baseDirectory = new File(context.getFilesDir(), DIRECTORY_NAME);
    if (!baseDirectory.exists()) {
      baseDirectory.mkdirs();
    }
  }

  private File resolve(String filename) {
    return new File(baseDirectory, filename + EXTENSION);
  }

  @Override
  public Writer writer(String filename) {
    try {
      return new FileWriter(resolve(filename));
    } catch (IOException ignored) {
      return null;
    }
  }

  @Override
  public Reader reader(String filename) {
    File target = resolve(filename);
    if (!target.exists()) {
      return null;
    }
    try {
      return new FileReader(target);
    } catch (IOException ignored) {
      return null;
    }
  }
}
