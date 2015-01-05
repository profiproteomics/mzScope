/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzscope.util.KeyEventDispatcherDecorator;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class SingleRawFilePanel extends AbstractRawFilePanel {

   final private static Logger logger = LoggerFactory.getLogger(SingleRawFilePanel.class);

   private IRawFile rawfile;

   public SingleRawFilePanel(IRawFile rawfile) {
      super();
      this.rawfile = rawfile;
      displayTIC(rawfile);
   }

   @Override
   public IRawFile getCurrentRawfile() {
      return rawfile;
   }

   protected void displayTIC(final IRawFile rawFile) {
      logger.info("Display single TIC chromatogram");
      SwingWorker worker = new SwingWorker<Chromatogram, Void>() {
         @Override
         protected Chromatogram doInBackground() throws Exception {
            return rawFile.getTIC();
         }

         @Override
         protected void done() {
            try {
               displayChromatogram(get());
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };
      worker.execute();
   }

}