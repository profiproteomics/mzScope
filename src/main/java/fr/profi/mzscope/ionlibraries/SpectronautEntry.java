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
public class SpectronautEntry implements IonEntry {

   final private static Logger logger = LoggerFactory.getLogger(SpectronautEntry.class);
   
   final public static CsvSchema schema = CsvSchema.builder()
            .addColumn("q1")
            .addColumn("q3")
            .addColumn("rt_detected", CsvSchema.ColumnType.NUMBER)
            .addColumn("proteinId")
            .addColumn("initialRT")
            .addColumn("fasta_name")
            .addColumn("relative_intensity", CsvSchema.ColumnType.NUMBER)
            .addColumn("stripped_sequence")
            .addColumn("modification_sequence")
            .addColumn("prec_z")
            .addColumn("frg_type")
            .addColumn("frg_loss_type")
            .addColumn("frg_z")
            .addColumn("frg_nr")
            .addColumn("uniprot_id")
            .addColumn("q1_abundance")
            .addColumn("shared")
            .addColumn("is_specific")
            .addColumn("rt_source").build().withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);

   private Double q1;
   private Double q3;
   private Double RT_detected;
   private String ProteinId;
   private String fasta_name;
   private String uniprot_id;
   private Double Q1_abundance;
   private Double relative_intensity;
   private String stripped_sequence;
   private String modification_sequence;
   private Integer prec_z;
   private String frg_type;
   private String frg_loss_type;
   private String frg_z;
   private String frg_nr;
   private Boolean shared;
   private String is_specific;
   private String RT_source;
   
   private Double RT_observed;
   private Double RT_predicted;
   private Double RT_delta;
   private Double initialRT = null;
   
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
      return ProteinId;
   }

   public void setProtein_name(String protein_name) {
      this.ProteinId = protein_name;
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

   public String getUniprot_id() {
      return uniprot_id;
   }

   public void setUniprot_id(String uniprot_id) {
      this.uniprot_id = uniprot_id;
   }

   public Boolean getShared() {
      return shared;
   }

   public void setShared(Boolean shared) {
      this.shared = shared;
   }

    public String getProteinId() {
        return ProteinId;
    }

    public void setProteinId(String ProteinId) {
        this.ProteinId = ProteinId;
    }

    public String getFasta_name() {
        return fasta_name;
    }

    public void setFasta_name(String fasta_name) {
        this.fasta_name = fasta_name;
    }

    public Double getQ1_abundance() {
        return Q1_abundance;
    }

    public void setQ1_abundance(Double Q1_abundance) {
        this.Q1_abundance = Q1_abundance;
    }

    public String getFrg_loss_type() {
        return frg_loss_type;
    }

    public void setFrg_loss_type(String frg_loss_type) {
        this.frg_loss_type = frg_loss_type;
    }

    public String getIs_specific() {
        return is_specific;
    }

    public void setIs_specific(String is_specific) {
        this.is_specific = is_specific;
    }

    public String getRT_source() {
        return RT_source;
    }

    public void setRT_source(String RT_source) {
        this.RT_source = RT_source;
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

    @Override
    public Double getInitialRT() {
        if (initialRT == null) {
            initialRT = getRT_detected();
        }
        return initialRT;
    }

    @Override
    public String getScore() {
        return "Not supported yet";
    }

    @Override
    public Double getPrec_y() {
        return getQ1_abundance();
    }
   
}
