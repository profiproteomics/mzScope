/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.MetricsCache;
import fr.proline.mzscope.model.EmptyRawFile;
import fr.proline.mzscope.model.FeaturesExtractionRequest;
import fr.proline.mzscope.model.IChromatogram;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.model.IPeakel;
import fr.proline.mzscope.model.QCMetrics;
import fr.proline.mzscope.ui.MzScopePanel;
import fr.proline.mzscope.ui.QCMetricsPanel;
import fr.proline.mzscope.ui.RawFileManager;
import fr.proline.mzscope.ui.RawFilesPanel;
import fr.proline.mzscope.ui.dialog.MzdbFilter;
import fr.proline.mzscope.ui.event.ExtractionEvent;
import fr.proline.mzscope.ui.event.ExtractionStateListener;
import fr.proline.mzscope.utils.IPopupMenuDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * main contains which contains the rawFilePanel and the MzScope Panel
 *
 * @author MB243701
 */
public class RawMinerPanel extends JPanel implements ExtractionStateListener, IPopupMenuDelegate {

   private final static Logger logger = LoggerFactory.getLogger("ProlineStudio.mzScope.RawMinerPanel");
   private final static String LAST_DIR = "mzscope.last.raw.directory";
   private final static String LAST_PEAKEL_DIR = "mzscope.last.peakeldb.directory";
   

   private final RawMinerFrame parentFrame;
   private JFileChooser fileChooser = null;
   private JSplitPane mainSplitPane = null;
   private JSplitPane explorerSplitPane = null;
   private RawFilesPanel rawFilePanel = null;
   private MzScopePanel mzScopePanel = null;
   private JMenuItem detectPeakelsMI;
   private JMenuItem loadPeakelsMI;   
   private JMenuItem extractFeaturesMI;
   private JMenuItem compareQCMI;
   private JMenuItem detectFeaturesMI;
   private JMenuItem closeAllFileMI;
   private JMenuItem closeRawFileMI;
   private JMenuItem viewRawFileMI;
   private ActionListener viewRawFileAction;
   private JMenuItem propertiesMI;
   private JMenuItem viewAllRawFilesMI;
   private JMenuItem viewLCMSMap;
   private JMenuItem export;

   public RawMinerPanel(RawMinerFrame parentFrame) {
      super();
      this.parentFrame = parentFrame;
      initComponents();
   }

   private void initComponents() {
      setLayout(new BorderLayout());
      createMainSplitPane();
   }

   private void createMainSplitPane() {
      this.add(getMainSplitPane(), BorderLayout.CENTER);
   }

   private JSplitPane getMainSplitPane() {
      if (this.mainSplitPane == null) {
         this.mainSplitPane = new JSplitPane();
         this.mainSplitPane.setBorder(null);
         this.mainSplitPane.setDividerLocation(200);
         this.mainSplitPane.setOneTouchExpandable(true);
         this.mainSplitPane.setLeftComponent(getExplorerPanel());
         this.mainSplitPane.setRightComponent(getMzScopePanel());
      }
      return this.mainSplitPane;
   }

   private JComponent getExplorerPanel() {
      if (this.explorerSplitPane == null) {
         explorerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         explorerSplitPane.setBorder(null);
         explorerSplitPane.setDividerLocation(200);
         explorerSplitPane.setOneTouchExpandable(true);
         explorerSplitPane.setTopComponent(getRawFilesPanel());
      }
      return explorerSplitPane;
   }

   private RawFilesPanel getRawFilesPanel() {
      if (this.rawFilePanel == null) {
         this.rawFilePanel = new RawFilesPanel(this);
      }
      return this.rawFilePanel;
   }

   protected MzScopePanel getMzScopePanel() {
      if (this.mzScopePanel == null) {
         mzScopePanel = new MzScopePanel(parentFrame);
         mzScopePanel.addExtractionListener(this);
      }
      return mzScopePanel;
   }

   private JFileChooser getFileChooser() {
      if (this.fileChooser == null) {
         this.fileChooser = new JFileChooser();
         this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
         this.fileChooser.setDialogTitle("Open mzDB file");
         this.fileChooser.addChoosableFileFilter(new MzdbFilter());
      }
      return this.fileChooser;
   }

   public void addRawFile(IRawFile rawfile){
      rawFilePanel.addFile(rawfile);
   }

   public void closeRawFile(IRawFile rawfile) {
      mzScopePanel.closeRawFile(rawfile);
      rawFilePanel.removeFile(rawfile);
      if(! (rawfile instanceof  EmptyRawFile ))
         RawFileManager.getInstance().removeRawFile(rawfile);
   }

   public void closeAllFiles() {
      boolean close = mzScopePanel.closeAllRaw();
      if (close) {
         rawFilePanel.removeAllFiles();
         RawFileManager.getInstance().removeAllFiles();
      }
   }

   @Override
   public void extractionStateChanged(ExtractionEvent event) {
      parentFrame.setExtractFeaturesMIEnabled(event.getState() == ExtractionEvent.EXTRACTION_DONE);
      parentFrame.setDetectPeakelsMIEnabled(event.getState() == ExtractionEvent.EXTRACTION_DONE);
   }

   public void openRawFile() {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      String directory = prefs.get(LAST_DIR, getFileChooser().getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      fileChooser.setMultiSelectionEnabled(true);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         File[] files = fileChooser.getSelectedFiles();
         for (File file : files) {
            prefs.put(LAST_DIR, file.getParentFile().getAbsolutePath());
            IRawFile rawfile = RawFileManager.getInstance().addRawFile(file);
            this.rawFilePanel.addFile(rawfile);
         }
      } else {
         System.out.println("File access cancelled by user.");
      }
   }

   public void comparePeakeList(){
      List<IRawFile> selectedFiles = rawFilePanel.getSelectedValues();
      if(selectedFiles.size() != 2){
         JOptionPane.showMessageDialog(this, "Select 2, and only 2, files to compare","Compare peakel list error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      IRawFile r1 = selectedFiles.get(0);
      IRawFile r2 = selectedFiles.get(1);
      FeaturesExtractionRequest params =  FeaturesExtractionRequest.builder().build();

      List<IPeakel> listPeakel1 = r1.extractPeakels(params);
      List<IPeakel> listPeakel2 = r2.extractPeakels(params);
      Comparator<IPeakel> peakelComparator = new Comparator<IPeakel>() {
         @Override
         public int compare(IPeakel o1, IPeakel o2) {
            if(Math.abs(o1.getApexIntensity()-o2.getApexIntensity()) < 0.000001)
               return 0;
            if (o1.getApexIntensity()<o2.getApexIntensity())
               return -1;
            return 1;
         }
      };
      listPeakel1.sort(peakelComparator);
      listPeakel2.sort(peakelComparator);
      if(listPeakel1.size() != listPeakel2.size()) {
         String msg  ="Peakel Size differ !! "+r1.getName()+"has got "+listPeakel1.size()+" peakels \n and "+r2.getName()+" has got "+listPeakel2.size()+" detected peakels";
         JOptionPane.showMessageDialog(this, msg, "Compare peakels list error", JOptionPane.ERROR_MESSAGE);
      } else {
         boolean errorFound = false;
         int nbrErr = 0;
         for( int i =0;i<listPeakel1.size(); i++){
            if(!arePeakelEquals(listPeakel1.get(i), listPeakel2.get(i)) ){
               errorFound = true;
               nbrErr++;
            }
         }
         if(errorFound)
            JOptionPane.showMessageDialog(this, "Found "+nbrErr+" error(s) ", "Compare peakels list error", JOptionPane.ERROR_MESSAGE);
         else
            JOptionPane.showMessageDialog(this, "NO Error Found ", "Compare peakels list", JOptionPane.INFORMATION_MESSAGE);
      }
   }

   private boolean arePeakelEquals(IPeakel p1,IPeakel p2){
      boolean result = Math.abs(p1.getElutionTime() - p2.getElutionTime()) < 0.0001;
      result = result && Math.abs(p1.getMz() - p2.getMz()) < 0.0001;
      result = result && Math.abs(p1.getApexIntensity() - p2.getApexIntensity()) < 0.0001;
      return  result;
   }

   public void exportChromatogram() {
      if (mzScopePanel.getCurrentRawFileViewer() != null) {
         try {
            DecimalFormat df = new DecimalFormat("#.00");
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            IChromatogram currentChromatogram = mzScopePanel.getCurrentRawFileViewer().getCurrentChromatogram();
            StringBuilder stb = new StringBuilder();
            stb.append(prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath())).append('\\');
            stb.append("extracted_xic_").append(df.format(currentChromatogram.getMinMz())).append(".tsv");
            
            JFileChooser saveFileChooser = new JFileChooser();
            saveFileChooser.setSelectedFile(new File(stb.toString()));
            int returnVal = saveFileChooser.showSaveDialog(parentFrame);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              File file = saveFileChooser.getSelectedFile();
              BufferedWriter output = new BufferedWriter(new FileWriter(file));
              output.write("index\trt\tintensity\n");
              for (int k = 0; k < currentChromatogram.getTime().length; k++) {
                stb = new StringBuilder();
                stb.append(k).append("\t").append(currentChromatogram.getTime()[k]).append("\t").append(currentChromatogram.getIntensities()[k]);
                stb.append("\n");
                output.write(stb.toString());
              }
              logger.info("extracted IChromatogram in " + file.getAbsolutePath());
              output.close();
            }
         } catch (Exception e) {
            logger.error("Enable to write current IChromatogram", e);
         }
      }
   }

   @Override
   public void initPopupMenu(JPopupMenu popupMenu) {
      // view data
      viewRawFileAction = evt -> {
         List<IRawFile> rawFiles = getRawFilesPanel().getSelectedValues();
         if (rawFiles.size() == 1) {
            mzScopePanel.displayRaw(rawFiles.get(0), true);
         } else {
            mzScopePanel.displayRaw(rawFiles);
         }
      };
      viewRawFileMI = new JMenuItem();
      viewRawFileMI.setText("View");
      viewRawFileMI.addActionListener(viewRawFileAction);
      popupMenu.add(viewRawFileMI);
      
      // close raw file
      viewAllRawFilesMI = new JMenuItem();
      viewAllRawFilesMI.setText("View all");
      viewAllRawFilesMI.addActionListener(evt -> mzScopePanel.displayAllRaw());
      popupMenu.add(viewAllRawFilesMI);
      
      
      // close raw file
      closeRawFileMI = new JMenuItem();
      closeRawFileMI.setText("Close mzDB file");
      closeRawFileMI.addActionListener(evt -> closeRawFile(getRawFilesPanel().getSelectedValues().get(0)));
      popupMenu.add(closeRawFileMI);
      popupMenu.addSeparator();

      // close all files
      closeAllFileMI = new JMenuItem();
      closeAllFileMI.setText("Close All mzDB files...");
      closeAllFileMI.addActionListener((ActionEvent evt) -> closeAllFiles());
      popupMenu.add(closeAllFileMI);
      popupMenu.addSeparator();

      // extract Features
      extractFeaturesMI = new JMenuItem();
      extractFeaturesMI.setText("Extract Features from MS2...");
      extractFeaturesMI.addActionListener((ActionEvent evt) -> mzScopePanel.extractFeaturesFromMS2(getRawFilesPanel().getSelectedValues()));
      popupMenu.add(extractFeaturesMI);
      // detect Features
      detectFeaturesMI = new JMenuItem();
      detectFeaturesMI.setText("Detect Features...");
      detectFeaturesMI.addActionListener((ActionEvent evt) -> mzScopePanel.detectFeatures(getRawFilesPanel().getSelectedValues()));
      popupMenu.add(detectFeaturesMI);
      // detect peakels
      detectPeakelsMI = new JMenuItem();
      detectPeakelsMI.setText("Detect Peakels...");
      detectPeakelsMI.addActionListener((ActionEvent evt) -> mzScopePanel.detectPeakels(getRawFilesPanel().getSelectedValues()));
      popupMenu.add(detectPeakelsMI);
      
      // load peakels
      loadPeakelsMI = new JMenuItem();
      loadPeakelsMI.setText("Load Peakels...");
      loadPeakelsMI.addActionListener((ActionEvent evt) -> {
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            JFileChooser peakelChooser = new JFileChooser();
            peakelChooser.setDialogTitle("Load peakels");
            peakelChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".peakeldb") || f.getName().endsWith(".sqlite");
                }

                @Override
                public String getDescription() {
                    return ("*.peakeldb, *.sqlite");
                }
            });
            String directory = prefs.get(LAST_PEAKEL_DIR, peakelChooser.getCurrentDirectory().getAbsolutePath());
            peakelChooser.setCurrentDirectory(new File(directory));
            int returnVal = peakelChooser.showOpenDialog(mzScopePanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = peakelChooser.getSelectedFile();
                prefs.put(LAST_PEAKEL_DIR, file.getParentFile().getAbsolutePath());
                mzScopePanel.loadPeakels(getRawFilesPanel().getSelectedValues().get(0), file);
            }
        });
      popupMenu.add(loadPeakelsMI);

      
      // view LCMS Map
      viewLCMSMap = new JMenuItem();
      viewLCMSMap.setText("View LCMS Map...");
      viewLCMSMap.addActionListener(ae -> mzScopePanel.displayLCMSMap(getRawFilesPanel().getSelectedValues().get(0)));
      popupMenu.add(viewLCMSMap);
      
      compareQCMI = new JMenuItem();
      compareQCMI.setText("QC metrics");
      compareQCMI.addActionListener(evt -> {
         List<QCMetrics> metrics = new ArrayList<>();
         for (IRawFile file : getRawFilesPanel().getSelectedValues()) {
                 metrics.add(getFileMetrics(file));
         }
         final QCMetricsPanel panel = mzScopePanel.displayMetrics(metrics);
         JButton loadMetricsBtn = new JButton("#QC");
         loadMetricsBtn.setToolTipText("Load existing QC metrics from the internal cache");
         loadMetricsBtn.addActionListener(e -> loadMetricsFromCache(panel));
         panel.getToolBar().add(loadMetricsBtn);
      });
      popupMenu.add(compareQCMI);
      
      popupMenu.addSeparator();
      // export (as MGF or tsv)
      export = new JMenuItem();
      export.setText("Export...");
      export.addActionListener(ae -> mzScopePanel.export(getRawFilesPanel().getSelectedValues()));
      popupMenu.add(export);
      
      popupMenu.addSeparator();
      propertiesMI = new JMenuItem();
      propertiesMI.setText("Properties");
      propertiesMI.addActionListener(evt -> mzScopePanel.displayProperties(getRawFilesPanel().getSelectedValues()));
      popupMenu.add(propertiesMI);

   }

   @Override
   public void updatePopupMenu() {
      List<IRawFile> selectedRaws =  getRawFilesPanel().getSelectedValues();
      int nbS = selectedRaws.size();
      boolean noEmptyRawFile =true;
      for (IRawFile rf : selectedRaws){
         if (rf instanceof EmptyRawFile) {
            noEmptyRawFile = false;
            break;
         }
      }

      viewRawFileMI.setEnabled(noEmptyRawFile && nbS > 0);
      closeRawFileMI.setEnabled(noEmptyRawFile && nbS == 1);
      closeAllFileMI.setEnabled(noEmptyRawFile);
      viewAllRawFilesMI.setEnabled(noEmptyRawFile);
      loadPeakelsMI.setEnabled(noEmptyRawFile);
      detectFeaturesMI.setEnabled(noEmptyRawFile);
      compareQCMI.setEnabled(noEmptyRawFile);
      propertiesMI.setEnabled(noEmptyRawFile);
      extractFeaturesMI.setEnabled(noEmptyRawFile && nbS > 0);
      detectPeakelsMI.setEnabled((noEmptyRawFile && nbS > 0));
      viewLCMSMap.setEnabled(noEmptyRawFile && nbS == 1);
      export.setEnabled(noEmptyRawFile && nbS > 0);
   }

   @Override
   public ActionListener getDefaultAction() {
      return viewRawFileAction;
   }

   
    private void loadMetricsFromCache(QCMetricsPanel panel) {
        MetricsCache.getInstance();
        panel.addMetrics(MetricsCache.getInstance().loadAllQC(), "cache");
    }
                
    private QCMetrics getFileMetrics(IRawFile rawFile) {
        QCMetrics metrics = MetricsCache.getInstance().loadQC(rawFile);
        if (metrics == null) {
            logger.info("QC metrics not found for file {}, they will be computed now", this.getName());
            metrics = rawFile.getFileMetrics();
            MetricsCache.getInstance().writeQC(rawFile, metrics);
        } else {
            metrics.setRawFile(rawFile);
        }
        return metrics;
    }
}
