/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class CalibrationIon {

   final private static Logger logger = LoggerFactory.getLogger(CalibrationIon.class);

   private Double q1;
   private Double RT_observed;
   private String protein_name;
   private String modification_sequence;
   private int prec_z;

   public Double getQ1() {
      return q1;
   }

   public void setQ1(Double q1) {
      this.q1 = q1;
   }

   public Double getRT_observed() {
      return RT_observed;
   }

   public void setRT_observed(Double RT_observed) {
      this.RT_observed = RT_observed;
   }

   public String getModification_sequence() {
      return modification_sequence;
   }

   public void setModification_sequence(String modification_sequence) {
      this.modification_sequence = modification_sequence;
   }

   public String getProtein_name() {
      return protein_name;
   }

   public void setProtein_name(String protein_name) {
      this.protein_name = protein_name;
   }

   public int getPrec_z() {
      return prec_z;
   }

   public void setPrec_z(int prec_z) {
      this.prec_z = prec_z;
   }

}
