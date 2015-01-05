/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.plot.XYPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class MultiRawFilePanel extends AbstractRawFilePanel {

   final private static Logger logger = LoggerFactory.getLogger(MultiRawFilePanel.class);

   private List<IRawFile> rawfiles;

   public MultiRawFilePanel(List<IRawFile> rawfiles) {
      super();
      this.rawfiles = rawfiles;
      displayTICs(rawfiles);
   }

   @Override
   public IRawFile getCurrentRawfile() {
      return currentChromatogram.rawFile;
   }

   protected void scanMouseClicked(ChartMouseEvent event) {
      if ((event.getTrigger().getClickCount() == 2)) {
         XYPlot xyplot = event.getChart().getXYPlot();
         double domain = xyplot.getDomainAxis().java2DToValue(event.getTrigger().getX(), spectrumPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
         //TODO : choose extraction ppm value (10 ppm)
         double maxMz = domain + domain * 10.0 / 1e6;
         double minMz = domain - domain * 10.0 / 1e6;
         extractChromatogram(minMz, maxMz);
      }
   }

   @Override
   public void extractChromatogram(final double minMz, final double maxMz) {
      SwingWorker worker = new SwingWorker<Integer, Chromatogram>() {

         int count = 0;

         @Override
         protected Integer doInBackground() throws Exception {

            for (IRawFile rawFile : rawfiles) {
               Chromatogram c = rawFile.getXIC(minMz, maxMz);;
               count++;
               publish(c);
            }
            return count;
         }

         @Override
         protected void process(List<Chromatogram> chunks) {
            Chromatogram c = chunks.get(chunks.size() - 1);
            if (count == 1) {
               logger.info("display first chromato");
               displayChromatogram(c);
            } else {
               logger.info("add additionnal chromato");
               addChromatogram(c);
            }
         }

         @Override
         protected void done() {
            try {
               logger.info("{} TIC chromatogram extracted", get());
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };

      worker.execute();
   }

   protected void displayTICs(final List<IRawFile> rawFiles) {
      logger.info("Display single TIC chromatogram");
      SwingWorker worker = new SwingWorker<Integer, Chromatogram>() {

         int count = 0;

         @Override
         protected Integer doInBackground() throws Exception {

            for (IRawFile rawFile : rawFiles) {
               Chromatogram c = rawFile.getTIC();
               count++;
               publish(c);
            }
            return count;
         }

         @Override
         protected void process(List<Chromatogram> chunks) {
            Chromatogram c = chunks.get(chunks.size() - 1);
            if (count == 1) {
               logger.info("display first chromato");
               displayChromatogram(c);
            } else {
               logger.info("add additionnal chromato");
               addChromatogram(c);
            }
         }

         @Override
         protected void done() {
            try {
               logger.info("{} TIC chromatogram extracted", get());
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };

      worker.execute();
   }

}
