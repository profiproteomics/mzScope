package fr.profi.mzscope.ui;

import com.google.common.base.Strings;
import fr.profi.mzdb.model.Feature;
import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.ExtractionParams;
import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzscope.ui.dialog.ExtractionParamsDialog;
import fr.profi.mzscope.ui.event.RawFileListener;
import fr.profi.mzscope.ui.event.ExtractFeatureListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main panel for mzscope
 *
 * @author MB243701
 */
public class MzScopePanel extends JPanel implements RawFileListener {
    private final static Logger logger = LoggerFactory.getLogger(MzScopePanel.class);
    
    private Frame parentFrame = null;
    private JSplitPane mainSplitPane = null;
    private JSplitPane mainLeftSplitPane = null;
    private JSplitPane mainRightSplitPane = null;
    private JSplitPane extractionSplitPane = null;
    private JFileChooser fileChooser = null;
    private JTabbedPane viewersTabPane = null;
    private JTabbedPane featuresTabPane = null;
    private RawFilesPanel rawFilePanel = null;
    private ObjectInspectorPanel propertySheetPanel = null;
    private XICExtractionPanel extractXICPanel = null;

    private IRawFilePlot selectedRawFilePanel;

    private final static String LAST_DIR = "Last directory";
    
    private EventListenerList listenerList = new EventListenerList();

    public MzScopePanel(Frame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        createMainSplitPane();
    }

    private void createMainSplitPane() {
        this.add(getMainSplitPane(), BorderLayout.CENTER);
    }

    private JSplitPane getMainSplitPane() {
        if (this.mainSplitPane == null) {
            this.mainSplitPane = new JSplitPane();
            this.mainSplitPane.setBorder(null);
            this.mainSplitPane.setDividerLocation(200);
            this.mainSplitPane.setOneTouchExpandable(true);

            this.mainSplitPane.setLeftComponent(getMainLeftComponent());
            this.mainSplitPane.setRightComponent(getMainRightComponent());
        }
        return this.mainSplitPane;
    }

    private JSplitPane getMainLeftComponent() {
        if (this.mainLeftSplitPane == null) {
            this.mainLeftSplitPane = new JSplitPane();
            this.mainLeftSplitPane.setDividerLocation(230);
            this.mainLeftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

            this.mainLeftSplitPane.setRightComponent(getFeaturesTabPane());
            this.mainLeftSplitPane.setLeftComponent(getRawFilePanel());
        }

        return this.mainLeftSplitPane;
    }

    private JSplitPane getMainRightComponent() {
        if (this.mainRightSplitPane == null) {
            this.mainRightSplitPane = new JSplitPane();
            this.mainRightSplitPane.setDividerLocation(320);
            this.mainRightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

            this.mainRightSplitPane.setRightComponent(getExtractionSplitPane());
            this.mainRightSplitPane.setLeftComponent(getViewersTabPane());
        }

        return this.mainRightSplitPane;
    }

    private RawFilesPanel getRawFilePanel() {
        if (this.rawFilePanel == null) {
            this.rawFilePanel = new RawFilesPanel();
            this.rawFilePanel.addRawFileListener(this);
        }
        return this.rawFilePanel;
    }

    private JTabbedPane getFeaturesTabPane() {
        if (this.featuresTabPane == null) {
            this.featuresTabPane = new JTabbedPane();
        }
        return this.featuresTabPane;
    }

    private JSplitPane getExtractionSplitPane() {
        if (this.extractionSplitPane == null) {
            this.extractionSplitPane = new JSplitPane();
            this.extractionSplitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            this.extractionSplitPane.setDividerLocation(370);
            this.extractionSplitPane.setRightComponent(getPropertySheetPanel());
            this.extractionSplitPane.setLeftComponent(getExtractXICPanel());
        }
        return this.extractionSplitPane;
    }

    private JTabbedPane getViewersTabPane() {
        if (this.viewersTabPane == null) {
            viewersTabPane = new JTabbedPane();
            viewersTabPane.addChangeListener(
                    new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent evt) {
                            viewersTabPaneStateChanged(evt);
                        }
                    });
        }
        return this.viewersTabPane;
    }

    private void viewersTabPaneStateChanged(ChangeEvent evt) {
        this.selectedRawFilePanel = (IRawFilePlot) viewersTabPane.getSelectedComponent();
        this.extractXICPanel.setCurrentRawFilePlot(selectedRawFilePanel);
    }

    private ObjectInspectorPanel getPropertySheetPanel() {
        if (this.propertySheetPanel == null) {
            this.propertySheetPanel = new ObjectInspectorPanel(Feature.class);
        }
        return this.propertySheetPanel;
    }

    private XICExtractionPanel getExtractXICPanel() {
        if (this.extractXICPanel == null) {
            this.extractXICPanel = new XICExtractionPanel();
        }
        return this.extractXICPanel;
    }

    private JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new JFileChooser();
            this.fileChooser.setDialogTitle("Open Raw file");
            this.fileChooser.addChoosableFileFilter(new MzdbFilter());
        }
        return this.fileChooser;
    }

    public void openRawMI() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        String directory = prefs.get(LAST_DIR, getFileChooser().getCurrentDirectory().getAbsolutePath());
        fileChooser.setCurrentDirectory(new File(directory));
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                prefs.put(LAST_DIR, file.getParentFile().getAbsolutePath());
                IRawFile rawfile = RawFileManager.getInstance().addRawFile(file);
                this.rawFilePanel.addFile(rawfile);
            }
        } else {
            logger.info("File access cancelled by user.");
        }
    }
    
    public void openRaw(File file) {
        IRawFile rawfile = RawFileManager.getInstance().addRawFile(file);
        this.rawFilePanel.addFile(rawfile);
    }
    
    public void openRaw(List<File> files) {
        for (File file : files) {
            IRawFile rawfile = RawFileManager.getInstance().addRawFile(file);
            this.rawFilePanel.addFile(rawfile);
        }
    }

    private void addRawTab(String s, Component c) {
        addTab(viewersTabPane, s, c);
    } 
    
    private void addFeatureTab(String s, Component c) {
        addTab(featuresTabPane, s, c);
    } 
    
    private void addTab(JTabbedPane tabPane, String s, Component c) {
        tabPane.add(s, c);
        int i= tabPane.getTabCount()-1;
        tabPane.setTabComponentAt(i, new ButtonTabComponent(tabPane));
    }
    
    protected void displayRawAction(IRawFile rawfile) {
        if (rawfile == null) {
            return;
        }
        AbstractRawFilePanel plotPanel = new SingleRawFilePanel(rawfile);
        //viewersTabPane.add(rawfile.getName(), plotPanel);
        addRawTab(rawfile.getName(), plotPanel);
        FeaturesPanel featuresPanel = new FeaturesPanel(plotPanel);
        //this.featuresTabPane.add(rawfile.getName(), featuresPanel);
        addFeatureTab(rawfile.getName(), featuresPanel);
    }

    protected void displayRawAction(List<IRawFile> rawfiles) {
        String name = "";
        if (rawfiles.size() > 1) {
            String prefix = Strings.commonPrefix(rawfiles.get(0).getName(), rawfiles.get(1).getName());
            if (!prefix.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (IRawFile rawfile : rawfiles) {
                    String shortName = rawfile.getName().substring(prefix.length() - 1);
                    shortName = shortName.substring(0, shortName.lastIndexOf('.'));
                    builder.append("..").append(shortName).append(',');
                }
                // trim to 30 char max
//              name = builder.substring(0, Math.min(builder.length()-1, 30))+"...";
                builder.deleteCharAt(builder.length() - 1);
                name = builder.toString();
            } else {
                name = Integer.toString(rawfiles.size()) + " files";
            }
        } else {
            name = rawfiles.get(0).getName();
        }
        AbstractRawFilePanel plotPanel = new MultiRawFilePanel(rawfiles);
        //viewersTabPane.add(name, plotPanel);
        addRawTab(name, plotPanel);
    }

    public void extractFeaturesMI() {
        extractFeaturesMI(selectedRawFilePanel.getCurrentRawfile());
    }
    
    public void extractFeaturesMI(IRawFile rawFile) {
        ExtractionParamsDialog dialog = new ExtractionParamsDialog(this.parentFrame, true);
        dialog.setExtractionParamsTitle("Extract Features Parameters");
        dialog.setLocationRelativeTo(this);
        dialog.showExtractionParamsDialog();
        if (dialog.getExtractionParams() != null) {
            extractFeatures(rawFile, IRawFile.ExtractionType.EXTRACT_MS2_FEATURES, dialog.getExtractionParams());
        }
    }

    private void extractFeatures(final IRawFile rawFile, final IRawFile.ExtractionType type, final ExtractionParams params) {
        if ((selectedRawFilePanel != null) && (viewersTabPane.getSelectedIndex() >= 0)) {
            final String tabName = viewersTabPane.getTitleAt(viewersTabPane.getSelectedIndex());
 //           final IRawFile rawFile = selectedRawFilePanel.getCurrentRawfile();
//            extractFeaturesMI.setEnabled(false);
//            detectPeakelsMI.setEnabled(false);
            fireExtractFeature(false, false);
            final long start = System.currentTimeMillis();
            SwingWorker worker = new SwingWorker<List<Feature>, Void>() {
                @Override
                protected List<Feature> doInBackground() throws Exception {
                    return rawFile.extractFeatures(type, params);
                }

                @Override
                protected void done() {
                    try {
                        List<Feature> features = get();
                        logger.info("{} features/peakels extracted in {}", features.size(), (System.currentTimeMillis() - start) / 1000.0);

                        for (int i = 0; i < featuresTabPane.getComponentCount(); i++) {
                            if (featuresTabPane.getTitleAt(i).equals(tabName)) {
                                FeaturesPanel featurePanel = (FeaturesPanel) featuresTabPane.getComponentAt(i);
                                featurePanel.setFeatures(features);
                                break;
                            }
                        }
//                        extractFeaturesMI.setEnabled(true);
//                        detectPeakelsMI.setEnabled(true);
                        fireExtractFeature(true, true);
                    } catch (Exception e) {
                        logger.error("Error while reading chromatogram");
                    }
                }
            };
            worker.execute();
            logger.debug("Feature extraction running ... ");
        }
    }

    public void exportChromatogram() {
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            Chromatogram currentChromatogram = selectedRawFilePanel.getCurrentChromatogram();
            StringBuilder stb = new StringBuilder();
            stb.append(prefs.get(LAST_DIR, fileChooser.getCurrentDirectory().getAbsolutePath())).append('/');
            stb.append("extracted_xic_").append(df.format(currentChromatogram.minMz)).append(".tsv");
            File file = new File(stb.toString());
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write("index\trt\tintensity\n");
            for (int k = 0; k < currentChromatogram.time.length; k++) {
                stb = new StringBuilder();
                stb.append(k).append("\t").append(currentChromatogram.time[k]).append("\t").append(currentChromatogram.intensities[k]);
                stb.append("\n");
                output.write(stb.toString());
            }
            logger.info("extracted Chromatogram in " + file.getAbsolutePath());
            output.close();
        } catch (Exception e) {
            logger.error("Enable to write current Chromatogram", e);
        }
    }

    public void detectPeakelsMI() {
        detectPeakelsMI(selectedRawFilePanel.getCurrentRawfile());
    }
    
    public void detectPeakelsMI(IRawFile rawfile) {
        ExtractionParamsDialog dialog = new ExtractionParamsDialog(this.parentFrame, true);
        dialog.setExtractionParamsTitle("Detect Peakels Parameters");
        dialog.setLocationRelativeTo(this);
        dialog.showExtractionParamsDialog();
        if (dialog.getExtractionParams() != null) {
            extractFeatures(rawfile, IRawFile.ExtractionType.DETECT_PEAKELS, dialog.getExtractionParams());
        }
    }
    
    public void addExtractFeatureListener(ExtractFeatureListener listener) {
        listenerList.add(ExtractFeatureListener.class, listener);
    }

    public void removeMyEventListener(ExtractFeatureListener listener) {
        listenerList.remove(ExtractFeatureListener.class, listener);
    }

    void fireExtractFeature(boolean extractFeature, boolean detectPeakel) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ExtractFeatureListener.class) {
                ((ExtractFeatureListener) listeners[i + 1]).extractFeatureListener(extractFeature, detectPeakel);
            }
        }
    }

    @Override
    public void displayRaw(IRawFile rawfile) {
        displayRawAction(rawfile);
    }

    @Override
    public void displayRaw(List<IRawFile> rawfiles) {
        displayRawAction(rawfiles);
    }
    
    public void closeAllRaw() {
        String[] options = {"Yes","No"};
        int reply = JOptionPane.showOptionDialog(parentFrame, "All files will be closed, do you want to continue?", "Close all files", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Yes");
        if (reply != JOptionPane.YES_OPTION) {
            viewersTabPane.removeAll();
            featuresTabPane.removeAll();
            rawFilePanel.removeAllFiles();
        }

    }

    @Override
    public void openRawFile() {
        openRawMI();
    }

    @Override
    public void closeRawFile(IRawFile rawfile) {
        //TODO
    }

    @Override
    public void closeAllFiles() {
        closeAllRaw();
    }

    @Override
    public void extractFeatures(IRawFile rawfile) {
        extractFeaturesMI(rawfile);
    }

    @Override
    public void extractFeatures(List<IRawFile> rawfiles) {
        ExtractionParamsDialog dialog = new ExtractionParamsDialog(this.parentFrame, true);
        dialog.setExtractionParamsTitle("Extract Features Parameters");
        dialog.setLocationRelativeTo(this);
        dialog.showExtractionParamsDialog();
        if (dialog.getExtractionParams() != null) {
            ExtractionParams extractionParams = dialog.getExtractionParams();
            for (IRawFile rawFile : rawfiles) {
                extractFeatures(rawFile, IRawFile.ExtractionType.EXTRACT_MS2_FEATURES, extractionParams);
            }
           
        }
    }

    @Override
    public void detectPeakels(IRawFile rawfile) {
        detectPeakelsMI(rawfile);
    }

    @Override
    public void detectPeakels(List<IRawFile> rawfiles) {
        ExtractionParamsDialog dialog = new ExtractionParamsDialog(this.parentFrame, true);
        dialog.setExtractionParamsTitle("Detect Peakels Parameters");
        dialog.setLocationRelativeTo(this);
        dialog.showExtractionParamsDialog();
        if (dialog.getExtractionParams() != null) {
            ExtractionParams extractionParams = dialog.getExtractionParams();
            for (IRawFile rawFile : rawfiles) {
                extractFeatures(rawFile, IRawFile.ExtractionType.DETECT_PEAKELS, extractionParams);
            }
        }
    }

    @Override
    public void exportChromatogram(IRawFile rawfile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportChromatogram(List<IRawFile> rawfiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    /**
     * MZDB file filter class
     */
    public class MzdbFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".mzdb");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Mzdb file (*.mzdb)";
        }

    }
}