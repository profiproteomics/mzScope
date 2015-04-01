/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.proline.mzscope.model.Chromatogram;
import fr.proline.mzscope.model.ExtractionParams;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.ui.MzScopePanel;
import fr.proline.mzscope.ui.MzdbFilter;
import fr.proline.mzscope.ui.RawFileManager;
import fr.proline.mzscope.ui.RawFilesPanel;
import fr.proline.mzscope.ui.dialog.ExtractionParamsDialog;
import fr.proline.mzscope.ui.event.ExtractFeatureListener;
import fr.proline.mzscope.ui.event.RawFileListener;
import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main contains which contains the rawFilePanel and the MzScope Panel
 * @author MB243701
 */
public class RawMinerPanel extends JPanel implements RawFileListener, ExtractFeatureListener{
    private final static Logger logger = LoggerFactory.getLogger("ProlineStudio.mzScope.RawMinerPanel");
    
    private final static String LAST_DIR = "Last directory";
    private JFileChooser fileChooser = null;
    
    private RawMinerFrame parentFrame;
    
    private JSplitPane mainSplitPane = null;
    private RawFilesPanel rawFilePanel = null;
    private MzScopePanel mzScopePanel = null;
    
    public RawMinerPanel(RawMinerFrame parentFrame){
        super();
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

            this.mainSplitPane.setLeftComponent(getRawFilePanel());
            this.mainSplitPane.setRightComponent(getMzScopePanel());
        }
        return this.mainSplitPane;
    }
    
    private RawFilesPanel getRawFilePanel() {
        if (this.rawFilePanel == null) {
            this.rawFilePanel = new RawFilesPanel();
            this.rawFilePanel.addRawFileListener(this);
        }
        return this.rawFilePanel;
    }
    
    private MzScopePanel getMzScopePanel(){
        if (this.mzScopePanel == null){
            mzScopePanel = new MzScopePanel(parentFrame);
            mzScopePanel.addExtractFeatureListener(this);
        }
        return mzScopePanel;
    }

    
    private JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new JFileChooser();
            this.fileChooser.setDialogTitle("Open Raw file");
            this.fileChooser.addChoosableFileFilter(new MzdbFilter());
        }
        return this.fileChooser;
    }

    @Override
    public void displayRaw(IRawFile rawfile) {
        mzScopePanel.displayRawAction(rawfile);
    }

    @Override
    public void displayRaw(List<IRawFile> rawfiles) {
        mzScopePanel.displayRawAction(rawfiles);
    }

    @Override
    public void openRawFile() {
        openRawMI();
    }

    @Override
    public void closeRawFile(IRawFile rawfile) {
        mzScopePanel.closeRawFile(rawfile);
        //raw FilePanel
        rawFilePanel.removeFile(rawfile);
    }

    @Override
    public void closeAllFiles() {
        boolean close = mzScopePanel.closeAllRaw();
        if (close){
            rawFilePanel.removeAllFiles();
        }
    }

    @Override
    public void extractFeatures(IRawFile rawfile) {
        mzScopePanel.extractFeaturesMI(rawfile);
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
                mzScopePanel.extractFeatures(rawFile, IRawFile.ExtractionType.EXTRACT_MS2_FEATURES, extractionParams);
            }

        }
    }

    @Override
    public void detectPeakels(IRawFile rawfile) {
        mzScopePanel.detectPeakelsMI(rawfile);
    }

    @Override
    public void detectPeakels(List<IRawFile> rawfiles) {
        mzScopePanel.detectPeakelsMI(rawfiles);
    }

    @Override
    public void exportChromatogram(IRawFile rawfile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportChromatogram(List<IRawFile> rawfiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void extractFeatureListener(boolean extractFeatures, boolean detectPeakels) {
        parentFrame.setExtractFeaturesMIEnabled(extractFeatures);
        parentFrame.setDetectPeakelsMIEnabled(detectPeakels);
    }
    
    
    public void openRawMI(){
       Preferences prefs = Preferences.userNodeForPackage(this.getClass());
       String directory = prefs.get(LAST_DIR, getFileChooser().getCurrentDirectory().getAbsolutePath());
       fileChooser.setCurrentDirectory(new File(directory));
       fileChooser.setMultiSelectionEnabled(true);
       int returnVal = fileChooser.showOpenDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
          File[] files = fileChooser.getSelectedFiles();
          for (File file : files) {
             prefs.put(LAST_DIR, file.getParentFile().getAbsolutePath());
             openRaw(file);
          }
       } else {
          System.out.println("File access cancelled by user.");
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


    public void extractFeaturesMI() {
        mzScopePanel.extractFeaturesMI();
    }
    
    public void exportChromatogram() {
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            Chromatogram currentChromatogram = mzScopePanel.getCurrentChromatogram();
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
        mzScopePanel.detectPeakelsMI();
    }
    
    public void closeAllRaw() {
        closeAllFiles();
    }



}
