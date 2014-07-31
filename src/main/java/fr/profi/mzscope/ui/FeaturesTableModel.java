/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ui;

import fr.profi.mzdb.model.Feature;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author CB205360
 */
public class FeaturesTableModel extends AbstractTableModel {
    
    private static final int MZ_COL = 0;
    private static final int ET_COL = 1;
    private static final int AREA_COL = 2;
    
    private List<Feature> features = new ArrayList<Feature>();

    public void setFeatures(List<Feature> features) {
        this.features = features;
        fireTableDataChanged();
    }

    public int getRowCount() {
        return features.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case MZ_COL: return features.get(rowIndex).getMz();
            case ET_COL: return features.get(rowIndex).getElutionTime()/60.0;
            case AREA_COL: return features.get(rowIndex).getArea();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case MZ_COL: return "m/z";
            case ET_COL: return "elution";
            case AREA_COL: return "area";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
         switch(columnIndex) {
            case MZ_COL: return Double.class;
            case ET_COL: return Float.class;
            case AREA_COL: return Float.class;
        }
        return Object.class;
    }
    
    
    
}
