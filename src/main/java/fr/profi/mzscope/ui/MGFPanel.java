/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.proline.studio.table.BeanTableModel;
import fr.profi.mzscope.MSMSSpectrum;
import fr.profi.mzscope.Peak;
import fr.proline.mzscope.model.MsnExtractionRequest;
import fr.proline.mzscope.ui.model.MzScopePreferences;
import fr.proline.mzscope.ui.IMzScopeController;
import fr.proline.mzscope.ui.IRawFileViewer;
import fr.proline.mzscope.utils.MzScopeConstants;
import fr.proline.mzscope.utils.MzScopeConstants.DisplayMode;
import fr.proline.studio.export.ExportButton;
import fr.proline.studio.filter.FilterButton;
import fr.proline.studio.markerbar.MarkerContainerPanel;
import fr.proline.studio.extendedtablemodel.CompoundTableModel;
import fr.proline.studio.table.DecoratedMarkerTable;
import fr.proline.studio.table.TablePopupMenu;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class MGFPanel extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(MGFPanel.class);
    
    private final CompoundTableModel peaksTableModel = new CompoundTableModel(new BeanTableModel<Peak>(Peak.class), true);
    private final CompoundTableModel spectrumTableModel = new CompoundTableModel(new BeanTableModel<MSMSSpectrum>(MSMSSpectrum.class), true);
    
    private MarkerContainerPanel peaksMarkerContainerPanel;
    private MarkerContainerPanel spectrumMarkerContainerPanel;
    
    private final IMzScopeController appController;
    
    private DecoratedMarkerTable peaksTable;
    private DecoratedMarkerTable spectrumTable;
    
    /**
     * Creates new form IonLibraryPanel
     */
    public MGFPanel(List<MSMSSpectrum> spectrum, IMzScopeController appController) {
        this.appController = appController;
        initComponents();
        spectrumMarkerContainerPanel.setMaxLineNumber(spectrum.size());
        ((BeanTableModel<MSMSSpectrum>) spectrumTableModel.getBaseModel()).setData(spectrum);
        
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableToolbar = new javax.swing.JToolBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        libraryTabbedPane = new javax.swing.JTabbedPane();
        tablePane = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        extractBtn = new javax.swing.JButton();

        tableToolbar.setFloatable(false);
        tableToolbar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        tableToolbar.setRollover(true);

        jSplitPane1.setDividerLocation(130);
        jSplitPane1.setLeftComponent(libraryTabbedPane);

        tablePane.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(tablePane);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(extractBtn, org.openide.util.NbBundle.getMessage(MGFPanel.class, "MGFPanel.extractBtn.text")); // NOI18N
        extractBtn.setFocusable(false);
        extractBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        extractBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        extractBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(extractBtn);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addContainerGap())
        );

        initCustomComponents();
    }// </editor-fold>//GEN-END:initComponents

    private void extractBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractBtnActionPerformed
        IRawFileViewer viewer = appController.getCurrentRawFileViewer();
        if (viewer.getCurrentRawfile().isDIAFile()) {
            DisplayMode mode = MzScopeConstants.DisplayMode.REPLACE;
            
            int selectedRow = spectrumTable.convertRowIndexToNonFilteredModel(spectrumTable.getSelectedRow());
            if (selectedRow >= 0) {
               MSMSSpectrum spectrum = ((BeanTableModel<MSMSSpectrum>) spectrumTableModel.getBaseModel()).getData().get(selectedRow);
               MsnExtractionRequest.Builder builder = MsnExtractionRequest.builder();
                    builder.setMzTolPPM(MzScopePreferences.getInstance().getMzPPMTolerance()).setMz(spectrum.getPrecursorMz());
                    viewer.extractAndDisplayChromatogram(builder.build(), mode, null);
                    mode = MzScopeConstants.DisplayMode.OVERLAY;
                try {
                    // Beurk : avoid replace/overlay bug
                    Thread.currentThread().sleep(70);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            int[] selectedRows = peaksTable.getSelectedRows();
            List<Peak> entries = ((BeanTableModel<Peak>) peaksTableModel.getBaseModel()).getData();
            if (selectedRows.length > 0) {
                for (int k = 0; k < selectedRows.length; k++) {
                    Peak peak = entries.get(peaksTable.convertRowIndexToNonFilteredModel(selectedRows[k]));
                    MsnExtractionRequest.Builder builder = MsnExtractionRequest.builder();
                    builder.setMzTolPPM(MzScopePreferences.getInstance().getMzPPMTolerance());
                    builder.setMz(peak.getSpectrum().getPrecursorMz()).setFragmentMz(peak.getMz()).setFragmentMzTolPPM(MzScopePreferences.getInstance().getFragmentMzPPMTolerance());
                    viewer.extractAndDisplayChromatogram(builder.build(), mode, null);
                    // then change to overlay mode for following extractions : TODO wrong : the extraction may terminate in a different order ! 
                    mode = MzScopeConstants.DisplayMode.OVERLAY;
                }
            }
        }
    }//GEN-LAST:event_extractBtnActionPerformed

    private void initCustomComponents() {
      JScrollPane peaksScrollPane = new JScrollPane();
      peaksTable = new DecoratedMarkerTable() {

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
      peaksTable.setModel(peaksTableModel);
      peaksTable.getColumnExt("charge").setVisible(false);
      peaksTable.getColumnExt("spectrum").setVisible(false);
      
      peaksScrollPane.setViewportView(peaksTable);
      peaksTable.setFillsViewportHeight(true);
      peaksTable.setViewport(peaksScrollPane.getViewport());

      ExportButton exportButton = new ExportButton(peaksTableModel, "Export", peaksTable);
      tableToolbar.add(exportButton);
      FilterButton filterButton = new FilterButton(peaksTableModel) {
         @Override
         protected void filteringDone() {         }
      };
      
      tableToolbar.add(filterButton);
      peaksMarkerContainerPanel = new MarkerContainerPanel(peaksScrollPane, peaksTable);
      JLabel label = new JLabel("Fragments");
      label.setBorder(new EmptyBorder(3,10,5,10));
      tablePane.add(label, BorderLayout.NORTH);
      tablePane.add(tableToolbar, BorderLayout.WEST);
      tablePane.add(peaksMarkerContainerPanel, BorderLayout.CENTER);

      
      JScrollPane spectrumScrollPane = new JScrollPane();
      spectrumTable = new DecoratedMarkerTable() {

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
         
         @Override
         public void valueChanged(ListSelectionEvent e) {
            super.valueChanged(e);
            int selectedRow = convertRowIndexToNonFilteredModel(this.getSelectedRow());
            if (selectedRow >= 0) {
               List<MSMSSpectrum> spectrum = ((BeanTableModel<MSMSSpectrum>) spectrumTableModel.getBaseModel()).getData();
               ((BeanTableModel<Peak>) peaksTableModel.getBaseModel()).setData(spectrum.get(selectedRow).getPeaks());
            }
        }
         
      };
      
      spectrumTable.setModel(spectrumTableModel);
      spectrumTable.getColumnExt("annotations").setVisible(false);
      spectrumTable.getColumnExt("peaks").setVisible(false);
      spectrumTable.getColumnExt("massValues").setVisible(false);
      spectrumTable.getColumnExt("intensityValues").setVisible(false);

      spectrumScrollPane.setViewportView(spectrumTable);
      spectrumTable.setFillsViewportHeight(true);
      spectrumTable.setViewport(spectrumScrollPane.getViewport());
      spectrumMarkerContainerPanel = new MarkerContainerPanel(spectrumScrollPane, spectrumTable);
      libraryTabbedPane.add("Precursor list", spectrumMarkerContainerPanel);
      
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton extractBtn;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTabbedPane libraryTabbedPane;
    private javax.swing.JPanel tablePane;
    private javax.swing.JToolBar tableToolbar;
    // End of variables declaration//GEN-END:variables
}
