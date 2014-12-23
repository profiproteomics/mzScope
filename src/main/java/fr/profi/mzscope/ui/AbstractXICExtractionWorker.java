/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzscope.ui.event.AppEventBus;
import fr.profi.mzscope.ui.event.NewChromatogramEvent;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
abstract class AbstractXICExtractionWorker extends SwingWorker<Chromatogram, Void> {

   private static Logger logger = LoggerFactory.getLogger(AbstractXICExtractionWorker.class);
   private IRawFile rawFile;
   private Object source;
   private double minMz;
   private double maxMz;

   public AbstractXICExtractionWorker(Object source, IRawFile rawFile, double min, double max) {
      this.source = source;
      this.rawFile = rawFile;
      this.minMz = min;
      this.maxMz = max;
   }

   @Override
   protected Chromatogram doInBackground() throws Exception {
      return rawFile.getXIC(minMz, maxMz);
   }

   @Override
   abstract protected void done();

}
