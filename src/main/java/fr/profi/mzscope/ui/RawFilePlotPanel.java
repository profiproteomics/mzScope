/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import com.google.common.eventbus.Subscribe;
import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzdb.model.Feature;
import fr.profi.mzdb.model.ScanHeader;
import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.Scan;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class RawFilePlotPanel extends javax.swing.JPanel implements IRawFilePlot, KeyEventDispatcher {

   final private static Logger logger = LoggerFactory.getLogger(RawFilePlotPanel.class);
   final private static DecimalFormat xFormatter = new DecimalFormat("0.000");
   final private static DecimalFormat yFormatter = new DecimalFormat("#,###,###");
   final private static Font tickLabelFont = new Font("SansSerif", java.awt.Font.PLAIN, 10);
   final private static Font titleFont = new Font("SansSerif", java.awt.Font.PLAIN, 12);
   final private IRawFile rawfile;
   private Chromatogram currentChromatogram;
   private Scan currentScan;
   private ChartPanel chromatogramPanel;
   private JFreeChart chromatogramChart;
   private ChartPanel spectrumPanel;
   private int lastCrossHairScanIdx = Integer.MIN_VALUE;

   /**
    * Creates new form IRawFilePlotPanel
    */
   public RawFilePlotPanel(IRawFile rawfile) {
      this.rawfile = rawfile;
      initComponents();
      initChartPanels();
      KeyEventDispatcherDecorator.addKeyEventListener(this);
      displayTIC();
      AppEventBus.eventBus.register(this);
   }

   private void initChartPanels() {

      XYSeriesCollection dataset = new XYSeriesCollection();
      chromatogramChart = ChartFactory.createXYLineChart("", null, null, dataset, PlotOrientation.VERTICAL, false, true, false);
      chromatogramPanel = new ChartPanel(chromatogramChart);
      chromatogramPanel.addChartMouseListener(new ChartMouseListener() {

         public void chartMouseMoved(ChartMouseEvent event) {
            int deviceX = event.getTrigger().getX();

            JFreeChart jfreechart = event.getChart();
            if (jfreechart != null) {
               XYPlot xyplot = event.getChart().getXYPlot();
               double domain = xyplot.getDomainAxis().java2DToValue(deviceX, chromatogramPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
               int result = Arrays.binarySearch(currentChromatogram.time, domain);
               if (~result < currentChromatogram.time.length) {
                  xyplot.clearAnnotations();
                  StringBuilder builder = new StringBuilder();
                  builder.append(xFormatter.format(xyplot.getDataset().getXValue(0, ~result))).append("-");
                  builder.append(yFormatter.format(xyplot.getDataset().getYValue(0, ~result)));
                  xyplot.addAnnotation(new XYPointerAnnotation(builder.toString(), xyplot.getDataset().getXValue(0, ~result), xyplot.getDataset().getYValue(0, ~result), -Math.PI / 2.0));
               }
            }
         }

         public void chartMouseClicked(ChartMouseEvent event) {

         }
      });
      chromatogramChart.addProgressListener(new ChartProgressListener() {

         public void chartProgress(ChartProgressEvent event) {
            XYPlot xyplot = event.getChart().getXYPlot();
            if (event.getType() == ChartProgressEvent.DRAWING_FINISHED) {
               double d = xyplot.getDomainCrosshairValue();
               int scanIdx = rawfile.getScanId(d * 60.0);
               if (scanIdx != lastCrossHairScanIdx) {
                  lastCrossHairScanIdx = scanIdx;
                  displayScan(scanIdx);
               }
            }
         }
      });
      chromatogramContainerPanel.removeAll();
      chromatogramContainerPanel.add(chromatogramPanel, BorderLayout.CENTER);
      XYPlot xyplot = chromatogramPanel.getChart().getXYPlot();
      xyplot.getDomainAxis().setTickLabelFont(tickLabelFont);
      xyplot.getRangeAxis().setTickLabelFont(tickLabelFont);
      xyplot.setDomainCrosshairVisible(true);
      xyplot.setDomainCrosshairLockedOnData(false);
      xyplot.setRangeCrosshairVisible(false);

      // Create Scan Charts
      
      dataset = new XYSeriesCollection();
      JFreeChart scanChart = ChartFactory.createXYLineChart("Scan", null, null, dataset, PlotOrientation.VERTICAL, false, true, false);
      xyplot = scanChart.getXYPlot();
      XYItemRenderer renderer = xyplot.getRenderer();
      renderer.setSeriesPaint(0, Color.RED);

      xyplot.getDomainAxis().setTickLabelFont(tickLabelFont);
      xyplot.getRangeAxis().setTickLabelFont(tickLabelFont);
      spectrumPanel = new ChartPanel(scanChart);
      spectrumPanel.addChartMouseListener(new ChartMouseListener() {

         public void chartMouseMoved(ChartMouseEvent event) {
            int deviceX = event.getTrigger().getX();

            JFreeChart jfreechart = event.getChart();
            if (jfreechart != null) {
               XYPlot xyplot = event.getChart().getXYPlot();
               double domain = xyplot.getDomainAxis().java2DToValue(deviceX, spectrumPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
               double[] domainValues = ((currentScan.getPeaksMz() == null) ? currentScan.getMasses() : currentScan.getPeaksMz());
               float[] rangeValues = ((currentScan.getPeaksIntensities() == null) ? currentScan.getIntensities() : currentScan.getPeaksIntensities());
               int result = Arrays.binarySearch(domainValues, (float) domain);
               if (~result < domainValues.length) {
                  xyplot.clearAnnotations();
                  StringBuilder builder = new StringBuilder();
                  builder.append(xFormatter.format(domainValues[~result])).append("-");
                  builder.append(yFormatter.format(rangeValues[~result]));
                  xyplot.addAnnotation(new XYPointerAnnotation(builder.toString(), domainValues[~result], rangeValues[~result], -Math.PI / 2.0));
               }
            }
         }

         public void chartMouseClicked(ChartMouseEvent arg0) {
         }
      });

      spectrumContainerPanel.removeAll();
      spectrumContainerPanel.add(spectrumPanel, BorderLayout.CENTER);

   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jSplitPane1 = new javax.swing.JSplitPane();
      chromatogramContainerPanel = new javax.swing.JPanel();
      spectrumContainerPanel = new javax.swing.JPanel();

      setBackground(new java.awt.Color(240, 240, 40));
      setLayout(new java.awt.BorderLayout());

      jSplitPane1.setDividerLocation(160);
      jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
      jSplitPane1.setResizeWeight(0.5);
      jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
      jSplitPane1.setDoubleBuffered(true);
      jSplitPane1.setOneTouchExpandable(true);

      chromatogramContainerPanel.setLayout(new java.awt.BorderLayout());
      jSplitPane1.setTopComponent(chromatogramContainerPanel);

      spectrumContainerPanel.setLayout(new java.awt.BorderLayout());
      jSplitPane1.setBottomComponent(spectrumContainerPanel);

      add(jSplitPane1, java.awt.BorderLayout.CENTER);
   }// </editor-fold>//GEN-END:initComponents

   @Override
   public void displayChromatogram(Chromatogram chromato) {
      this.currentChromatogram = chromato;
      XYSeries series = new XYSeries("Chromato");
      for (int k = 0; k < chromato.intensities.length; k++) {
         series.add(chromato.time[k], chromato.intensities[k]);
      }

      XYSeriesCollection dataset = new XYSeriesCollection();
      dataset.addSeries(series);
      StringBuilder builder = new StringBuilder("Mass range: ");
      builder.append(xFormatter.format(chromato.minMz)).append("-").append(xFormatter.format(chromato.maxMz));
      chromatogramPanel.getChart().setTitle(new TextTitle(builder.toString(), titleFont));

      chromatogramPanel.restoreAutoRangeBounds();
      XYPlot xyplot = chromatogramPanel.getChart().getXYPlot();
      xyplot.clearDomainMarkers();
      xyplot.setDataset(dataset);

      XYItemRenderer renderer = xyplot.getRenderer();
      renderer.setSeriesPaint(0, Color.RED);
      //renderer.setSeriesStroke(0, new BasicStroke(1.5f));

   }

   @Override
   public void displayScan(int index) {
      if ((currentScan == null) || (index != currentScan.getIndex())) {
         //TODO check index bounds ... + MS2 scan or not ??
         logger.debug("Display Scan Id = " + index);
         currentScan = rawfile.getScan(index);
         XYSeries series = new XYSeries("Scan");
         double[] masses = currentScan.getMasses();
         float[] intensities = currentScan.getIntensities();
         for (int k = 0; k < currentScan.getMasses().length; k++) {
            series.add(masses[k], intensities[k]);
         }

         XYSeriesCollection dataset = new XYSeriesCollection();
         dataset.addSeries(series);
         spectrumPanel.getChart().setTitle(new TextTitle(currentScan.getTitle(), titleFont));
         XYPlot xyplot = spectrumPanel.getChart().getXYPlot();
         xyplot.setDataset(dataset);
         XYItemRenderer renderer = xyplot.getRenderer();
         renderer.setSeriesPaint(0, Color.RED);
      }
   }

   @Override
   public void displayFeature(final Feature f) {
      double ppm = 10.0f;
      final double maxMz = f.getMz() + f.getMz() * ppm / 1e6;
      final double minMz = f.getMz() - f.getMz() * ppm / 1e6;

      SwingWorker worker = new SwingWorker<Chromatogram, Void>() {
         @Override
         protected Chromatogram doInBackground() throws Exception {
            return getRawfile().getXIC(minMz, maxMz);
         }

         @Override
         protected void done() {
            try {
               displayChromatogram(get());
//               displayScan(f.getApexScanHeader().getScanId());
               displayScan(f.getBasePeakel().getApexScanId());
//               ScanHeader[] sch = Feature.getPeakelsScanHeaders(f.getPeakels()) ;
               
               // draw a horizontal line across the chart at y == 0
//               chromatogramPanel.getChart().getXYPlot().addDomainMarker(new IntervalMarker(sch[0].getElutionTime() / 60.0, sch[sch.length - 1].getElutionTime() / 60.0, Color.ORANGE, new BasicStroke(1),
//                       Color.RED, new BasicStroke(1), 0.3f));
               chromatogramPanel.getChart().getXYPlot().addDomainMarker(new IntervalMarker(f.getBasePeakel().getFirstElutionTime() / 60.0, f.getBasePeakel().getLastElutionTime() / 60.0, Color.ORANGE, new BasicStroke(1),
                       Color.RED, new BasicStroke(1), 0.3f));

            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };
      worker.execute();

   }

   @Override
   public IRawFile getRawfile() {
      return rawfile;
   }

   public Chromatogram getCurrentChromatogram() {
      return currentChromatogram;
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel chromatogramContainerPanel;
   private javax.swing.JSplitPane jSplitPane1;
   private javax.swing.JPanel spectrumContainerPanel;
   // End of variables declaration//GEN-END:variables

   public boolean dispatchKeyEvent(KeyEvent e) {
      if (e.isConsumed() || e.getID() != KeyEvent.KEY_PRESSED) {
         return false;
      }
      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
         if (currentScan != null) {
            displayScan(currentScan.getIndex() - 1);
         }
         e.consume();
         return true;
      } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
         if (currentScan != null) {
            displayScan(currentScan.getIndex() + 1);
         }
         e.consume();
         return true;
      }
      return false;
   }

   private void displayTIC() {
      logger.info("Display TIC chromatogram");
      SwingWorker worker = new SwingWorker<Chromatogram, Void>() {
         @Override
         protected Chromatogram doInBackground() throws Exception {
            return getRawfile().getTIC();
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

   @Subscribe public void handleNewChromatogramEvent(NewChromatogramEvent event) {
      logger.info("New Chromatogram event received");
      if (event.rawFile == rawfile) {
         displayChromatogram(event.chromatogram);
      }
   }
}
