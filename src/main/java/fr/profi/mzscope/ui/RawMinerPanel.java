/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.proline.mzscope.model.Chromatogram;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.ui.ExtractionResultsPanel;
import fr.proline.mzscope.ui.MzScopePanel;
import fr.proline.mzscope.ui.dialog.MzdbFilter;
import fr.proline.mzscope.ui.RawFileManager;
import fr.proline.mzscope.ui.RawFilesPanel;
import fr.proline.mzscope.ui.event.ExtractionEvent;
import fr.proline.mzscope.ui.event.ExtractionStateListener;
import fr.proline.mzscope.utils.IPopupMenuDelegate;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main contains which contains the rawFilePanel and the MzScope Panel
 *
 * @author MB243701
 */
public class RawMinerPanel extends JPanel implements ExtractionStateListener, IPopupMenuDelegate {

   private final static Logger logger = LoggerFactory.getLogger("ProlineStudio.mzScope.RawMinerPanel");
   private final static String LAST_DIR = "Last directory";

   private final RawMinerFrame parentFrame;
   private JFileChooser fileChooser = null;
   private JSplitPane mainSplitPane = null;
   private JSplitPane explorerSplitPane = null;
   private RawFilesPanel rawFilePanel = null;
   private MzScopePanel mzScopePanel = null;
   private JMenuItem detectPeakelsMI;
   private JMenuItem extractFeaturesMI;
   private JMenuItem closeAllFileMI;
   private JMenuItem closeRawFileMI;
   private JMenuItem viewRawFileMI;
   private ActionListener viewRawFileAction;
   private JMenuItem viewAllRawFilesMI;
   private JMenuItem viewLCMSMap;

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
         explorerSplitPane.setBottomComponent(new ExtractionResultsPanel(ExtractionResultsPanel.TOOLBAR_ALIGN_HORIZONTAL));
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
         mzScopePanel = new MzScopePanel(parentFrame, false);
         mzScopePanel.addExtractionListener(this);
      }
      return mzScopePanel;
   }

   private JFileChooser getFileChooser() {
      if (this.fileChooser == null) {
         this.fileChooser = new JFileChooser();
         this.fileChooser.setDialogTitle("Open Raw file");
         this.fileChooser.addChoosableFileFilter(new MzdbFilter());
      }
      return this.fileChooser;
   }

   public void closeRawFile(IRawFile rawfile) {
      mzScopePanel.closeRawFile(rawfile);
      rawFilePanel.removeFile(rawfile);
   }

   public void closeAllFiles() {
      boolean close = mzScopePanel.closeAllRaw();
      if (close) {
         rawFilePanel.removeAllFiles();
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

   public void exportChromatogram() {
      try {
         DecimalFormat df = new DecimalFormat("#.00");
         Preferences prefs = Preferences.userNodeForPackage(this.getClass());
         Chromatogram currentChromatogram = mzScopePanel.getCurrentChromatogram();
         StringBuilder stb = new StringBuilder();
         stb.append(prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath())).append('/');
         stb.append("extracted_xic_").append(df.format(currentChromatogram.minMz)).append(".tsv");
         File file = new File(stb.toString());
         BufferedWriter output = new BufferedWriter(new FileWriter(file));
         output.write("index\trt\tintensity\n");
         for (int k = 0; k < currentChromatogram.time.length; k++) {
            stb = new StringBuilder();
            stb.append(k).append("\t").append(currentChromatogram.time[k]).append("\t").append(currentChromatogram.intensities[k]);
            stb.append("\n");
            output.write(stb.toString());
         }
         logger.info("extracted Chromatogram in " + file.getAbsolutePath());
         output.close();
      } catch (Exception e) {
         logger.error("Enable to write current Chromatogram", e);
      }
   }

   @Override
   public void initPopupMenu(JPopupMenu popupMenu) {
      // view data
      viewRawFileAction = new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            List<IRawFile> rawFiles = getRawFilesPanel().getSelectedValues();
            if (rawFiles.size() == 1) {
               mzScopePanel.displayRawAction(rawFiles.get(0), true);
            } else {
               mzScopePanel.displayRawAction(rawFiles);               
            }
         }
      };
      viewRawFileMI = new JMenuItem();
      viewRawFileMI.setText("View");
      viewRawFileMI.addActionListener(viewRawFileAction);
      popupMenu.add(viewRawFileMI);
      
      // close raw file
      viewAllRawFilesMI = new JMenuItem();
      viewAllRawFilesMI.setText("View all");
      viewAllRawFilesMI.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            mzScopePanel.displayAllRawAction();
         }
      });
      popupMenu.add(viewAllRawFilesMI);
      
      
      // close raw file
      closeRawFileMI = new JMenuItem();
      closeRawFileMI.setText("Close Rawfile");
      closeRawFileMI.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            closeRawFile(getRawFilesPanel().getSelectedValues().get(0));
         }
      });
      popupMenu.add(closeRawFileMI);
      popupMenu.addSeparator();

      // close all files
      closeAllFileMI = new JMenuItem();
      closeAllFileMI.setText("Close All Rawfile...");
      closeAllFileMI.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            closeAllFiles();
         }
      });
      popupMenu.add(closeAllFileMI);
      popupMenu.addSeparator();

      // extract Features
      extractFeaturesMI = new JMenuItem();
      extractFeaturesMI.setText("Extract Features...");
      extractFeaturesMI.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            mzScopePanel.extractFeatures(getRawFilesPanel().getSelectedValues());
         }
      });
      popupMenu.add(extractFeaturesMI);
      // detect peakels
      detectPeakelsMI = new JMenuItem();
      detectPeakelsMI.setText("Detect Peakels...");
      detectPeakelsMI.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            mzScopePanel.detectPeakels(getRawFilesPanel().getSelectedValues());
         }
      });
      popupMenu.add(detectPeakelsMI);
      
      // view LCMS Map
      viewLCMSMap = new JMenuItem();
      viewLCMSMap.setText("View LCMS Map...");
      viewLCMSMap.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent ae) {
              mzScopePanel.displayLCMSMap(getRawFilesPanel().getSelectedValues().get(0));
          }
      });
      popupMenu.add(viewLCMSMap);

   }

   @Override
   public void updatePopupMenu() {
      int nbS = getRawFilesPanel().getSelectedValues().size();
      viewRawFileMI.setEnabled(nbS > 0);
      closeRawFileMI.setEnabled(nbS == 1);
      closeAllFileMI.setEnabled(true);
      viewAllRawFilesMI.setEnabled(true);
      extractFeaturesMI.setEnabled(nbS > 0);
      detectPeakelsMI.setEnabled((nbS > 0));
      viewLCMSMap.setEnabled(nbS == 1);
   }

   @Override
   public ActionListener getDefaultAction() {
      return viewRawFileAction;
   }
   
   
}
