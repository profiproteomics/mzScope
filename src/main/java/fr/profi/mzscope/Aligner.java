/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class Aligner {

    final private static Logger logger = LoggerFactory.getLogger(Aligner.class);

    final private List<IonEntry> entries;
    private final Map<String, List<IonEntry>> entriesBySequence = new HashMap<>();
    final private List<IonEntry> nonRedondantEntries;    
    private double[] x;
    private double[] y;
    private UnivariateFunction interpolationFunction; 
   private SimpleRegression regression;
            
    public Aligner(List<IonEntry> entries) {
       this.entries = entries;
       this.nonRedondantEntries = new ArrayList<>();
       
       for (IonEntry e : entries) {
          if (!entriesBySequence.containsKey(e.getModification_sequence())) {
             entriesBySequence.put(e.getModification_sequence(), new ArrayList<IonEntry>(10));
             nonRedondantEntries.add(e);
          }
          entriesBySequence.get(e.getModification_sequence()).add(e);
       }
    }
    
    public boolean addCalibrationIon(CalibrationIon ion) {
       if (ion.getRT_observed() == null)
          return false;
       if (!entriesBySequence.containsKey(ion.getModification_sequence())) {
          logger.info("Sequence "+ion.getModification_sequence()+" not found in the library");
          return false;
       }
       List<IonEntry> matchingSequenceEntries = entriesBySequence.get(ion.getModification_sequence());
       for (IonEntry me : matchingSequenceEntries) {
          if (Precision.equals(me.getQ1(), ion.getQ1(), 1e-3) && Precision.equals(me.getQ3(), ion.getQ3(), 1e-3)) {
             me.setRT_observed(ion.getRT_observed());
             // we select only one entry by modification_sequence in the constructor. This selected entry can be different
             // than the one matched here. Add the matched entry in the nonRedondantEntries list.
             if (!nonRedondantEntries.contains(me)) {
                nonRedondantEntries.add(me);
             }
             return true;
          }
       }
      return false;
    }
    
    public void align(UnivariateInterpolator interpolator) {
       List<IonEntry> controlPoints = new ArrayList<>();
       List<Double> xValues = new ArrayList<>();
       List<Double> yValues = new ArrayList<>();
       
       for (IonEntry e : entries) {
          if (e.getRT_observed() != null) {
             controlPoints.add(e);
          }
       }
       
       Collections.sort(controlPoints, new Comparator<IonEntry>() {
          @Override
          public int compare(IonEntry o1, IonEntry o2) {
             return Double.compare(o1.getRT_detected(), o2.getRT_detected());
          }
       });
       
//       regression = new SimpleRegression();
//       for (IonEntry e : controlPoints) {
//          regression.addData(e.getRT_detected(), e.getRT_observed());
//       }
      
       xValues.add(0.0);
       yValues.add(0.0);
       
       for (IonEntry e : controlPoints) {
             xValues.add(e.getiRT());
             yValues.add(e.getRT_observed());

       }
       x = ArrayUtils.toPrimitive(xValues.toArray(new Double[xValues.size()]));
       y = ArrayUtils.toPrimitive(yValues.toArray(new Double[yValues.size()]));
       interpolationFunction = interpolator.interpolate(x, y);
    }
    
    public List<IonEntry> predictRT() {
       for (IonEntry e: entries) {
          try {
             double y = interpolationFunction.value(e.getiRT());
             e.setRT_predicted(y);
          } catch (Exception ex) {
             logger.error("RT interpolation fail", ex);
          }
       }
       return entries;
    }

    public List<IonEntry> applyPredictedRT() {
       for (IonEntry e: entries) {
             e.setRT_detected(e.getRT_predicted());
       }
       return entries;
    }

    
     public List<IonEntry> getEntries() {
        return entries;
     }

     /**
      * Returns a non redondant list of entries. Only one fragment per modification_sequence
      * @return 
      */
   public List<IonEntry> getNonRedondantEntries() {
      return nonRedondantEntries;
   }
}
