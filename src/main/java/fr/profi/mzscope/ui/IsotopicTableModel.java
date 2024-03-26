package fr.profi.mzscope.ui;

import fr.profi.ms.model.TheoreticalIsotopePattern;
import fr.proline.studio.extendedtablemodel.ExtraDataType;
import fr.proline.studio.extendedtablemodel.GlobalTableModelInterface;
import fr.proline.studio.table.AbstractNonLazyTableModel;
import fr.proline.studio.table.Column;
import fr.proline.studio.table.LazyData;
import fr.proline.studio.table.TableDefaultRendererManager;
import fr.proline.studio.table.renderer.BigFloatOrDoubleRenderer;
import fr.proline.studio.table.renderer.DefaultAlignRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;

public class IsotopicTableModel extends AbstractNonLazyTableModel implements GlobalTableModelInterface {

  private static TableCellRenderer FLOAT_2DIGITS = new BigFloatOrDoubleRenderer( new DefaultAlignRenderer(TableDefaultRendererManager.getDefaultRenderer(String.class), JLabel.RIGHT), 2 );

  public static final Column COLTYPE_FEATURE_INDEX = new Column("index", "index", Integer.class, 0);
  public static final Column COLTYPE_FEATURE_MZCOL = new Column("m/z", "m/z", Double.class, 1, Column.DOUBLE_5DIGITS_RIGHT);
  public static final Column COLTYPE_FEATURE_INTENSITY_COL = new Column("Int.", "Intensity", Float.class, 2, FLOAT_2DIGITS);
  private TheoreticalIsotopePattern m_isotopicPattern = null;

  @Override
  public int[] getKeysColumn() {
    int[] keys = {COLTYPE_FEATURE_INDEX.getIndex(), COLTYPE_FEATURE_MZCOL.getIndex(), COLTYPE_FEATURE_INTENSITY_COL.getIndex()};
    return keys;
  }

  @Override
  public int getInfoColumn() {
    return COLTYPE_FEATURE_INDEX.getIndex();
  }

  @Override
  public ArrayList<ExtraDataType> getExtraDataTypes() {
    return null;
  }

  @Override
  public String getTootlTipValue(int row, int col) {
    return null;
  }

  @Override
  public int getRowCount() {

    return (m_isotopicPattern == null) ? 0 : m_isotopicPattern.isotopeCount();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == COLTYPE_FEATURE_INDEX.getIndex()) {
      return rowIndex;
    } else if (columnIndex == COLTYPE_FEATURE_INTENSITY_COL.getIndex()) {
      return m_isotopicPattern.abundances()[rowIndex];
    } else if (columnIndex == COLTYPE_FEATURE_MZCOL.getIndex()) {
      return m_isotopicPattern.mzAbundancePairs()[rowIndex]._1;
    }

    return null;
  }


  @Override
  public Object getValue(Class c) {
    return getSingleValue(c);
  }

  @Override
  public Object getRowValue(Class c, int row) {
    return null;
  }

  @Override
  public Object getColValue(Class c, int col) {
    return null;
  }


  @Override
  public GlobalTableModelInterface getFrozzenModel() {
    return this;
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
  public Long getTaskId() {
    return -1L;
  }

  @Override
  public LazyData getLazyData(int row, int col) {
    return null;
  }

  @Override
  public void givePriorityTo(Long taskId, int row, int col) {

  }

  @Override
  public void sortingChanged(int col) {

  }

  @Override
  public int getSubTaskId(int col) {
    return 0;
  }

  public void setPattern(TheoreticalIsotopePattern pattern) {
    m_isotopicPattern = pattern;
    fireTableStructureChanged();
  }

  public TheoreticalIsotopePattern getIsotopicPattern() {
    return m_isotopicPattern;
  }

  public double[] getMasses() {
    if (m_isotopicPattern != null) {
      double[] masses = new double[m_isotopicPattern.isotopeCount()];
      for (int k = 0; k < masses.length; k++) {
        masses[k] = ((Double)m_isotopicPattern.mzAbundancePairs()[k]._1).doubleValue();
      }
      return masses;
    }
    return new double[0];
  }

  public float[] getAbundances() {
    if (m_isotopicPattern != null) {
      float[] abundances = new float[m_isotopicPattern.isotopeCount()];
      for (int k = 0; k < abundances.length; k++) {
        abundances[k] = ((Float)m_isotopicPattern.mzAbundancePairs()[k]._2).floatValue();
      }
      return abundances;
    }
    return new float[0];
  }
}
