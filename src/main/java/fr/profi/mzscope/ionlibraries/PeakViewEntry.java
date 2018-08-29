/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ionlibraries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakViewEntry implements IonEntry {

   final private static Logger logger = LoggerFactory.getLogger(PeakViewEntry.class);
   
   // For strange reasons, first letter must not be Capitalized to be matched on the property. In turn  
   // this means that written column will be in lowercase 
   final public static CsvSchema schema = CsvSchema.builder()
            .addColumn("q1")
            .addColumn("q3")
            .addColumn("rt_detected", CsvSchema.ColumnType.NUMBER)
            .addColumn("protein_name")
            .addColumn("isotype")
            .addColumn("relative_intensity", CsvSchema.ColumnType.NUMBER)
            .addColumn("stripped_sequence")
            .addColumn("modification_sequence")
            .addColumn("prec_z")
            .addColumn("frg_type")
            .addColumn("frg_z")
            .addColumn("frg_nr")
            .addColumn("iRT", CsvSchema.ColumnType.NUMBER)
            .addColumn("uniprot_id")
            .addColumn("score")
            .addColumn("decoy")
            .addColumn("prec_y")
            .addColumn("confidence")
            .addColumn("shared")
            .addColumn("n")
            .addColumn("rank")
            .addColumn("mods")
            .addColumn("nterm")
            .addColumn("cterm")
            .addColumn("rt_source").build().withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);

   private Double q1;
   private Double q3;
   private Double RT_detected;
   private String protein_name;
   private String isotype;
   private Double relative_intensity;
   private String stripped_sequence;
   private String modification_sequence;
   private Integer prec_z;
   private String frg_type;
   private String frg_z;
   private String frg_nr;
   private Double iRT;
   private String uniprot_id;
   private String score;
   private String decoy;
   private Double prec_y;
   private String confidence;
   private Boolean shared;
   private String n;
   private String rank;
   private String mods;
   private String nterm;
   private String cterm;
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

   public Integer getPrec_z() {
      return prec_z;
   }

   public void setPrec_z(Integer prec_z) {
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

   public Double getInitialRT() {
       return iRT;
   }
   
   public Double getiRT() {
      return iRT;
   }

   public String getUniprot_id() {
      return uniprot_id;
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

   public Double getPrec_y() {
      return prec_y;
   }

   public void setPrec_y(Double prec_y) {
      this.prec_y = prec_y;
   }

   public String getConfidence() {
      return confidence;
   }

   public void setConfidence(String confidence) {
      this.confidence = confidence;
   }

   public Boolean getShared() {
      return shared;
   }

   public void setShared(Boolean shared) {
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

    public CsvSchema getSchema() {
        return schema;
    }

    public String getRT_source() {
        return RT_source;
    }

    public void setRT_source(String RT_source) {
        this.RT_source = RT_source;
    }
   
}
