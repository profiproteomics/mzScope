/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.proline.studio.table.BeanTableModel;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fr.profi.mzscope.Aligner;
import fr.profi.mzscope.CalibrationIon;
import fr.profi.mzscope.IonEntry;
import fr.profi.mzscope.IonLibrary;
import fr.proline.studio.export.ExportButton;
import fr.proline.studio.filter.FilterButton;
import fr.proline.studio.graphics.BaseGraphicsPanel;
import fr.proline.studio.markerbar.MarkerContainerPanel;
import fr.proline.studio.extendedtablemodel.CompoundTableModel;
import fr.proline.studio.table.DecoratedMarkerTable;
import fr.proline.studio.table.TablePopupMenu;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelListener;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class IonLibraryAlignementPanel extends javax.swing.JPanel {

   private final static String LAST_DIR = "Last ion lib directory";
   final private static Logger logger = LoggerFactory.getLogger(IonLibraryAlignementPanel.class);

   private final CompoundTableModel tableModel = new CompoundTableModel(new BeanTableModel<IonEntry>(IonEntry.class), true);
   private Aligner aligner;
   private BaseGraphicsPanel graphicsPanel;
   private MarkerContainerPanel markerContainerPanel;
    private final IonLibrary library;

   /**
    * Creates new form IonLibraryAlignementPanel
    */
    public IonLibraryAlignementPanel(IonLibrary library) {
        this.library = library;
        initComponents();
        aligner = new Aligner(library);
        libraryEntriesJL.setText("(" + library.getEntries().size() + " entries found, " + library.getNonRedondantEntries().size() + " non redondant)");
        markerContainerPanel.setMaxLineNumber(library.getNonRedondantEntries().size());
        ((BeanTableModel<IonEntry>) tableModel.getBaseModel()).setData(library.getNonRedondantEntries());
        graphicsPanel.setData(tableModel, null);
    }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableToolbar = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        referencePathTF = new javax.swing.JTextField();
        loadReferenceBtn = new javax.swing.JButton();
        libraryEntriesJL = new javax.swing.JLabel();
        referenceEntriesJL = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        saveLibraryBtn = new javax.swing.JButton();
        alignBtn = new javax.swing.JButton();
        interpolationCbx = new javax.swing.JComboBox();
        tablePane = new javax.swing.JPanel();
        plotPanel = new javax.swing.JPanel();

        tableToolbar.setFloatable(false);
        tableToolbar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        tableToolbar.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.jLabel2.text")); // NOI18N

        referencePathTF.setText(org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.referencePathTF.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(loadReferenceBtn, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.loadReferenceBtn.text")); // NOI18N
        loadReferenceBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadReferenceBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(libraryEntriesJL, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.libraryEntriesJL.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(referenceEntriesJL, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.referenceEntriesJL.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(referencePathTF, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadReferenceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(referenceEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(libraryEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(libraryEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(referencePathTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loadReferenceBtn))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(referenceEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        org.openide.awt.Mnemonics.setLocalizedText(saveLibraryBtn, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.saveLibraryBtn.text")); // NOI18N
        saveLibraryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLibraryBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(alignBtn, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.alignBtn.text")); // NOI18N
        alignBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignBtnActionPerformed(evt);
            }
        });

        interpolationCbx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 246, Short.MAX_VALUE)
                .addComponent(interpolationCbx, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveLibraryBtn))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(saveLibraryBtn)
                .addComponent(alignBtn)
                .addComponent(interpolationCbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tablePane.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setTopComponent(jPanel2);

        javax.swing.GroupLayout plotPanelLayout = new javax.swing.GroupLayout(plotPanel);
        plotPanel.setLayout(plotPanelLayout);
        plotPanelLayout.setHorizontalGroup(
            plotPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 557, Short.MAX_VALUE)
        );
        plotPanelLayout.setVerticalGroup(
            plotPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(plotPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        initCustomComponents();
    }// </editor-fold>//GEN-END:initComponents

   private void alignBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alignBtnActionPerformed
      UnivariateInterpolator interpolator = (UnivariateInterpolator)interpolationCbx.getSelectedItem();
      aligner.align(interpolator);
      aligner.predictRT();
      tableModel.fireTableDataChanged();
      graphicsPanel.setData(tableModel, null);

   }//GEN-LAST:event_alignBtnActionPerformed

   private void loadReferenceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadReferenceBtnActionPerformed
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open Reference file");
      String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      fileChooser.setMultiSelectionEnabled(false);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         try {
            File file = fileChooser.getSelectedFile();
            logger.info("Start loading ion library from "+file.getAbsolutePath());
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("q1")
                    .addColumn("q3")
                    .addColumn("rt_observed", CsvSchema.ColumnType.NUMBER)
                    .addColumn("protein_name")
                    .addColumn("modification_sequence")
                    .addColumn("prec_z").build();

            List<CalibrationIon> entries = new ArrayList<>();
            FileInputStream fis = new FileInputStream(file);
            BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
            CsvMapper mapper = new CsvMapper();
            schema = schema.withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);
            MappingIterator<CalibrationIon> it = mapper.reader(schema).forType(CalibrationIon.class).readValues(fReader);
            while (it.hasNext()) {
               CalibrationIon row = it.next();
               entries.add(row);
            }
            int matchesCount = 0;
            for (CalibrationIon c : entries) {
               if (aligner.addCalibrationIon(c)) 
                  matchesCount++;
            }
            
            referencePathTF.setText(file.getAbsolutePath());
            referenceEntriesJL.setText("(" + entries.size() + " entries found, "+matchesCount+" matched in the library)");

            tableModel.fireTableDataChanged();
            graphicsPanel.setData(tableModel, null);
            prefs.put(LAST_DIR, file.getParent());
            logger.info("Ion Library loaded");
         } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
         }

      } else {
         System.out.println("File access cancelled by user.");
      }
   }//GEN-LAST:event_loadReferenceBtnActionPerformed

   private void saveLibraryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLibraryBtnActionPerformed
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Save Library file");
      String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      int returnVal = fileChooser.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         try {
            File file = fileChooser.getSelectedFile();
            aligner.applyPredictedRT();
            aligner.getLibrary().saveToFile(file);
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }//GEN-LAST:event_saveLibraryBtnActionPerformed

   private void initCustomComponents() {
      JScrollPane tableScrollPane = new JScrollPane();
      DecoratedMarkerTable table = new DecoratedMarkerTable() {

         @Override
         public TablePopupMenu initPopupMenu() {
            return new TablePopupMenu();
         }

         @Override
         public void prepostPopupMenu() {
         }
         
         @Override
         public void addTableModelListener(TableModelListener l) {
            getModel().addTableModelListener(l);
         }
      };
      table.setModel(tableModel);
      table.getColumnExt("RT_detected").setVisible(false);      
      table.getColumnExt("frg_nr").setVisible(false);
      table.getColumnExt("frg_z").setVisible(false);
      table.getColumnExt("n").setVisible(false);
      table.getColumnExt("isotype").setVisible(false);
      table.getColumnExt("rank").setVisible(false);
      table.getColumnExt("score").setVisible(false);
      table.getColumnExt("nterm").setVisible(false);
      table.getColumnExt("cterm").setVisible(false);
      tableScrollPane.setViewportView(table);
      table.setFillsViewportHeight(true);
      table.setViewport(tableScrollPane.getViewport());

      ExportButton exportButton = new ExportButton(tableModel, "Export", table);
      tableToolbar.add(exportButton);
      FilterButton filterButton = new FilterButton(tableModel) {

         @Override
         protected void filteringDone() {

         }

      };
      
      
      tableToolbar.add(filterButton);

      markerContainerPanel = new MarkerContainerPanel(tableScrollPane, table);
      tablePane.add(tableToolbar, BorderLayout.WEST);
      tablePane.add(markerContainerPanel, BorderLayout.CENTER);
      
      graphicsPanel = new BaseGraphicsPanel(false);
      graphicsPanel.setData(tableModel, null);

      plotPanel.setLayout(new BorderLayout());
      plotPanel.add(graphicsPanel, BorderLayout.CENTER);
      
      UnivariateInterpolator[] interpolationMethods = { new LinearInterpolator(){ 
         public String toString() {
            return "Multi-Linear";
      }}, new SplineInterpolator() {
         public String toString() {
            return "Spline";
      }}, new AkimaSplineInterpolator() {
                public String toString() {
            return "Akima";
      }}};
      interpolationCbx.setModel(new DefaultComboBoxModel<UnivariateInterpolator>(interpolationMethods));
      
   }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton alignBtn;
    private javax.swing.JComboBox interpolationCbx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel libraryEntriesJL;
    private javax.swing.JButton loadReferenceBtn;
    private javax.swing.JPanel plotPanel;
    private javax.swing.JLabel referenceEntriesJL;
    private javax.swing.JTextField referencePathTF;
    private javax.swing.JButton saveLibraryBtn;
    private javax.swing.JPanel tablePane;
    private javax.swing.JToolBar tableToolbar;
    // End of variables declaration//GEN-END:variables
}
