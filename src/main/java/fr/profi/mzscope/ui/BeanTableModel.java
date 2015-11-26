/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.profi.mzscope.ui;

import fr.proline.studio.comparedata.ExtraDataType;
import fr.proline.studio.filter.DoubleFilter;
import fr.proline.studio.filter.Filter;
import fr.proline.studio.filter.IntegerFilter;
import fr.proline.studio.filter.StringFilter;
import fr.proline.studio.graphics.PlotInformation;
import fr.proline.studio.graphics.PlotType;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.table.LazyData;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class BeanTableModel<T> extends AbstractTableModel implements GlobalTableModelInterface {

   final private static Logger logger = LoggerFactory.getLogger(BeanTableModel.class);
   
   private String name;
   private Class<T> typeParameterClass;
   private PropertyDescriptor[] descriptors;
   private List<T> entities = new ArrayList<>();
   
   public BeanTableModel(Class<T> type) {
      try {
         typeParameterClass = type;
         name = type.getName()+" table";
         descriptors = Introspector.getBeanInfo(type, Object.class).getPropertyDescriptors();
      } catch (IntrospectionException ex) {
         logger.info("cannot infer getters from "+type, ex);
      }  
    }

   public void setData(List<T> data) {
      entities = data;
      fireTableDataChanged();
   }
   
   @Override
   public int getRowCount() {
      return entities.size();
   }

   @Override
   public int getColumnCount() {
      return descriptors.length;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         return descriptors[columnIndex].getReadMethod().invoke(entities.get(rowIndex));
      } catch (Exception ex) {
         logger.info("cannot get read method for property "+descriptors[columnIndex].getName(), ex);
      } 
      return null;
   }

   
   @Override
   public String getColumnName(int column) {
      return descriptors[column].getDisplayName();
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return descriptors[columnIndex].getPropertyType();
   }


   @Override
   public String getToolTipForHeader(int column) {
      return descriptors[column].getShortDescription();
   }

   @Override
   public String getTootlTipValue(int row, int col) {
      return null;
   }

   @Override
   public TableCellRenderer getRenderer(int col) {
      return null;
   }

   @Override
   public Long getTaskId() {
      return -1l;
   }

   @Override
   public LazyData getLazyData(int row, int col) {
      return null;
   }

   @Override
   public void givePriorityTo(Long taskId, int row, int col) {   }

   @Override
   public void sortingChanged(int col) {   }

   @Override
   public int getSubTaskId(int col) {
      return -1;
   }

   @Override
   public String getDataColumnIdentifier(int columnIndex) {
      return getColumnName(columnIndex);
   }

   @Override
   public Class getDataColumnClass(int columnIndex) {
      return getColumnClass(columnIndex);
   }

   @Override
   public Object getDataValueAt(int rowIndex, int columnIndex) {
      return getValueAt(rowIndex, columnIndex);
   }

   @Override
   public int[] getKeysColumn() {
      return null;
   }

   @Override
   public int getInfoColumn() {
      return 0;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Map<String, Object> getExternalData() {
      return null;
   }

   @Override
   public PlotInformation getPlotInformation() {
       return null;
   }

   @Override
   public long row2UniqueId(int rowIndex) {
      return rowIndex;
   }

   @Override
   public int uniqueId2Row(long id) {
      return (int)id;
   }

   @Override
   public void addFilters(LinkedHashMap<Integer, Filter> filtersMap) {
      for(int k = 0; k < descriptors.length; k++) {
         switch(descriptors[k].getPropertyType().getSimpleName()) {
            case "String" : filtersMap.put(k, new StringFilter(getColumnName(k), null, k));
               break;
            case "Double" : 
            case "double" : filtersMap.put(k, new DoubleFilter(getColumnName(k), null, k));
               break;
            case "Integer" :
            case "int" : filtersMap.put(k, new IntegerFilter(getColumnName(k), null, k));
               break;
            default: System.out.println("no filter for type : "+descriptors[k].getPropertyType().getSimpleName());
         }
      }
   }

   @Override
   public boolean isLoaded() {
      return true;
   }

   @Override
   public int getLoadingPercentage() {
      return 100;
   }

   @Override
   public PlotType getBestPlotType() {
      return PlotType.SCATTER_PLOT;
   }

   @Override
   public int getBestXAxisColIndex(PlotType plotType) {
      return 0;
   }

   @Override
   public int getBestYAxisColIndex(PlotType plotType) {
      return Math.min(1, descriptors.length - 1);
   }

   @Override
   public String getExportRowCell(int row, int col) {
      return null;
   }

   @Override
   public String getExportColumnName(int col) {
      return getColumnName(col);
   }

   @Override
   public GlobalTableModelInterface getFrozzenModel() {
      return this;
   }

   @Override
   public ArrayList<ExtraDataType> getExtraDataTypes() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public Object getValue(Class c) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public Object getValue(Class c, int row) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void addSingleValue(Object v) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public Object getSingleValue(Class c) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

}
