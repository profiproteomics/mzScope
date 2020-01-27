/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ionlibraries;

import fr.profi.mzscope.CalibrationIon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class Aligner {

    final private static Logger logger = LoggerFactory.getLogger(Aligner.class);

    private final IonLibrary library;
    private double[] x;
    private double[] y;
    private UnivariateFunction interpolationFunction; 
    private SimpleRegression regression;
    
    public Aligner(IonLibrary library) {
       this.library = library;
    }
    
    public IonLibrary getLibrary() {
        return library;
    }
    
    public boolean addCalibrationIon(CalibrationIon calibrationIon) {
       if (calibrationIon.getRT_observed() == null)
          return false;
       List<IonEntry> matchingSequenceEntries = library.getIonEntryBySequence(calibrationIon.getModification_sequence());       
       if (matchingSequenceEntries == null || matchingSequenceEntries.isEmpty()) {
          logger.info("Sequence "+calibrationIon.getModification_sequence()+" not found in the library");
          return false;
       }

       for (IonEntry me : matchingSequenceEntries) {
           if (calibrationIon.getPrec_z() == me.getPrec_z().intValue()) {
             me.setRT_observed(calibrationIon.getRT_observed());
             return true;
          }
       }
      return false;
        
//        IonEntry me = matchingSequenceEntries.get(0);
//        me.setRT_observed(calibrationIon.getRT_observed());
//        return true;
    }
    
    public void align(UnivariateInterpolator interpolator) {
       List<IonEntry> controlPoints = new ArrayList<>();
       List<Double> xValues = new ArrayList<>();
       List<Double> yValues = new ArrayList<>();
       
       // loop over ion entries to : 
       // - search for the max initial RT
       // - build a list of controlPoints = { entries with RT_observed not null }
       IonEntry lastPoint = library.getEntries().get(0);
       for (IonEntry e : library.getEntries()) {
          if (e.getRT_observed() != null) {
             controlPoints.add(e);
          }
          if (e.getInitialRT() > lastPoint.getInitialRT()) {
             lastPoint = e;
          }
       }
       
       // then sort control points by initial RT
       Collections.sort(controlPoints, new Comparator<IonEntry>() {
          @Override
          public int compare(IonEntry o1, IonEntry o2) {
             return Double.compare(o1.getInitialRT(), o2.getInitialRT());
          }
       });
       
       IonEntry e1 = controlPoints.get(0);
       IonEntry e2 = controlPoints.get(1);
       
       double slope = 1.0f*(e2.getRT_observed() - e1.getRT_observed()) / (e2.getInitialRT() - e1.getInitialRT());
       double intercept = e1.getRT_observed() - slope*e1.getInitialRT();
       
       xValues.add(0.0);
       yValues.add(intercept);
       logger.info("first control point added at ("+0.0+","+intercept+")");
       
       for (IonEntry e : controlPoints) {
             xValues.add(e.getInitialRT());
             yValues.add(e.getRT_observed());
       }
       
       
       e1 = controlPoints.get(controlPoints.size()-2);
       e2 = controlPoints.get(controlPoints.size()-1);
       
       slope = 1.0f*(e2.getRT_observed() - e1.getRT_observed()) / (e2.getInitialRT() - e1.getInitialRT());
       intercept = e1.getRT_observed() - slope*e1.getInitialRT();
       
       xValues.add(lastPoint.getInitialRT());
       yValues.add(slope*lastPoint.getInitialRT()+intercept);

       logger.info("last control point added at ("+lastPoint.getInitialRT()+","+(slope*lastPoint.getInitialRT()+intercept)+")");

              
       x = ArrayUtils.toPrimitive(xValues.toArray(new Double[xValues.size()]));
       y = ArrayUtils.toPrimitive(yValues.toArray(new Double[yValues.size()]));
       interpolationFunction = interpolator.interpolate(x, y);
    }
    
    public List<IonEntry> predictRT() {
       for (IonEntry e: library.getEntries()) {
          try {
             double y = interpolationFunction.value(e.getInitialRT());
             e.setRT_predicted(y);
             e.setRT_delta(y - e.getInitialRT());
          } catch (Exception ex) {
             logger.error("RT interpolation fail", ex);
          }
       }
       return library.getEntries();
    }

    public List<IonEntry> applyPredictedRT() {
       for (IonEntry e: library.getEntries()) {
             e.setRT_detected(e.getRT_predicted());
       }
       return library.getEntries();
    }
    
}
