package fr.profi.mzscope;

import java.io.File;

/**
 * Interface to be notified when a conversion is starting or ends
 */
public interface ConverterListener {

  void convertStart(File file2Convert);
  void convertEnd(File file2Convert, File convertedFile, boolean success);

}
