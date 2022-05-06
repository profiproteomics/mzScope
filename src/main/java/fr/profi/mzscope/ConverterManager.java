package fr.profi.mzscope;

import fr.profi.mzscope.threading.queue.TaskManagerThread;
import fr.profi.mzscope.threading.task.ConvertRawFile2MzdbCallback;
import fr.profi.mzscope.threading.task.ConvertRawFile2MzdbTask;
import fr.profi.mzscope.ui.RawMinerPanel;
import fr.proline.mzscope.model.EmptyRawFile;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.ui.RawFileManager;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterManager extends Thread implements ConverterListener {

  private static List<File> m_rawFiles;
  private static  ConverterManager m_instance;
  private static File m_converterFile;
  protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass().toString());
  private RawMinerPanel rawpanel; //Use listener !!! TODO
  private Map<String, IRawFile> m_emptyRawFileByName;

  public static ConverterManager getInstance() {
    if(m_instance == null)
      m_instance = new ConverterManager();
    return m_instance;
  }

  private  ConverterManager(){
    m_rawFiles = new ArrayList<>();
    m_emptyRawFileByName = new HashMap<>();
  }


  public static void setConverterExeFile(File exeFile){
    m_converterFile =exeFile;
  }

  public void setRawMinerPanel(RawMinerPanel panel){
    rawpanel = panel;
  }

  public static File getConverterExeFile(){
    return m_converterFile;
  }

  public void addFiles(List<File> newFiles){
    m_rawFiles.addAll(newFiles);
    if(rawpanel != null) {
      for(File file2Convert : newFiles) {
        IRawFile eRf = new EmptyRawFile(file2Convert.getName());
        m_emptyRawFileByName.put(file2Convert.getName(), eRf);
        rawpanel.addRawFile(eRf);
      }
    }
  }

  public File getAndRemoveNextFile(){
    if(!m_rawFiles.isEmpty())
      return m_rawFiles.remove(0);
    return null;
  }

  public boolean fileToConvertExist(){
      return (!m_rawFiles.isEmpty());
  }

  public void runConvert() {
    if (fileToConvertExist()) {
      logger.debug("CM: Add Tasks");
      File f = getAndRemoveNextFile();
      while(f != null) {
        ConvertRawFile2MzdbCallback callback = new ConvertRawFile2MzdbCallback();
        callback.setListener(this);
        ConvertRawFile2MzdbTask task = new ConvertRawFile2MzdbTask(callback, getAndRemoveNextFile());
        task.setListener(this);
        TaskManagerThread.getTaskManagerThread().addTask(task);
        f = getAndRemoveNextFile();
      }
    }
  }

  @Override
  public void convertStart(File file2Convert) {
//    if(file2Convert !=null && rawpanel != null) {
//      IRawFile eRf = new EmptyRawFile(file2Convert.getName());
//      m_emptyRawFileByName.put(file2Convert.getName(), eRf );
//      rawpanel.addRawFile(eRf);
//    }
  }

  @Override
  public void convertEnd(File file2Convert, File convertedFile, boolean success) {
      if(file2Convert != null) {
        IRawFile eRf = m_emptyRawFileByName.get(file2Convert.getName());
        if (eRf != null)
          rawpanel.closeRawFile(eRf);
      }
      if (success) {
          IRawFile rawfile = RawFileManager.getInstance().addRawFile(convertedFile);
          rawpanel.addRawFile(rawfile);
      }
//      ConverterManager.getInstance().runConvert();
  }
}
