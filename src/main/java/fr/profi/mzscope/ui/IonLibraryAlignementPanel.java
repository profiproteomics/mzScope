/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.opencsv.CSVReader;
import com.opencsv.bean.MappingStrategy;
import fr.profi.mzscope.Aligner;
import fr.profi.mzscope.CalibrationIon;
import fr.profi.mzscope.IonEntry;
import fr.proline.studio.export.ExportButton;
import fr.proline.studio.filter.FilterButton;
import fr.proline.studio.graphics.BaseGraphicsPanel;
import fr.proline.studio.table.CompoundTableModel;
import fr.proline.studio.table.DecoratedMarkerTable;
import fr.proline.studio.table.DecoratedTable;
import fr.proline.studio.table.TablePopupMenu;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelListener;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class IonLibraryAlignementPanel extends javax.swing.JPanel {

   private final static String LAST_DIR = "Last directory";
   final private static Logger logger = LoggerFactory.getLogger(IonLibraryAlignementPanel.class);

   private final CompoundTableModel tableModel = new CompoundTableModel(new BeanTableModel<IonEntry>(IonEntry.class), true);
   private Aligner aligner;
   private BaseGraphicsPanel graphicsPanel;

   /**
    * Creates new form IonLibraryAlignementPanel
    */
   public IonLibraryAlignementPanel() {
      initComponents();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPanel1 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      libraryPathTF = new javax.swing.JTextField();
      loadLibraryBtn = new javax.swing.JButton();
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
      applyBtn = new javax.swing.JButton();
      jPanel4 = new javax.swing.JPanel();
      tableScrollPane = new javax.swing.JScrollPane();
      tableToolbar = new javax.swing.JToolBar();
      plotPanel = new javax.swing.JPanel();

      org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.jLabel1.text")); // NOI18N

      libraryPathTF.setText(org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.libraryPathTF.text")); // NOI18N

      org.openide.awt.Mnemonics.setLocalizedText(loadLibraryBtn, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.loadLibraryBtn.text")); // NOI18N
      loadLibraryBtn.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            loadLibraryBtnActionPerformed(evt);
         }
      });

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
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabel1)
               .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(referencePathTF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
               .addComponent(libraryPathTF, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addComponent(loadLibraryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(libraryEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addComponent(loadReferenceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(referenceEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addContainerGap())
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel1)
                  .addComponent(libraryPathTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(loadLibraryBtn))
               .addComponent(libraryEntriesJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
      jSplitPane1.setDividerSize(3);
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

      org.openide.awt.Mnemonics.setLocalizedText(applyBtn, org.openide.util.NbBundle.getMessage(IonLibraryAlignementPanel.class, "IonLibraryAlignementPanel.applyBtn.text")); // NOI18N
      applyBtn.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            applyBtnActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addGap(0, 312, Short.MAX_VALUE)
            .addComponent(alignBtn)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(applyBtn)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(saveLibraryBtn))
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(saveLibraryBtn)
            .addComponent(alignBtn)
            .addComponent(applyBtn))
      );

      tableToolbar.setFloatable(false);
      tableToolbar.setOrientation(javax.swing.SwingConstants.VERTICAL);
      tableToolbar.setRollover(true);

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addComponent(tableToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tableScrollPane))
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(tableToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
         .addComponent(tableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
      );

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
      aligner.align();
      aligner.predictRT();
      tableModel.fireTableDataChanged();
      graphicsPanel.setData(tableModel, null);

   }//GEN-LAST:event_alignBtnActionPerformed

   private void loadLibraryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadLibraryBtnActionPerformed
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open Library file");
      String directory = prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
      fileChooser.setCurrentDirectory(new File(directory));
      fileChooser.setMultiSelectionEnabled(true);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         try {
            File file = fileChooser.getSelectedFile();
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("q1")
                    .addColumn("q3")
                    .addColumn("rt_detected", CsvSchema.ColumnType.NUMBER)
                    .addColumn("protein_name")
                    .addColumn("isotype")
                    .addColumn("relative_intensity")
                    .addColumn("stripped_sequence")
                    .addColumn("modification_sequence")
                    .addColumn("prec_z")
                    .addColumn("frg_type")
                    .addColumn("frg_z")
                    .addColumn("frg_nr")
                    .addColumn("iRT", CsvSchema.ColumnType.NUMBER)
                    .addColumn("uniprot_id")
                    .addColumn("score")
                    .addColumn("decoy")
                    .addColumn("prec_y")
                    .addColumn("confidence")
                    .addColumn("shared")
                    .addColumn("n")
                    .addColumn("rank")
                    .addColumn("mods")
                    .addColumn("nterm")
                    .addColumn("cterm").build();
            schema = schema.withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);
            
            List<IonEntry> entries = new ArrayList<>();
            FileInputStream fis = new FileInputStream(file);
            BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
            CsvMapper mapper = new CsvMapper();

            MappingIterator<IonEntry> it = mapper.reader(schema).forType(IonEntry.class).readValues(fReader);
            while (it.hasNext()) {
               IonEntry row = it.next();
               entries.add(row);
            }
            aligner = new Aligner(entries);
            List<IonEntry> nrEntries = aligner.getNonRedondantEntries();
            libraryPathTF.setText(file.getAbsolutePath());
            libraryEntriesJL.setText("(" + entries.size() + " entries found, "+nrEntries.size()+" non redondant)");
            
            ((BeanTableModel<IonEntry>) tableModel.getBaseModel()).setData(nrEntries);
            graphicsPanel.setData(tableModel, null);
            
            prefs.put(LAST_DIR, file.getParent());
         } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            logger.error("Error while parsing CSV file", ex);
         }

      } else {
         System.out.println("File access cancelled by user.");
      }

   }//GEN-LAST:event_loadLibraryBtnActionPerformed

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
            FileWriter writer = new FileWriter(file);
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("q1")
                    .addColumn("q3")
                    .addColumn("rt_detected", CsvSchema.ColumnType.NUMBER)
                    .addColumn("protein_name")
                    .addColumn("isotype")
                    .addColumn("relative_intensity")
                    .addColumn("stripped_sequence")
                    .addColumn("modification_sequence")
                    .addColumn("prec_z")
                    .addColumn("frg_type")
                    .addColumn("frg_z")
                    .addColumn("frg_nr")
                    .addColumn("iRT", CsvSchema.ColumnType.NUMBER)
                    .addColumn("uniprot_id")
                    .addColumn("score")
                    .addColumn("decoy")
                    .addColumn("prec_y")
                    .addColumn("confidence")
                    .addColumn("shared")
                    .addColumn("n")
                    .addColumn("rank")
                    .addColumn("mods")
                    .addColumn("nterm")
                    .addColumn("cterm").build();
            schema = schema.withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);
            CsvMapper mapper = new CsvMapper();
            mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
            mapper.writer(schema).writeValues(file).writeAll(aligner.getEntries().toArray(new IonEntry[0]));
            writer.flush();
            writer.close();
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }//GEN-LAST:event_saveLibraryBtnActionPerformed

   private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
      // apply predictedRT to observedRT
      aligner.applyPredictedRT();
      tableModel.fireTableDataChanged();
      graphicsPanel.setData(tableModel, null);
   }//GEN-LAST:event_applyBtnActionPerformed

   private void initCustomComponents() {
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

      graphicsPanel = new BaseGraphicsPanel(false);
      graphicsPanel.setData(tableModel, null);

      plotPanel.setLayout(new BorderLayout());
      plotPanel.add(graphicsPanel, BorderLayout.CENTER);
   }

   public static void main(String[] args) {
      try {
         CsvSchema schema = CsvSchema.builder()
                 .addColumn("q1")
                 .addColumn("q3")
                 .addColumn("rt_detected", CsvSchema.ColumnType.NUMBER)
                 .addColumn("protein_name")
                 .addColumn("isotype")
                 .addColumn("relative_intensity")
                 .addColumn("stripped_sequence")
                 .addColumn("modification_sequence")
                 .addColumn("prec_z")
                 .addColumn("frg_type")
                 .addColumn("frg_z")
                 .addColumn("frg_nr")
                 .addColumn("iRT", CsvSchema.ColumnType.NUMBER)
                 .addColumn("uniprot_id")
                 .addColumn("score")
                 .addColumn("decoy")
                 .addColumn("prec_y")
                 .addColumn("confidence")
                 .addColumn("shared")
                 .addColumn("n")
                 .addColumn("rank")
                 .addColumn("mods")
                 .addColumn("nterm")
                 .addColumn("cterm").build();
         schema = schema.withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);
//         File file = new File("D:\\tmp\\RTCalibration\\TTOF2_01804_CBY.txt");
           File file = new File("D:\\tmp\\RTCalibration\\test.txt");
         List<IonEntry> entries = jacksonCSV(file, schema);
         IonEntry e = entries.get(0);
         System.out.println("e = " + e.getModification_sequence());
         
         CsvMapper mapper = new CsvMapper();
//            Set<String> columnNames = new HashSet<String>();
//            for (CsvSchema.Column column : schema) {
//               columnNames.add(column.getName());
//            }
//
//            SimpleBeanPropertyFilter csvReponseFilter = new SimpleBeanPropertyFilter.FilterExceptFilter(columnNames);
//            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("propertiesFilter", csvReponseFilter);
//
//         mapper.setFilterProvider(filterProvider);
//         mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector () {
//            @Override
//            public Object findFilterId(Annotated a) {
//               return "propertiesFilter";
//            }
//         });
         
         mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
         // setFeature or with Feature :: JsonGenerator.Feature.IGNORE_UNKNOWN
         
                 
         mapper.writer(schema).writeValues(new File("D:\\tmp\\RTCalibration\\test_out.txt")).writeAll(entries.toArray(new IonEntry[0]));
            
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static List<IonEntry> jacksonCSV(File file, CsvSchema schema) throws Exception {
      List<IonEntry> entries = new ArrayList<>();
      FileInputStream fis = new FileInputStream(file);
      BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
      CsvMapper mapper = new CsvMapper();
      schema = schema.withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);
      long lineProcessed = 0;
      MappingIterator<IonEntry> it = mapper.reader(schema).forType(IonEntry.class).readValues(fReader);
      while (it.hasNext()) {
         IonEntry row = it.next();
         lineProcessed++;
         entries.add(row);
         System.out.println("line " + lineProcessed);
      }
      return entries;
   }

   public static void openCSV(File file) throws Exception {
      FileInputStream fis = new FileInputStream(file);
      BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
      CSVReader reader = new CSVReader(fReader, '\t', '\'');
      MappingStrategy<IonEntry> mapper = IonEntry.getMapping();
      long lineProcessed = 0;
      String[] line = null;

      try {
         mapper.captureHeader(reader);
      } catch (Exception e) {
         throw new RuntimeException("Error capturing CSV header!", e);
      }

      try {
         while (null != (line = reader.readNext())) {
            lineProcessed++;
            System.out.println("line " + lineProcessed);
         }

      } catch (Exception e) {
         fis.close();
         throw new RuntimeException("Error parsing CSV line: " + lineProcessed + " values: " + line, e);

      }
      fis.close();
      System.out.println("Done");
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton alignBtn;
   private javax.swing.JButton applyBtn;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JSplitPane jSplitPane1;
   private javax.swing.JLabel libraryEntriesJL;
   private javax.swing.JTextField libraryPathTF;
   private javax.swing.JButton loadLibraryBtn;
   private javax.swing.JButton loadReferenceBtn;
   private javax.swing.JPanel plotPanel;
   private javax.swing.JLabel referenceEntriesJL;
   private javax.swing.JTextField referencePathTF;
   private javax.swing.JButton saveLibraryBtn;
   private javax.swing.JScrollPane tableScrollPane;
   private javax.swing.JToolBar tableToolbar;
   // End of variables declaration//GEN-END:variables
}
