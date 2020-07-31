/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ionlibraries;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class IonLibrary {

    final private static Logger logger = LoggerFactory.getLogger(IonLibrary.class);
    
    private final List<IonEntry> entries;
    private final List<IonEntry> nonRedondantEntries;
    private final Map<String, List<IonEntry>> entriesBySequence = new HashMap<>();

    public static class Peptide {
        
        private IonEntry prototype;
        
        public Peptide(IonEntry ionPrototype) {
            this.prototype = ionPrototype;
        }
        
        public Double getQ1() {
            return prototype.getQ1();
        }
        
        public String getSequence() {
            return prototype.getModification_sequence();
        }

        public Double getIntensity() {
            return prototype.getPrec_y();
        }

        public Integer getCharge() {
            return prototype.getPrec_z();
        }
        
        public Boolean getShared() {
            return prototype.getShared();
        }
        
        public String getProtein_name() {
            return prototype.getProtein_name();
        }
        
        public Double getRT_detected() {
            return prototype.getRT_detected();
        }
    }
    
    public static IonLibrary fromFile(File file, IonEntry prototypeEntry) {
        List<IonEntry> entries = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
            CsvMapper mapper = new CsvMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true); 
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator('\t');
            MappingIterator<IonEntry> it = mapper.readerFor(prototypeEntry.getClass()).with(schema).readValues(fReader);
            while (it.hasNext()) {
                IonEntry row = it.next();
                entries.add(row);
            }
        } catch (Exception ex) {
            logger.error("Error while parsing CSV file", ex);
        }

        return new IonLibrary(entries);
    }

    private IonLibrary(List<IonEntry> entries) {
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

    public List<IonEntry> getEntries() {
        return entries;
    }

    public List<IonEntry> getNonRedondantEntries() {
        return nonRedondantEntries;
    }

    public List<IonEntry> getIonEntryBySequence(String sequence) {
        return entriesBySequence.get(sequence);
    }

    public void saveToFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        logger.info("Start writing ion library to " + file.getAbsolutePath());
        CsvMapper mapper = new CsvMapper();
        mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        mapper.writer(entries.get(0).getSchema()).writeValues(file).writeAll(entries.toArray(new IonEntry[0]));
        writer.flush();
        writer.close();
        
        logger.info("Library writed");
    }
}
