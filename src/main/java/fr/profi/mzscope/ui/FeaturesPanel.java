/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ui;

import fr.profi.mzdb.model.Feature;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CB205360
 */
public class FeaturesPanel extends javax.swing.JPanel {

   private IRawFilePlot rawFilePanel;
   private List<Feature> features = new ArrayList<Feature>();
   
   /**
    * Creates new form FeaturesTable
    */
   public FeaturesPanel() {
      initComponents();
   }

   public FeaturesPanel(IRawFilePlot rawFilePanel) {
      this.rawFilePanel = rawFilePanel;
      initComponents();
   }
   
 
   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      featureTableModel = new fr.profi.mzscope.ui.FeaturesTableModel();
      jScrollPane1 = new javax.swing.JScrollPane();
      featureTable = new org.jdesktop.swingx.JXTable();

      jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

      featureTable.setModel(featureTableModel);
      featureTable.setEditable(false);
      featureTable.setShowGrid(true);
      featureTable.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            featureTableMouseClicked(evt);
         }
      });
      jScrollPane1.setViewportView(featureTable);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
      );
   }// </editor-fold>//GEN-END:initComponents

   private void featureTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_featureTableMouseClicked
      if ((features != null) && (!features.isEmpty()) && (rawFilePanel != null) && (evt.getClickCount() == 2)) {
         Feature f = features.get(featureTable.convertRowIndexToModel(featureTable.getSelectedRow()));
         rawFilePanel.displayFeature(f);
         //propertySheetPanel.setBean(f); // TODO
      }
   }//GEN-LAST:event_featureTableMouseClicked


   public void setFeatures(List<Feature> features) {
      featureTableModel.setFeatures(features);
      this.features = features;
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private org.jdesktop.swingx.JXTable featureTable;
   private fr.profi.mzscope.ui.FeaturesTableModel featureTableModel;
   private javax.swing.JScrollPane jScrollPane1;
   // End of variables declaration//GEN-END:variables
}

