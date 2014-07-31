package fr.profi.mzscope.model;

public class Scan {
   
   private String title;
   private Integer index;
   private int msLevel;
   private double[] masses;
   private float[] intensities;
   private float retentionTime;
   private double[] peaksMz = null;
   private float[] peaksIntensities = null;
   
   public Scan(Integer index, float rt, double[] masses, float[] intensities, int msLevel) {
      this.index = index;
      this.masses = masses;
      this.intensities = intensities;
      this.retentionTime = rt;
      this.msLevel = msLevel;
   }

   public double[] getMasses() {
      return this.masses;
   }

   public float[] getIntensities() {
      return this.intensities;
   }

   public Integer getIndex() {
      return index;
   }

   public float getRetentionTime() {
      return retentionTime;
   }

   /**
    * @return the peaksMz
    */
   public double[] getPeaksMz() {
      return peaksMz;
   }

   /**
    * @param peaksMz the peaksMz to set
    */
   public void setPeaksMz(double[] peaksMz) {
      this.peaksMz = peaksMz;
   }

   /**
    * @return the peaksIntensities
    */
   public float[] getPeaksIntensities() {
      return peaksIntensities;
   }

   /**
    * @param peaksIntensities the peaksIntensities to set
    */
   public void setPeaksIntensities(float[] peaksIntensities) {
      this.peaksIntensities = peaksIntensities;
   }
   
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public int getMsLevel() {
      return msLevel;
   }

}
