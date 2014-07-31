/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ui;

import fr.profi.mzdb.model.Feature;
import fr.profi.mzscope.model.Chromatogram;
import fr.profi.mzscope.model.IRawFile;

/**
 *
 * @author CB205360
 */
public interface IRawFilePlot {

   void displayChromatogram(Chromatogram chromato);

   void displayFeature(Feature f);

   void displayScan(int index);

   IRawFile getRawfile();
      
   Chromatogram getCurrentChromatogram();
}
