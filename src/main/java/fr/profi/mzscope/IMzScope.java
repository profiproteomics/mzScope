/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import java.io.File;

/**
 * mzscope interface
 * @author MB243701
 */
public interface IMzScope {
    /**
     * open the specified file, and extract at the moz specified value
     * @param file
     * @param moz 
     */
    public abstract void openRawAndExtract(File file, double moz);
}
