/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.model;

import fr.profi.mzdb.model.Feature;
import java.util.List;

/**
 *
 * @author CB205360
 */
public interface IRawFile {

   public Chromatogram getXIC(double min, double max);
   
   public Chromatogram getTIC();

   public List<Feature> extractFeatures();
   
   public Scan getScan(int scanIndex);
   
   public int getScanId(double retentionTime);
   
   public int getNextScanId(int scanIndex, int msLevel);
   
   public int getPreviousScanId(int scanIndex, int msLevel);
   
}
