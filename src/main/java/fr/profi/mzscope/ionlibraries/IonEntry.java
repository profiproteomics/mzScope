/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ionlibraries;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 *
 * @author CB205360
 */
public interface IonEntry {


   public CsvSchema getSchema();
            
   public Double getQ1();
   
   public Double getQ3();
   
   public Double getRT_detected();

   public void setRT_detected(Double rt_predicted);

   public String getProtein_name();

   public Double getRelative_intensity();

   public String getStripped_sequence();

   public String getModification_sequence();

   public Integer getPrec_z();

   public String getFrg_type();

   public String getFrg_z();

   public Double getInitialRT();

   public String getUniprot_id();

   public String getScore();

   public Double getPrec_y();

   public Boolean getShared();

   public Double getRT_observed();

   public void setRT_observed(Double rt_observed);

   public Double getRT_delta();

   public void setRT_delta(Double d);

   public Double getRT_predicted();

   public void setRT_predicted(Double y);

}
