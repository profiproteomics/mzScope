/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import fr.profi.mzscope.ui.MzScopePanel;
import java.awt.Frame;
import java.io.File;
import javax.swing.JPanel;

/**
 * main entry point for mzscope
 * @author MB243701
 */
public class MzScope implements IMzScope{

    private MzScopePanel mzScopePanel;
    
    public MzScope() {
    }
    
    /**
     * create the mzscope panel, relative to the specified frame
     * @param frame
     * @return 
     */
    public JPanel createMzScopePanel(Frame frame){
        mzScopePanel = new MzScopePanel(frame);
        return mzScopePanel;
        
    }

    /**
     * open the specified file, and extract at the moz specified value
     * @param file
     * @param moz 
     */
    @Override
    public void openRawAndExtract(File file, double moz) {
        mzScopePanel.openRawAndExtract(file, moz);
    }

    @Override
    public void extractRawFile(File file, double moz) {
        mzScopePanel.extractRawFile(file, moz);
    }
}
