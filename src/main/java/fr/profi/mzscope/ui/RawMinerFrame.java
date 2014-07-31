/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzdb.model.Feature;
import fr.profi.mzscope.model.Chromatogram;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class RawMinerFrame extends javax.swing.JFrame {

   final private static Logger logger = LoggerFactory.getLogger(RawMinerFrame.class);
   final private static String LAST_DIR = "Last directory";

   private IRawFilePlot selectedRawFilePanel;

   /**
    * Creates new form RawMinerFrame
    */
   public RawMinerFrame() {
      initComponents();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      fileChooser = new javax.swing.JFileChooser();
      jSplitPane1 = new javax.swing.JSplitPane();
      jSplitPane2 = new javax.swing.JSplitPane();
      rawFilesPanel = new javax.swing.JPanel();
      featuresTabPane = new javax.swing.JTabbedPane();
      jSplitPane3 = new javax.swing.JSplitPane();
      jSplitPane4 = new javax.swing.JSplitPane();
      propertySheetPanel = new fr.profi.mzscope.ui.ObjectInspectorPanel(Feature.class);
      extractXICPanel = new fr.profi.mzscope.ui.XICExtractionPanel();
      viewersTabPane = new javax.swing.JTabbedPane();
      menuBar = new javax.swing.JMenuBar();
      FileMenu = new javax.swing.JMenu();
      openRawMI = new javax.swing.JMenuItem();
      jSeparator1 = new javax.swing.JPopupMenu.Separator();
      closeAllMI = new javax.swing.JMenuItem();
      jSeparator2 = new javax.swing.JPopupMenu.Separator();
      exitMI = new javax.swing.JMenuItem();
      ProcessMenu = new javax.swing.JMenu();
      extractFeaturesMI = new javax.swing.JMenuItem();
      exportChromatogram = new javax.swing.JMenuItem();

      fileChooser.setDialogTitle("Open Raw file");
      fileChooser.addChoosableFileFilter(new MzdbFilter());

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setTitle("Raw Miner");

      jSplitPane1.setBorder(null);
      jSplitPane1.setDividerLocation(200);
      jSplitPane1.setOneTouchExpandable(true);

      jSplitPane2.setDividerLocation(200);
      jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

      javax.swing.GroupLayout rawFilesPanelLayout = new javax.swing.GroupLayout(rawFilesPanel);
      rawFilesPanel.setLayout(rawFilesPanelLayout);
      rawFilesPanelLayout.setHorizontalGroup(
         rawFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 198, Short.MAX_VALUE)
      );
      rawFilesPanelLayout.setVerticalGroup(
         rawFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 0, Short.MAX_VALUE)
      );

      jSplitPane2.setTopComponent(rawFilesPanel);
      jSplitPane2.setRightComponent(featuresTabPane);

      jSplitPane1.setLeftComponent(jSplitPane2);

      jSplitPane3.setDividerLocation(320);
      jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

      jSplitPane4.setDividerLocation(370);
      jSplitPane4.setRightComponent(propertySheetPanel);
      jSplitPane4.setLeftComponent(extractXICPanel);

      jSplitPane3.setRightComponent(jSplitPane4);

      viewersTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            viewersTabPaneStateChanged(evt);
         }
      });
      jSplitPane3.setLeftComponent(viewersTabPane);

      jSplitPane1.setRightComponent(jSplitPane3);

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

      extractFeaturesMI.setText("Extract features");
      extractFeaturesMI.setActionCommand("extractMenuItem");
      extractFeaturesMI.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            extractFeaturesMIActionPerformed(evt);
         }
      });
      ProcessMenu.add(extractFeaturesMI);

      exportChromatogram.setText("Export chromatogram");
      exportChromatogram.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            exportChromatogramActionPerformed(evt);
         }
      });
      ProcessMenu.add(exportChromatogram);
      exportChromatogram.getAccessibleContext().setAccessibleName("exportChromatogramItem");

      menuBar.add(ProcessMenu);

      setJMenuBar(menuBar);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
       System.exit(0);
    }//GEN-LAST:event_exitMIActionPerformed

    private void openRawMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRawMIActionPerformed
       Preferences prefs = Preferences.userNodeForPackage(this.getClass());
       String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
       fileChooser.setCurrentDirectory(new File(directory));
       int returnVal = fileChooser.showOpenDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          prefs.put(LAST_DIR, file.getParentFile().getAbsolutePath());
          IRawFile rawfile = RawFileManager.getInstance().addRawFile(file);
          RawFilePlotPanel plotPanel = new RawFilePlotPanel(rawfile);
          viewersTabPane.add(file.getName(), plotPanel);
          FeaturesPanel featuresPanel = new FeaturesPanel(plotPanel);
          featuresTabPane.add(file.getName(), featuresPanel);
       } else {
          System.out.println("File access cancelled by user.");
       }
    }//GEN-LAST:event_openRawMIActionPerformed

    private void extractFeaturesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractFeaturesMIActionPerformed
       if ((selectedRawFilePanel != null) && (viewersTabPane.getSelectedIndex() >= 0)) {
          final String tabName = viewersTabPane.getTitleAt(viewersTabPane.getSelectedIndex());
          final IRawFile rawFile = selectedRawFilePanel.getRawfile();
          extractFeaturesMI.setEnabled(false);
          SwingWorker worker = new SwingWorker<List<Feature>, Void>() {
             @Override
             protected List<Feature> doInBackground() throws Exception {
                return rawFile.extractFeatures();
             }

             @Override
             protected void done() {
                logger.info("extraction done");
                try {
                   for (int i = 0; i < featuresTabPane.getComponentCount(); i++) {
                      if (featuresTabPane.getTitleAt(i).equals(tabName)) {
                         FeaturesPanel featurePanel = (FeaturesPanel) featuresTabPane.getComponentAt(i);
                         featurePanel.setFeatures(get());
                         break;
                      }
                   }
                   extractFeaturesMI.setEnabled(false);
                } catch (Exception e) {
                   logger.error("Error while reading chromatogram");
                }
             }
          };
          worker.execute();
          logger.debug("Feature extraction running ... ");
       }
    }//GEN-LAST:event_extractFeaturesMIActionPerformed

   private void exportChromatogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportChromatogramActionPerformed
      try {
         DecimalFormat df = new DecimalFormat("#.00");
         Preferences prefs = Preferences.userNodeForPackage(this.getClass());
         Chromatogram currentChromatogram = selectedRawFilePanel.getCurrentChromatogram();
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
   }//GEN-LAST:event_exportChromatogramActionPerformed

   private void viewersTabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_viewersTabPaneStateChanged
      selectedRawFilePanel = (IRawFilePlot) viewersTabPane.getSelectedComponent();
      extractXICPanel.setRawFile(selectedRawFilePanel.getRawfile());
   }//GEN-LAST:event_viewersTabPaneStateChanged

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
            logger.info("L&F = " + info.getName());
            if ("Windows".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(RawMinerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(RawMinerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(RawMinerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(RawMinerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            new RawMinerFrame().setVisible(true);
         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JMenu FileMenu;
   private javax.swing.JMenu ProcessMenu;
   private javax.swing.JMenuItem closeAllMI;
   private javax.swing.JMenuItem exitMI;
   private javax.swing.JMenuItem exportChromatogram;
   private javax.swing.JMenuItem extractFeaturesMI;
   private fr.profi.mzscope.ui.XICExtractionPanel extractXICPanel;
   private javax.swing.JTabbedPane featuresTabPane;
   private javax.swing.JFileChooser fileChooser;
   private javax.swing.JPopupMenu.Separator jSeparator1;
   private javax.swing.JPopupMenu.Separator jSeparator2;
   private javax.swing.JSplitPane jSplitPane1;
   private javax.swing.JSplitPane jSplitPane2;
   private javax.swing.JSplitPane jSplitPane3;
   private javax.swing.JSplitPane jSplitPane4;
   private javax.swing.JMenuBar menuBar;
   private javax.swing.JMenuItem openRawMI;
   private fr.profi.mzscope.ui.ObjectInspectorPanel propertySheetPanel;
   private javax.swing.JPanel rawFilesPanel;
   private javax.swing.JTabbedPane viewersTabPane;
   // End of variables declaration//GEN-END:variables

}

class MzdbFilter extends javax.swing.filechooser.FileFilter {

   @Override
   public boolean accept(File file) {
      // Allow only directories, or files with ".txt" extension
      return file.isDirectory() || file.getAbsolutePath().endsWith(".mzdb");
   }

   @Override
   public String getDescription() {
      // This description will be displayed in the dialog,
      // hard-coded = ugly, should be done via I18N
      return "Mzdb file (*.mzdb)";
   }
}
