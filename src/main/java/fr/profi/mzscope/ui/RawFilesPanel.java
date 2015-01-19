/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.IRawFile;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author VD225637
 */
public class RawFilesPanel extends javax.swing.JPanel  {
    final private static Logger logger = LoggerFactory.getLogger(RawMinerFrame.class);
    
    private RawMinerFrame m_parentFrame;
    
    /**
     * Creates new form RawFilesPanel
     */
    public RawFilesPanel() {
        initComponents();
        rawFilesListModel = (RawFileListModel) rawFilesList1.getModel();
    }
       
    protected void setParentFrame(RawMinerFrame parent){
        this.m_parentFrame = parent;
    }
    
    public void addFile(IRawFile file){
        rawFilesListModel.add(file);
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPopupMenu1 = new javax.swing.JPopupMenu();
      viewRawFileMI = new javax.swing.JMenuItem();
      openedRawFilesLabel = new javax.swing.JLabel();
      jScrollPane2 = new javax.swing.JScrollPane();
      rawFilesList1 = new javax.swing.JList();

      viewRawFileMI.setText("View data");
      viewRawFileMI.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            viewRawFileMIActionPerformed(evt);
         }
      });
      jPopupMenu1.add(viewRawFileMI);

      openedRawFilesLabel.setText("Raw files");

      rawFilesList1.setModel(new RawFileListModel()
      );
      rawFilesList1.setComponentPopupMenu(jPopupMenu1);
      rawFilesList1.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
      rawFilesList1.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            rawFilesList1MouseClicked(evt);
         }
         public void mousePressed(java.awt.event.MouseEvent evt) {
            rawFilesList1MousePressed(evt);
         }
      });
      jScrollPane2.setViewportView(rawFilesList1);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jScrollPane2)
                  .addGap(5, 5, 5))
               .addGroup(layout.createSequentialGroup()
                  .addComponent(openedRawFilesLabel)
                  .addContainerGap(156, Short.MAX_VALUE))))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(openedRawFilesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addGap(5, 5, 5))
      );

      openedRawFilesLabel.getAccessibleContext().setAccessibleName("Raw files");
   }// </editor-fold>//GEN-END:initComponents

    private void viewRawFileMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewRawFileMIActionPerformed
        if(m_parentFrame != null ) {
            if (rawFilesList1.getSelectedValuesList().size() > 1) {
               m_parentFrame.displayRawAction(rawFilesList1.getSelectedValuesList());
            } else {
               m_parentFrame.displayRawAction((IRawFile)rawFilesList1.getSelectedValue());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to view selected data, unspecified main frame !", "View Data  Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_viewRawFileMIActionPerformed

    private void rawFilesList1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawFilesList1MousePressed
        if(rawFilesList1.getSelectedIndex() > -1 && SwingUtilities.isRightMouseButton(evt)){
            jPopupMenu1.show((JComponent)evt.getSource(),evt.getX(), evt.getY());
        }      
    }//GEN-LAST:event_rawFilesList1MousePressed

   private void rawFilesList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawFilesList1MouseClicked
      if (evt.getClickCount() == 2) {
         if(rawFilesList1.getSelectedIndex() > -1 ){
            m_parentFrame.displayRawAction((IRawFile)rawFilesList1.getSelectedValue());
        }   
      }
   }//GEN-LAST:event_rawFilesList1MouseClicked


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPopupMenu jPopupMenu1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JLabel openedRawFilesLabel;
   private javax.swing.JList rawFilesList1;
   private javax.swing.JMenuItem viewRawFileMI;
   // End of variables declaration//GEN-END:variables
    private RawFileListModel rawFilesListModel; 

    
}

