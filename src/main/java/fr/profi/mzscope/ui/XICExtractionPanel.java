/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ui;

import com.google.common.eventbus.Subscribe;
import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class XICExtractionPanel extends javax.swing.JPanel {
   
   private static Logger logger = LoggerFactory.getLogger(XICExtractionPanel.class);
   private IRawFile rawFile;

   
   class ExtractionWorker extends SwingWorker<Chromatogram, Void>  {
      
         private double minMz, maxMz;
         
         public ExtractionWorker(double min, double max) {
            this.minMz = min;
            this.maxMz = max;
         }
         
         @Override
         protected Chromatogram doInBackground() throws Exception {
            return rawFile.getXIC(minMz, maxMz);
         }

         @Override
         protected void done() {
            try {
               AppEventBus.eventBus.post(new NewChromatogramEvent(XICExtractionPanel.this, get(), rawFile));
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      }
   /**
    * Creates new form ExtractXICPanel
    */
   public XICExtractionPanel() {
      initComponents();
      AppEventBus.eventBus.register(this);
   }

   public void setRawFile(IRawFile rawfile) {
      this.rawFile = rawfile;
   }
   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jTabbedPane1 = new javax.swing.JTabbedPane();
      jPanel4 = new javax.swing.JPanel();
      jPanel1 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      massRangeTF = new javax.swing.JTextField();
      jLabel2 = new javax.swing.JLabel();
      toleranceTF = new javax.swing.JTextField();
      jCheckBox1 = new javax.swing.JCheckBox();

      jLabel1.setText("mass range:");

      massRangeTF.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            massRangeTFActionPerformed(evt);
         }
      });

      jLabel2.setText("Tol (ppm):");

      toleranceTF.setText("10.0");

      jCheckBox1.setText("display overlay");
      jCheckBox1.setEnabled(false);

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(massRangeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jLabel2)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(toleranceTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
               .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(0, 23, Short.MAX_VALUE))
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(massRangeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel2)
               .addComponent(toleranceTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jCheckBox1)
            .addGap(0, 70, Short.MAX_VALUE))
      );

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );

      jTabbedPane1.addTab("Extraction", jPanel4);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jTabbedPane1)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jTabbedPane1)
      );
   }// </editor-fold>//GEN-END:initComponents

   private void massRangeTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_massRangeTFActionPerformed
      String text = massRangeTF.getText().trim();
      double minMz = Double.NaN;
      double maxMz = Double.NaN;
      String[] masses = text.split("-");
      minMz = Double.parseDouble(masses[0]);
      if (masses.length == 1) {
         double ppm = Double.parseDouble(toleranceTF.getText().trim());
         maxMz = minMz + minMz * ppm / 1e6;
         minMz -= minMz * ppm / 1e6;
      } else {
         maxMz = Double.parseDouble(masses[1]);
      }
      SwingWorker worker = new ExtractionWorker(minMz, maxMz);
      worker.execute();
   }//GEN-LAST:event_massRangeTFActionPerformed

 @Subscribe public void handleNewChromatogramEvent(NewChromatogramEvent event) {
      if (event.source != this) {
         logger.info("New Chromatogram event received");
      } else {
         logger.info("receive my own event ... ");
      }
}
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JCheckBox jCheckBox1;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JTextField massRangeTF;
   private javax.swing.JTextField toleranceTF;
   // End of variables declaration//GEN-END:variables
}
