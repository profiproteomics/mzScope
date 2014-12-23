/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.util.KeyEventDispatcherDecorator;
import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzdb.model.Feature;
import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.Scan;
import fr.profi.mzscope.util.CyclicColorPalette;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
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
   //final private IRawFile rawfile;
   private ChartPanel chromatogramPanel;
   private ChartPanel spectrumPanel;

   private Chromatogram currentChromatogram;
   private Scan currentScan;

   private XYItemRenderer stickRenderer = new XYItemStickRenderer();
   private XYItemRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);

   /**
    * Creates new form IRawFilePlotPanel
    */
   public RawFilePlotPanel(IRawFile rawfile) {
      //this.rawfile = rawfile;
      initComponents();
      initChartPanels();
      KeyEventDispatcherDecorator.addKeyEventListener(this);
      displayTIC(rawfile);
   }

   private void initChartPanels() {

      XYSeriesCollection dataset = new XYSeriesCollection();
      JFreeChart chromatogramChart = ChartFactory.createXYLineChart("", null, null, dataset, PlotOrientation.VERTICAL, false, true, false);
      chromatogramPanel = new ChartPanel(chromatogramChart);
      chromatogramPanel.addChartMouseListener(new ChartMouseListener() {

         public void chartMouseMoved(ChartMouseEvent event) {
            int deviceX = event.getTrigger().getX();
            JFreeChart jfreechart = event.getChart();
            if ((jfreechart != null) && (currentChromatogram != null)) {
               XYPlot xyplot = event.getChart().getXYPlot();
               double domain = xyplot.getDomainAxis().java2DToValue(deviceX, chromatogramPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
               int result = Arrays.binarySearch(currentChromatogram.time, domain);
               if (~result < currentChromatogram.time.length) {
                  xyplot.clearAnnotations();
                  double x = xyplot.getDataset().getXValue(0, ~result);
                  double y = xyplot.getDataset().getYValue(0, ~result);
                  final Rectangle2D area = chromatogramPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

                  RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(xyplot.getRangeAxisLocation(), PlotOrientation.VERTICAL);
                  double pY = xyplot.getRangeAxis().valueToJava2D(y, area, rangeEdge);
                  double angle = (pY < 60.0) ? -5.0 * Math.PI / 4.0 : -Math.PI / 2.0;
                  xyplot.addAnnotation(new XYPointerAnnotation(xFormatter.format(xyplot.getDataset().getXValue(0, ~result)), x, y, angle));
               }
            }
         }

         public void chartMouseClicked(ChartMouseEvent event) {
            int deviceX = event.getTrigger().getX();
            JFreeChart jfreechart = event.getChart();
            if ((jfreechart != null) && (currentChromatogram != null)) {
               XYPlot xyplot = event.getChart().getXYPlot();
               double d = xyplot.getDomainAxis().java2DToValue(deviceX, chromatogramPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
               int scanIdx = getCurrentRawfile().getScanId(d * 60.0);
               displayScan(scanIdx);
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
      xyplot.setDomainPannable(true);
      xyplot.setBackgroundPaint(CyclicColorPalette.GRAY_BACKGROUND);
      xyplot.setRangeGridlinePaint(CyclicColorPalette.GRAY_GRID);
      
      // Create Scan Charts
      dataset = new XYSeriesCollection();
      JFreeChart scanChart = ChartFactory.createXYLineChart("Scan", null, null, dataset, PlotOrientation.VERTICAL, false, true, false);
      xyplot = scanChart.getXYPlot();
      XYItemRenderer renderer = xyplot.getRenderer();
      renderer.setSeriesPaint(0, CyclicColorPalette.getColor(1));
      xyplot.setDomainPannable(true);
      xyplot.setDomainCrosshairVisible(true);
      xyplot.setDomainCrosshairLockedOnData(false);
      xyplot.setRangeCrosshairVisible(false);
      xyplot.getDomainAxis().setTickLabelFont(tickLabelFont);
      xyplot.getRangeAxis().setTickLabelFont(tickLabelFont);
      xyplot.setBackgroundPaint(CyclicColorPalette.GRAY_BACKGROUND);
      xyplot.setRangeGridlinePaint(CyclicColorPalette.GRAY_GRID);
      spectrumPanel = new ChartPanel(scanChart);
      spectrumPanel.addChartMouseListener(new ChartMouseListener() {

         public void chartMouseMoved(ChartMouseEvent event) {
            int deviceX = event.getTrigger().getX();

            JFreeChart jfreechart = event.getChart();
            if ((jfreechart != null) && (currentScan != null)) {
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
                  double y = rangeValues[~result];
                  final Rectangle2D area = spectrumPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
                  RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(xyplot.getRangeAxisLocation(), PlotOrientation.VERTICAL);
                  double pY = xyplot.getRangeAxis().valueToJava2D(y, area, rangeEdge);
                  double angle = (pY < 60.0) ? -5.0 * Math.PI / 4.0 : -Math.PI / 2.0;
                  xyplot.addAnnotation(new XYPointerAnnotation(builder.toString(), domainValues[~result], rangeValues[~result], angle));
               }
            }
         }

         public void chartMouseClicked(ChartMouseEvent event) {
            JFreeChart jfreechart = event.getChart();
            if ((event.getTrigger().getClickCount() == 2) && (jfreechart != null) && (currentScan != null)) {
               XYPlot xyplot = event.getChart().getXYPlot();
               double domain = xyplot.getDomainAxis().java2DToValue(event.getTrigger().getX(), spectrumPanel.getScreenDataArea(), xyplot.getDomainAxisEdge());
               //TODO : choose extraction ppm value (10 ppm)
               double maxMz = domain + domain * 10.0 / 1e6;
               double minMz = domain - domain * 10.0 / 1e6;
               if ((event.getTrigger().getModifiers() & KeyEvent.ALT_MASK) != 0) {
                  SwingWorker worker = new AbstractXICExtractionWorker(RawFilePlotPanel.this, getCurrentRawfile(), minMz, maxMz) {
                     @Override
                     protected void done() {
                        try {
                           addChromatogram(get());
                        } catch (Exception e) {
                           logger.error("Error while extraction chromatogram", e);
                        }
                     }
                  };
                  worker.execute();
               } else {
                  SwingWorker worker = new AbstractXICExtractionWorker(RawFilePlotPanel.this, getCurrentRawfile(), minMz, maxMz) {
                     @Override
                     protected void done() {
                        try {
                           displayChromatogram(get());
                        } catch (Exception e) {
                           logger.error("Error while extraction chromatogram", e);
                        }
                     }
                  };
                  worker.execute();
               }
            }
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
      xyplot.getRangeAxis().setUpperMargin(0.3);
      XYItemRenderer renderer = xyplot.getRenderer();
      renderer.setSeriesPaint(0, CyclicColorPalette.getColor(1));
   }

   public void addChromatogram(Chromatogram chromato) {
      XYSeries series = new XYSeries("Chromato");
      for (int k = 0; k < chromato.intensities.length; k++) {
         series.add(chromato.time[k], chromato.intensities[k]);
      }
      XYPlot xyplot = chromatogramPanel.getChart().getXYPlot();
      ((XYSeriesCollection) xyplot.getDataset()).addSeries(series);
      xyplot.getRenderer().setSeriesPaint(xyplot.getDataset().getSeriesCount() - 1, CyclicColorPalette.getColor(xyplot.getDataset().getSeriesCount()));
   }

   @Override
   public void displayScan(int index) {
      logger.info("Display Scan " + index);
      if ((currentScan == null) || (index != currentScan.getIndex())) {
         currentScan = getCurrentRawfile().getScan(index);
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
         if (currentScan.getDataType() == Scan.ScanType.CENTROID) {
            stickRenderer.setSeriesPaint(0, CyclicColorPalette.getColor(1));
            xyplot.setRenderer(stickRenderer);
         } else {
            lineRenderer.setSeriesPaint(0, CyclicColorPalette.getColor(1));
            xyplot.setRenderer(lineRenderer);
         }
         xyplot.getRangeAxis().setUpperMargin(0.3);
         chromatogramPanel.getChart().getXYPlot().setDomainCrosshairValue(currentScan.getRetentionTime() / 60.0);
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
            return getCurrentRawfile().getXIC(minMz, maxMz);
         }

         @Override
         protected void done() {
            try {
               displayChromatogram(get());
               displayScan(f.getBasePeakel().getApexScanId());
               Marker marker = new IntervalMarker(f.getBasePeakel().getFirstElutionTime() / 60.0, f.getBasePeakel().getLastElutionTime() / 60.0, Color.ORANGE, new BasicStroke(1), Color.RED, new BasicStroke(1), 0.3f);
               chromatogramPanel.getChart().getXYPlot().addDomainMarker(marker);
            } catch (Exception e) {
               logger.error("Error while reading chromatogram");
            }
         }
      };
      worker.execute();

   }

   @Override
   public IRawFile getCurrentRawfile() {
      return currentChromatogram.rawFile;
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
      if (currentScan == null) {
         e.consume();
         return true;
      }
      if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            displayScan(getCurrentRawfile().getPreviousScanId(currentScan.getIndex(), currentScan.getMsLevel()));
            e.consume();
            return true;
         } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            displayScan(getCurrentRawfile().getNextScanId(currentScan.getIndex(), currentScan.getMsLevel()));
            e.consume();
            return true;
         }

      } else {
         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            displayScan(currentScan.getIndex() - 1);
            e.consume();
            return true;
         } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            displayScan(currentScan.getIndex() + 1);
            e.consume();
            return true;
         }
      }
      return false;
   }

   private void displayTIC(final IRawFile rawFile) {
      logger.info("Display TIC chromatogram");
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

   private class XYItemStickRenderer extends AbstractXYItemRenderer {

      public XYItemStickRenderer() {
      }

      @Override
      public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
              XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
         if (!getItemVisible(series, item)) {
            return;
         }
         double x = dataset.getXValue(series, item);
         double y = dataset.getYValue(series, item);

         if (!java.lang.Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
            double transY0 = rangeAxis.valueToJava2D(0.0, dataArea, yAxisLocation);
            g2.setStroke(DEFAULT_STROKE);
            g2.setPaint(getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
               g2.drawLine((int) transY, (int) transX, (int) transY0, (int) transX);
            } else if (orientation == PlotOrientation.VERTICAL) {
               g2.drawLine((int) transX, (int) transY, (int) transX, (int) transY0);
            }
//            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
//            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            //updateCrosshairValues(crosshairState, x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
         }
      }
   }
}
