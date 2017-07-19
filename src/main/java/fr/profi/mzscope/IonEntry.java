/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IonEntry {

   final private static Logger logger = LoggerFactory.getLogger(IonEntry.class);
   
   @JsonProperty("Q1")
   private Double q1;
   @JsonProperty("Q3")
   private Double q3;
   @JsonProperty("RT_detected")
   private Double RT_detected;
   @JsonProperty("protein_name")
   private String protein_name;
   private String isotype;
   @JsonProperty("relative_intensity")
   private Double relative_intensity;
   @JsonProperty("stripped_sequence")
   private String stripped_sequence;
   @JsonProperty("modification_sequence")
   private String modification_sequence;
   @JsonProperty("prec_z")
   private String prec_z;
   @JsonProperty("frg_type")
   private String frg_type;
   @JsonProperty("frg_z")
   private String frg_z;
   @JsonProperty("frg_nr")
   private String frg_nr;
   @JsonProperty("iRT")
   private Double iRT;
   @JsonProperty("uniprot_id")
   private String uniprot_id;
   private String score;
   private String decoy;
   @JsonProperty("prec_y")
   private String prec_y;
   private String confidence;
   private String shared;
   @JsonProperty("N")
   private String n;
   private String rank;
   private String mods;
   private String nterm;
   private String cterm;
   @JsonProperty("RT Source")
   private String RT_source;
   private Double RT_observed;
   private Double RT_predicted;
   private Double RT_delta;
   
   public Double getQ1() {
      return q1;
   }

   public void setQ1(Double Q1) {
      this.q1 = Q1;
   }

   public Double getQ3() {
      return q3;
   }

   public void setQ3(Double Q3) {
      this.q3 = Q3;
   }

   public Double getRT_detected() {
      return RT_detected;
   }

   public void setRT_detected(Double RT_detected) {
      this.RT_detected = RT_detected;
   }

   public String getProtein_name() {
      return protein_name;
   }

   public void setProtein_name(String protein_name) {
      this.protein_name = protein_name;
   }

   public String getIsotype() {
      return isotype;
   }

   public void setIsotype(String isotype) {
      this.isotype = isotype;
   }

   public Double getRelative_intensity() {
      return relative_intensity;
   }

   public void setRelative_intensity(Double relative_intensity) {
      this.relative_intensity = relative_intensity;
   }

   public String getStripped_sequence() {
      return stripped_sequence;
   }

   public void setStripped_sequence(String stripped_sequence) {
      this.stripped_sequence = stripped_sequence;
   }

   public String getModification_sequence() {
      return modification_sequence;
   }

   public void setModification_sequence(String modification_sequence) {
      this.modification_sequence = modification_sequence;
   }

   public String getPrec_z() {
      return prec_z;
   }

   public void setPrec_z(String prec_z) {
      this.prec_z = prec_z;
   }

   public String getFrg_type() {
      return frg_type;
   }

   public void setFrg_type(String frg_type) {
      this.frg_type = frg_type;
   }

   public String getFrg_z() {
      return frg_z;
   }

   public void setFrg_z(String frg_z) {
      this.frg_z = frg_z;
   }

   public String getFrg_nr() {
      return frg_nr;
   }

   public void setFrg_nr(String frg_nr) {
      this.frg_nr = frg_nr;
   }

   public Double getiRT() {
      return iRT;
   }

   public void setiRT(Double iRT) {
      this.iRT = iRT;
   }

   public String getUniprot_id() {
      return uniprot_id;
   }

   public void setUniprot_id(String uniprot_id) {
      this.uniprot_id = uniprot_id;
   }

   public String getScore() {
      return score;
   }

   public void setScore(String score) {
      this.score = score;
   }

   public String getDecoy() {
      return decoy;
   }

   public void setDecoy(String decoy) {
      this.decoy = decoy;
   }

   public String getPrec_y() {
      return prec_y;
   }

   public void setPrec_y(String prec_y) {
      this.prec_y = prec_y;
   }

   public String getConfidence() {
      return confidence;
   }

   public void setConfidence(String confidence) {
      this.confidence = confidence;
   }

   public String getShared() {
      return shared;
   }

   public void setShared(String shared) {
      this.shared = shared;
   }

   public String getN() {
      return n;
   }

   public void setN(String n) {
      this.n = n;
   }

   public String getRank() {
      return rank;
   }

   public void setRank(String rank) {
      this.rank = rank;
   }

   public String getMods() {
      return mods;
   }

   public void setMods(String mods) {
      this.mods = mods;
   }

   public String getNterm() {
      return nterm;
   }

   public void setNterm(String nterm) {
      this.nterm = nterm;
   }

   public String getCterm() {
      return cterm;
   }

   public void setCterm(String cterm) {
      this.cterm = cterm;
   }

   public Double getRT_observed() {
      return RT_observed;
   }

   public void setRT_observed(Double RT_observed) {
      this.RT_observed = RT_observed;
   }
   
      public Double getRT_delta() {
      return RT_delta;
   }

   public void setRT_delta(Double RT_delta) {
      this.RT_delta = RT_delta;
   }

   public Double getRT_predicted() {
      return RT_predicted;
   }

   public void setRT_predicted(Double RT_predicted) {
      this.RT_predicted = RT_predicted;
   }

}
