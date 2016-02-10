/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope;

import com.fasterxml.jackson.core.JsonGenerator;
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
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class IonLibrary {

    final private static Logger logger = LoggerFactory.getLogger(IonLibrary.class);
    final private static CsvSchema schema = CsvSchema.builder()
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
            .addColumn("cterm").build().withColumnSeparator('\t').withoutQuoteChar().withHeader().withUseHeader(true).withNullValue(null);

    private final List<IonEntry> entries;
    private final List<IonEntry> nonRedondantEntries;
    private final Map<String, List<IonEntry>> entriesBySequence = new HashMap<>();
    private final Map<String, List<IonEntry>> entriesByProteinName = new HashMap<>();

    public static class Peptide {
        
        private String sequence;
        private Double q1;
        private Double relative_intensity;
        
        public Peptide(String sequence, Double q1, Double intensity) {
            this.sequence = sequence;
            this.q1 = q1;
            this.relative_intensity = intensity;
        }

        
        public Double getQ1() {
            return q1;
        }

        
        public String getSequence() {
            return sequence;
        }

        public Double getRelative_intensity() {
            return relative_intensity;
        }
        
    }
    
    public static IonLibrary fromFile(File file) {
        List<IonEntry> entries = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader fReader = new BufferedReader(new InputStreamReader(fis));
            CsvMapper mapper = new CsvMapper();
            MappingIterator<IonEntry> it = mapper.reader(schema).forType(IonEntry.class).readValues(fReader);
            while (it.hasNext()) {
                IonEntry row = it.next();
                entries.add(row);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
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
            if (!entriesByProteinName.containsKey(e.getProtein_name())) {
                entriesByProteinName.put(e.getProtein_name(), new ArrayList<IonEntry>(10));
            }

            entriesBySequence.get(e.getModification_sequence()).add(e);
            entriesByProteinName.get(e.getProtein_name()).add(e);
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
        mapper.writer(schema).writeValues(file).writeAll(entries.toArray(new IonEntry[0]));
        writer.flush();
        writer.close();
        logger.info("Library writed");
    }
}
