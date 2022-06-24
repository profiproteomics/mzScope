/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import com.thoughtworks.xstream.XStream;
import fr.proline.mzscope.model.IChromatogram;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.model.MsnExtractionRequest;
import fr.proline.mzscope.ui.RawFileManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author VD225637
 */
public class ChromatogramsExporter {
     final private static Logger logger = LoggerFactory.getLogger(ChromatogramsExporter.class);

        
     private List<IonProperties> ions2Xtract = new ArrayList<>();
     private String m_outputDir;

     public ChromatogramsExporter(List<IonProperties> ions, String outputDir){
         ions2Xtract = ions;
         m_outputDir= outputDir;
     }

     public boolean extractChromatoForAllFiles(){
        DecimalFormat df = new DecimalFormat("#.00");
        List<IRawFile> allFiles = RawFileManager.getInstance().getAllFiles();
        int nbrError = 0;
        for(IRawFile nextRawFile : allFiles){
            for(IonProperties nextIon : ions2Xtract ){
                BufferedWriter output = null;
                String chromatoFile = "";
                try {
                    MsnExtractionRequest.Builder builder = MsnExtractionRequest.builder().setMinMz(nextIon.getMinMz()).setMaxMz(nextIon.getMaxMz());
                    if((nextIon.startRT > 0.0) && (nextIon.stopRT > 0.0))
                       builder.setElutionTimeLowerBound(nextIon.startRT*60.0f).setElutionTimeUpperBound(nextIon.stopRT*60.0f);
                    IChromatogram currentChromatogram = nextRawFile.getXIC(builder.build());
                    StringBuilder stb = new StringBuilder();
                    stb.append(m_outputDir).append('/');
                    stb.append("extracted_xic_").append(nextRawFile.getName()).append(df.format(currentChromatogram.getMinMz()).replace(',', '.')).append(".tsv");
                    chromatoFile = stb.toString();
                    logger.info("Extracting chromatogram to file "+chromatoFile);
                    File file = new File(chromatoFile);
                    output = new BufferedWriter(new FileWriter(file));
                    output.write("index\trt\tintensity\n");
                    for (int k = 0; k < currentChromatogram.getTime().length; k++) {
                        stb = new StringBuilder();
                        stb.append(k).append("\t").append(currentChromatogram.getTime()[k]).append("\t").append(currentChromatogram.getIntensities()[k]);
                        stb.append("\n");
                        output.write(stb.toString());
                    }
                    logger.info("extracted IChromatogram in " + file.getAbsolutePath());

                } catch (IOException ex) {
                    nbrError++;
                    logger.error("Unable to write IChromatogram "+chromatoFile, ex);
                } finally {
                    try {
                        if(output != null)
                            output.close();
                    } catch (IOException ex) {
                        logger.error("Unable to write current IChromatogram "+chromatoFile, ex);
                    }
                }
            }
        }
        if(nbrError >0 )
            return false;
       return true;
     }
     
        
    public static String usage() {

      return "'extract' (shortcut for ChromatogramsExporter) is a command line chromatogram extraction program . It extracts" +
              " chromatograms from RAW files (as mzData or mzMl) for specified masses and time window.\n" +
              "One chromatogram file is written for each specified raw file and mass.\n\n" +
              "extract options : \n" +
              "\t<raw_path> :\n\t\t file path for single raw file extraction or path to directory containing raw files to treat\n" +
              "\t<properties_filename> : \n\t\t properties file where ions to extract are defined\n" +
              "\t<output_path> : {default= same as input directory}\n\t\t output directory where results are written\n\n" +
              "Properties_filename is a XML file structured as in example file, sample_prop.xml \n";
    }
        
     public static void main(String[] args) {
         if(args.length < 2 ){
            System.out.println( usage() );
            System.exit(-1);            
        }
         
        String filepath = args[0];
        String ionsList = args[1];
        String outputDir = "";
        if(args.length >=3)
            outputDir =args[2];
        
        if(filepath == null ||filepath.isEmpty()){
            ChromatogramsExporter.logger.warn("No path specified ");
            System.exit(-1);
        }
         
        if(ionsList == null ||ionsList.isEmpty()){
            ChromatogramsExporter.logger.warn("No Ions specified ");
            System.exit(-1);
        }

        try {

            File specifiedFile = new File(filepath);
            List<File> rawFiles = new ArrayList<File>();
            if(outputDir.isEmpty()){
                if(specifiedFile.isDirectory()){
                    outputDir = filepath;
                    rawFiles = Arrays.asList(specifiedFile.listFiles());
                } else{ 
                    outputDir = specifiedFile.getParent();
                    rawFiles.add(specifiedFile);
                }
            }
            
            for(File rawFile : rawFiles){
                RawFileManager.getInstance().addRawFile(rawFile);
            }
            
            File ionsPropertiesFile = new File(ionsList);
            XStream xStream = new XStream();
            xStream.alias("ion", IonProperties.class);
            List<IonProperties> ionsToXtract = (List<IonProperties>) xStream.fromXML(ionsPropertiesFile);
            
//            logger.debug("Extract usinf params ");
//            for(IonProperties ion: ionsToXtract){
//               logger.debug(" ion mz {} ppm {}",ion.getMinMz(), ion.ppm); 
//               logger.debug(" TIME min {} max {}",ion.startRT, ion.stopRT);
//            }
           
            ChromatogramsExporter chExporter = new ChromatogramsExporter(ionsToXtract,outputDir);
            chExporter.extractChromatoForAllFiles();
            System.exit(0);
          } catch (Exception e) {
             // TODO Auto-generated catch block
             logger.error("Error while extracting chromatogram ", e);
             System.exit(-1);
          }
                
     }
     
     
     
     static class IonProperties {
         double ppm;
         double ionMz;
         float startRT;
         float stopRT;
         
         
         public IonProperties(double ppm, double ionMz, float startRT, float stopRT){
             this.ppm = ppm;
             this.ionMz = ionMz;
             this.startRT = startRT;
             this.stopRT = stopRT;
         }

        public double getPpm() {
            return ppm;
        }

        public void setPpm(double ppm) {
            this.ppm = ppm;
        }

        public double getIonMz() {
            return ionMz;
        }

        public void setIonMz(double ionMz) {
            this.ionMz = ionMz;
        }

        public float getStartRT() {
            return startRT;
        }

        public void setStartRT(float startRT) {
            this.startRT = startRT;
        }

        public float getStopRT() {
            return stopRT;
        }

        public void setStopRT(float stopRT) {
            this.stopRT = stopRT;
        }
                
        public double getMinMz(){
            return (ionMz - (ionMz * ppm / 1e6));
        }
        
        public double getMaxMz(){
            return (ionMz  + (ionMz * ppm / 1e6));
        }

     }
     
}
