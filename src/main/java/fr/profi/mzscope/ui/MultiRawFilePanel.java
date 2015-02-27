/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
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
      displayTIC();
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            updateToolbar();
         }
      });
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

   protected void displayTIC() {
      final List<IRawFile> rawFiles = new ArrayList<>(rawfiles);
      logger.info("Display {} TIC chromatograms", rawFiles.size());
      SwingWorker worker = new SwingWorker<Integer, Chromatogram>() {

         int count = 0;
         boolean isFirstProcessCall = true;
         
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
            int k = 0;
            if (isFirstProcessCall) {
               logger.info("display first chromato");
               isFirstProcessCall = false;
               displayChromatogram(chunks.get(0));
               k = 1;
            } 
            for ( ; k < chunks.size(); k++) {
               logger.info("add additionnal chromato");
               addChromatogram(chunks.get(k));
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

   protected void displayBPI() {
      final List<IRawFile> rawFiles = new ArrayList<>(rawfiles);
      logger.info("Display {} BPI chromatogram", rawFiles.size());
      SwingWorker worker = new SwingWorker<Integer, Chromatogram>() {
         int count = 0;
         boolean isFirstProcessCall = true;
         @Override
         protected Integer doInBackground() throws Exception {

            for (IRawFile rawFile : rawFiles) {
               Chromatogram c = rawFile.getBPI();
               count++;
               publish(c);
            }
            return count;
         }

         @Override
         protected void process(List<Chromatogram> chunks) {
            int k = 0;
            if (isFirstProcessCall) {
               logger.info("display first chromato");
               isFirstProcessCall = false;
               displayChromatogram(chunks.get(0));
               k = 1;
            } 
            for ( ; k < chunks.size(); k++) {
               logger.info("add additionnal chromato");
               addChromatogram(chunks.get(k));
            }
         }

         @Override
         protected void done() {
            try {
               logger.info("{} BPI chromatogram extracted", get());
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };

      worker.execute();
   }

   protected JToolBar updateToolbar() {
      final JPopupMenu popupMenu = new JPopupMenu();
      ButtonGroup bg = new ButtonGroup();
      final Map<String, JRadioButtonMenuItem> map = new HashMap<>();
      ActionListener changeCurrentChromatogramAction = new ActionListener() {
         public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton aButton = (AbstractButton) actionEvent.getSource();
            logger.debug("Selected: " + aButton.getText());
         }
      };
      for (IRawFile rawFile : rawfiles) {
         JRadioButtonMenuItem mi = new JRadioButtonMenuItem(rawFile.getName());
         mi.addActionListener(changeCurrentChromatogramAction);
         popupMenu.add(mi);
         bg.add(mi);
         map.put(rawFile.getName(), mi);
      }
      map.get(rawfiles.get(0).getName()).setSelected(true);
      final JButton currentChromatoBtn = new JButton("Chr");
      setToolTipText("Display TIC Chromatogram");
      currentChromatoBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            popupMenu.show(currentChromatoBtn, 0, currentChromatoBtn.getHeight());
         }
      });
      toolbar.add(currentChromatoBtn);
      return toolbar;
   }

}