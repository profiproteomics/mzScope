/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.InvalidMGFFormatException;
import fr.profi.mzscope.MGFReader;
import fr.profi.mzscope.MSMSSpectrum;
import fr.profi.mzscope.ionlibraries.IonEntry;
import fr.profi.mzscope.ionlibraries.IonLibrary;
import fr.profi.mzscope.ionlibraries.PeakViewEntry;
import fr.profi.mzscope.ionlibraries.SpectronautEntry;
import fr.profi.util.version.IVersion;
import fr.proline.mzscope.ui.IRawFileViewer;
import fr.proline.mzscope.ui.dialog.MzdbFilter;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

/**
 *
 * @author CB205360
 */
public class RawMinerFrame extends JFrame {
      
   private final static Logger logger = LoggerFactory.getLogger(RawMinerFrame.class);
   private final static String LAST_DIR = "Last ion lib directory";
   
   private final RawMinerPanel rawMinerPanel;
//    static {
//        try {
//           // System.load("D:\\DEV\\sqlite4java-win32-x64-1.0.392.dll");
//            System.load("C:\\DATA\\sqlite4java-win32-x64-1.0.392.dll");
//        } catch (UnsatisfiedLinkError e) {
//          System.err.println("Native code library failed to load.\n" + e);
//          System.exit(1);
//        }
//  }
   /**
    * Creates new form RawMinerFrame
    */
   public RawMinerFrame() {
      initComponents();
      ServiceLoader<IVersion> versionLoader = ServiceLoader.load(IVersion.class);
      Iterator<IVersion> iter = versionLoader.iterator();
      String version = "snapshot";
      while(iter.hasNext()) {
          IVersion v = iter.next();
          if (v.getModuleName().equalsIgnoreCase("mzScope")) {
            version = v.getVersion();
          }
      }
      setTitle("mzScope "+version);
      //rawFilesPanel1.setParentFrame(this);
      rawMinerPanel = new RawMinerPanel(this);
      mainPanel.add(rawMinerPanel, BorderLayout.CENTER);
      setSize(700,500);
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        mainPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        openRawMI = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        closeAllMI = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMI = new javax.swing.JMenuItem();
        ProcessMenu = new javax.swing.JMenu();
        extractFeaturesMI = new javax.swing.JMenuItem();
        detectPeakelsMI = new javax.swing.JMenuItem();
        detectFeatureMI = new javax.swing.JMenuItem();
        exportChromatogram = new javax.swing.JMenuItem();
        ToolsMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        loadPeakViewLibraryMI = new javax.swing.JMenuItem();
        loadSpectronautLibraryMI = new javax.swing.JMenuItem();
        loadMGFMI = new javax.swing.JMenuItem();

        fileChooser.setDialogTitle("Open Raw file");
        fileChooser.addChoosableFileFilter(new MzdbFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("mzScope");

        mainPanel.setLayout(new java.awt.BorderLayout());

        FileMenu.setText("File");

        openRawMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openRawMI.setText("Open Rawfile ...");
        openRawMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRawMIActionPerformed(evt);
            }
        });
        FileMenu.add(openRawMI);
        FileMenu.add(jSeparator1);

        closeAllMI.setText("Close All");
        closeAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllMIActionPerformed(evt);
            }
        });
        FileMenu.add(closeAllMI);
        FileMenu.add(jSeparator2);

        exitMI.setText("Exit");
        exitMI.setActionCommand("exit");
        exitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIActionPerformed(evt);
            }
        });
        FileMenu.add(exitMI);
        exitMI.getAccessibleContext().setAccessibleName("exitMenuItem");

        menuBar.add(FileMenu);

        ProcessMenu.setText("Process");

        extractFeaturesMI.setText("Extract features from MS2");
        extractFeaturesMI.setActionCommand("extractMenuItem");
        extractFeaturesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractFeaturesMIActionPerformed(evt);
            }
        });
        ProcessMenu.add(extractFeaturesMI);

        detectPeakelsMI.setText("Detect Peakels");
        detectPeakelsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectPeakelsMIActionPerformed(evt);
            }
        });
        ProcessMenu.add(detectPeakelsMI);

        detectFeatureMI.setText("Detect Features");
        detectFeatureMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectFeatureMIActionPerformed(evt);
            }
        });
        ProcessMenu.add(detectFeatureMI);

        exportChromatogram.setText("Export chromatogram");
        exportChromatogram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportChromatogramActionPerformed(evt);
            }
        });
        ProcessMenu.add(exportChromatogram);
        exportChromatogram.getAccessibleContext().setAccessibleName("exportChromatogramItem");

        menuBar.add(ProcessMenu);

        ToolsMenu.setText("Tools");

        jMenu1.setText("Load Ion Library");

        loadPeakViewLibraryMI.setText("Peakview...");
        loadPeakViewLibraryMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPeakViewLibraryMIActionPerformed(evt);
            }
        });
        jMenu1.add(loadPeakViewLibraryMI);

        loadSpectronautLibraryMI.setText("Proline/Spectronaut...");
        loadSpectronautLibraryMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSpectronautLibraryMIActionPerformed(evt);
            }
        });
        jMenu1.add(loadSpectronautLibraryMI);

        ToolsMenu.add(jMenu1);

        loadMGFMI.setText("Load MGF File ...");
        loadMGFMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMGFMIActionPerformed(evt);
            }
        });
        ToolsMenu.add(loadMGFMI);

        menuBar.add(ToolsMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
       rawMinerPanel.closeAllFiles();
        System.exit(0);
    }//GEN-LAST:event_exitMIActionPerformed

    private void openRawMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRawMIActionPerformed
       rawMinerPanel.openRawFile();
    }//GEN-LAST:event_openRawMIActionPerformed

        
    private void extractFeaturesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractFeaturesMIActionPerformed
        IRawFileViewer viewer = rawMinerPanel.getMzScopePanel().getCurrentRawFileViewer();
        if (viewer != null &&  viewer.getCurrentRawfile() != null) {
          rawMinerPanel.getMzScopePanel().extractFeaturesFromMS2(Arrays.asList(viewer.getCurrentRawfile()));
        }
    }//GEN-LAST:event_extractFeaturesMIActionPerformed

   private void exportChromatogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportChromatogramActionPerformed
      rawMinerPanel.exportChromatogram();
   }//GEN-LAST:event_exportChromatogramActionPerformed

   private void detectPeakelsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectPeakelsMIActionPerformed
      IRawFileViewer viewer = rawMinerPanel.getMzScopePanel().getCurrentRawFileViewer();
      if (viewer != null &&  viewer.getCurrentRawfile() != null) {
        rawMinerPanel.getMzScopePanel().detectPeakels(Arrays.asList(viewer.getCurrentRawfile()));
      }
   }//GEN-LAST:event_detectPeakelsMIActionPerformed

    private void closeAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllMIActionPerformed
      rawMinerPanel.closeAllFiles();
    }//GEN-LAST:event_closeAllMIActionPerformed

   private void detectFeatureMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectFeatureMIActionPerformed
     IRawFileViewer viewer = rawMinerPanel.getMzScopePanel().getCurrentRawFileViewer();
     if (viewer != null && viewer.getCurrentRawfile() != null) {
      rawMinerPanel.getMzScopePanel().detectFeatures(Arrays.asList(viewer.getCurrentRawfile()));
     }
   }//GEN-LAST:event_detectFeatureMIActionPerformed


   private void loadIonLibraryMI(String dialogTitle, IonEntry prototype) {                                                      
      
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle(dialogTitle);
      String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      fileChooser.setMultiSelectionEnabled(true);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            IonLibrary library = IonLibrary.fromFile(file, prototype);
            prefs.put(LAST_DIR, file.getParent());
            rawMinerPanel.getMzScopePanel().addFeatureTab("Ion Lib: "+file.getName(), new IonLibraryPanel(library, rawMinerPanel.getMzScopePanel()), "");
      } 
      
   }                                                     

   private void loadPeakViewLibraryMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPeakViewLibraryMIActionPerformed
      loadIonLibraryMI("Open PeakView Library file", new PeakViewEntry());      
   }//GEN-LAST:event_loadPeakViewLibraryMIActionPerformed

    private void loadMGFMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMGFMIActionPerformed
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open MGF file");
      String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      fileChooser.setMultiSelectionEnabled(true);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
          try {
              File file = fileChooser.getSelectedFile();
              MGFReader reader = new MGFReader();
              List<MSMSSpectrum> peakList = reader.read(file);
              rawMinerPanel.getMzScopePanel().getFeaturesTabPane().add("MGF file "+file.getName(), new MGFPanel(peakList, rawMinerPanel.getMzScopePanel()));
              prefs.put(LAST_DIR, file.getParent());
          } catch (InvalidMGFFormatException ex) {
              Exceptions.printStackTrace(ex);
          }
      } 
    }//GEN-LAST:event_loadMGFMIActionPerformed

    private void loadSpectronautLibraryMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSpectronautLibraryMIActionPerformed
        loadIonLibraryMI("Open Spectronaut Library file", new SpectronautEntry());
    }//GEN-LAST:event_loadSpectronautLibraryMIActionPerformed

    
   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      /* Set the Nimbus look and feel */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
       * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
       */
      try {
//         javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
            }
         }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(RawMinerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
       //</editor-fold>
       
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            new RawMinerFrame().setVisible(true);
         }
      });
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu ProcessMenu;
    private javax.swing.JMenu ToolsMenu;
    private javax.swing.JMenuItem closeAllMI;
    private javax.swing.JMenuItem detectFeatureMI;
    private javax.swing.JMenuItem detectPeakelsMI;
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JMenuItem exportChromatogram;
    private javax.swing.JMenuItem extractFeaturesMI;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem loadMGFMI;
    private javax.swing.JMenuItem loadPeakViewLibraryMI;
    private javax.swing.JMenuItem loadSpectronautLibraryMI;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openRawMI;
    // End of variables declaration//GEN-END:variables


    public void setExtractFeaturesMIEnabled(boolean extractFeatures){
        extractFeaturesMI.setEnabled(extractFeatures);
    }
    
    public void setDetectPeakelsMIEnabled(boolean detectPeakels){
        detectPeakelsMI.setEnabled(detectPeakels);
    }
}

