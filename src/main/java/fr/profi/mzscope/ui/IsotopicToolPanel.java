package fr.profi.mzscope.ui;

import com.compomics.util.general.IsotopicDistribution;
import fr.profi.chemistry.algo.AveragineComputer;
import fr.profi.chemistry.algo.MassComputer;
import fr.profi.chemistry.algo.MassPrecision$;
import fr.profi.chemistry.model.*;
import fr.profi.ms.algo.IsotopePatternEstimator;
import fr.profi.ms.algo.IsotopePatternEstimator$;
import fr.profi.ms.model.TheoreticalIsotopePattern;
import fr.profi.mzdb.model.DataMode;
import fr.proline.mzscope.model.Spectrum;
import fr.proline.mzscope.processing.SpectrumUtils;
import fr.proline.mzscope.ui.IMzScopeController;
import fr.proline.mzscope.ui.IRawFileViewer;
import fr.proline.mzscope.ui.MzScopePanel;
import fr.proline.mzscope.ui.model.ScanTableModel;
import fr.proline.studio.corewrapper.util.PeptideClassesUtils;
import fr.proline.studio.graphics.BasePlotPanel;
import fr.proline.studio.graphics.PlotLinear;
import fr.proline.studio.table.DecoratedMarkerTable;
import fr.proline.studio.table.TablePopupMenu;
import fr.proline.studio.utils.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.collection.mutable.HashMap;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IsotopicToolPanel extends JPanel {

  private static final Logger logger = LoggerFactory.getLogger(IsotopicToolPanel.class);
  private static final BiomoleculeAtomTable$ atomTable = BiomoleculeAtomTable$.MODULE$;
  private static final ProteinogenicAminoAcidTable$ aaTable = ProteinogenicAminoAcidTable$.MODULE$;

  private final IMzScopeController appController;
  protected BasePlotPanel m_graphPlot;
  protected JTextField m_mozTF;
  private IsotopicTableModel m_isotopicTableModel;
  private JComboBox<Integer> m_chargeCB;
  private JSlider m_mzSlider;
  private JRadioButton m_averagineBtn;
  private JRadioButton m_binomialBtn;

  private JTextField m_CTF;
  private JTextField m_HTF;
  private JTextField m_NTF;
  private JTextField m_OTF;
  private JTextField m_STF;

  private double m_fwhm = 0.01;

  private MassComputer massComputer = new fr.profi.chemistry.algo.MassComputer(aaTable, MassPrecision$.MODULE$.MONOISOTOPIC());

  public IsotopicToolPanel(MzScopePanel mzScopePanel) {
    this.appController = mzScopePanel;
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());

    JToolBar toolbar = initToolbar();

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new java.awt.Insets(5, 5, 5, 5);

    // Mz and charge state
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    mainPanel.add(new JLabel("m/z:"),c);

    c.gridx++;
    c.weightx = 1;
    m_mozTF = new JTextField(10);
    mainPanel.add(m_mozTF,c);
    m_mozTF.addActionListener(e -> {
      updateAtomComposition();
      computeIsotopicDist();
    });

    c.gridx++;
    c.weightx = 0;
    mainPanel.add(new JLabel("charge:"),c);

    c.gridx++;
    c.weightx = 1;
    m_chargeCB = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
    mainPanel.add(m_chargeCB,c);
    m_chargeCB.addActionListener(e -> {
      updateAtomComposition();
      computeIsotopicDist();
    });

    // mz slider
    c.gridx = 0;
    c.gridy++;
    c.gridwidth = 4;
    m_mzSlider = new JSlider(300, 3000);
    mainPanel.add(m_mzSlider, c);
    m_mzSlider.addChangeListener(e -> {
              m_mozTF.setText(Double.toString(m_mzSlider.getValue()));
              updateAtomComposition();
              computeIsotopicDist();
            }
    );

    c.gridx = 0;
    c.gridy++;
    c.weightx = 1;
    c.gridwidth = 4;
    mainPanel.add(getFormulaPanel(), c);

    c.gridx = 0;
    c.gridy++;
    c.gridwidth = 2;
    c.weightx = 0;
    m_averagineBtn = new JRadioButton("Averagine");
    m_averagineBtn.setSelected(true);
    m_averagineBtn.addActionListener(e -> computeIsotopicDist());
    mainPanel.add(m_averagineBtn, c);

    c.gridx+=2;
    m_binomialBtn = new JRadioButton("Binomial");
    m_binomialBtn.addActionListener(e -> computeIsotopicDist());
    mainPanel.add(m_binomialBtn, c);

    ButtonGroup group = new ButtonGroup();
    group.add(m_averagineBtn);
    group.add(m_binomialBtn);


    DecoratedMarkerTable m_table = new DecoratedMarkerTable() {
      @Override
      public void addTableModelListener(TableModelListener l) {
        getModel().addTableModelListener(l);
      }

      @Override
      public TablePopupMenu initPopupMenu() {
        return null;
      }

      @Override
      public void prepostPopupMenu() {

      }
    };
    m_isotopicTableModel = new IsotopicTableModel();
    m_table.setModel(m_isotopicTableModel);
    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setViewportView(m_table);
    m_table.setFillsViewportHeight(true);
    m_table.setViewport(jScrollPane.getViewport());

    c.gridx = 0;
    c.gridy++;
    c.gridwidth = 4;
    c.weightx = 1;
    c.weighty = 1;

    mainPanel.add(jScrollPane, c);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(toolbar, BorderLayout.WEST);
    leftPanel.add(mainPanel, BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(leftPanel);
    m_graphPlot = new BasePlotPanel();
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new GridBagLayout());

    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 1;
    rightPanel.add(new JLabel("fwhm:"),c);
    JSlider fwhmSlider = new JSlider(0, 200);
    fwhmSlider.setValue((int)(m_fwhm*1000));
    fwhmSlider.addChangeListener(e -> {
      m_fwhm = ((JSlider)e.getSource()).getValue()/1000.0;
      updatePatternViewer();
    });
    c.gridx++;
    c.weightx = 1;
    rightPanel.add(fwhmSlider,c);
    c.gridy++;
    c.gridx = 0;
    c.weighty = 1;
    c.gridwidth = 2;
    rightPanel.add(m_graphPlot, c);
    splitPane.setRightComponent(rightPanel);

    this.add(splitPane, BorderLayout.CENTER);

  }

  private JPanel getFormulaPanel() {
    JPanel formulaPanel = new JPanel();
    formulaPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new java.awt.Insets(5, 5, 5, 5);
    c.gridy = 0;
    c.gridx = 0;
    formulaPanel.add(new JLabel("C"),c);
    c.gridx++;
    m_CTF = new JTextField(3);
    m_CTF.addActionListener(e -> {
      computeMozFromFormula();
      computeIsotopicDist();
    });
    formulaPanel.add(m_CTF, c);

    c.gridx++;
    formulaPanel.add(new JLabel("H"),c);
    c.gridx++;
    m_HTF = new JTextField(3);
    formulaPanel.add(m_HTF, c);

    c.gridx++;
    formulaPanel.add(new JLabel("N"),c);
    c.gridx++;
    m_NTF = new JTextField(3);
    formulaPanel.add(m_NTF, c);

    c.gridx++;
    formulaPanel.add(new JLabel("O"),c);
    c.gridx++;
    m_OTF = new JTextField(3);
    formulaPanel.add(m_OTF, c);

    c.gridx++;
    formulaPanel.add(new JLabel("S"),c);
    c.gridx++;
    m_STF = new JTextField(3);
    formulaPanel.add(m_STF, c);

    return formulaPanel;
  }

  private void computeMozFromFormula() {
    double monoMass = getMonoMassFromFormula();
    int charge = ((Integer)m_chargeCB.getSelectedItem()).intValue();
    m_mozTF.setText(Double.toString(massToMz(monoMass, charge)));
  }

  private double getMonoMassFromMzField() {
    int charge = ((Integer)m_chargeCB.getSelectedItem()).intValue();
    double mz = Double.parseDouble(m_mozTF.getText());
    return mzToMass(mz, charge);
  }

  private double getMonoMassFromFormula() {
    int cAb = m_CTF.getText().length() > 0 ? Integer.parseInt(m_CTF.getText()) : 0;
    int hAb = m_HTF.getText().length() > 0 ? Integer.parseInt(m_HTF.getText()) : 0;
    int nAb = m_NTF.getText().length() > 0 ? Integer.parseInt(m_NTF.getText()) : 0;
    int oAb = m_OTF.getText().length() > 0 ? Integer.parseInt(m_OTF.getText()) : 0;
    int sAb = m_STF.getText().length() > 0 ? Integer.parseInt(m_STF.getText()) : 0;

    double monoMass = atomTable.getAtom("C").monoMass()* cAb +
                  atomTable.getAtom("H").monoMass()* hAb +
                  atomTable.getAtom("N").monoMass()* nAb +
                  atomTable.getAtom("O").monoMass()* oAb +
                  atomTable.getAtom("S").monoMass()* sAb;
    return monoMass;
  }

  private double mzToMass(double mz, int charge) {
    return mz*charge - charge* PeptideClassesUtils.PROTON_MASS;
  }

  private double massToMz(double monoMass, int charge) {
    return (monoMass + charge * PeptideClassesUtils.PROTON_MASS)/charge;
  }

  private void updateAtomComposition() {

    if (m_mozTF.getText() != null && !m_mozTF.getText().isEmpty()) {

      int charge = ((Integer) m_chargeCB.getSelectedItem()).intValue();
      double mz = Double.parseDouble(m_mozTF.getText());

      AveragineComputer computer = new fr.profi.chemistry.algo.AveragineComputer(HumanAminoAcidTable$.MODULE$, atomTable);
      AtomComposition composition = computer.computeAveragine(mzToMass(mz, charge), false, 0.01f)._1;
      composition.roundAbundances();

      displayAtomComposition(composition);

//      logger.trace("Formula = {}, mass = {}", composition.toFormula(), composition.getMonoMass());
    }
  }

  private void displayAtomComposition(AtomComposition composition) {

    final HashMap<Atom, Object> atoms = composition.abundanceMap();

    int cAb = atoms.get(atomTable.getAtom("C")).isDefined() ? ((Float) atoms.get(atomTable.getAtom("C")).get()).intValue() : 0;
    m_CTF.setText(Integer.toString(cAb));

    int hAb = atoms.get(atomTable.getAtom("H")).isDefined() ? ((Float) atoms.get(atomTable.getAtom("H")).get()).intValue() : 0;
    m_HTF.setText(Integer.toString(hAb));

    int nAb =atoms.get(atomTable.getAtom("N")).isDefined() ? ((Float) atoms.get(atomTable.getAtom("N")).get()).intValue() : 0;
    m_NTF.setText(Integer.toString(nAb));

    int oAb = atoms.get(atomTable.getAtom("O")).isDefined() ? ((Float) atoms.get(atomTable.getAtom("O")).get()).intValue() : 0;
    m_OTF.setText(Integer.toString(oAb));

    int sAb = (atoms.get(atomTable.getAtom("S")).isDefined()) ? ((Float) atoms.get(atomTable.getAtom("S")).get()).intValue() : 0;
    m_STF.setText(Integer.toString(sAb));
  }

  private void computeIsotopicDist() {
    if (m_mozTF.getText() != null && !m_mozTF.getText().isEmpty()) {
      if (m_averagineBtn.isSelected()) {

        double moz = Double.parseDouble(m_mozTF.getText());
        int charge = ((Integer) m_chargeCB.getSelectedItem()).intValue();
        TheoreticalIsotopePattern pattern = IsotopePatternEstimator.getTheoreticalPattern(moz, charge);
        m_isotopicTableModel.setPattern(pattern);

      } else {

        int cAb = m_CTF.getText().length() > 0 ? Integer.parseInt(m_CTF.getText()) : 0;
        int hAb = m_HTF.getText().length() > 0 ? Integer.parseInt(m_HTF.getText()) : 0;
        int nAb = m_NTF.getText().length() > 0 ? Integer.parseInt(m_NTF.getText()) : 0;
        int oAb = m_OTF.getText().length() > 0 ? Integer.parseInt(m_OTF.getText()) : 0;
        int sAb = m_STF.getText().length() > 0 ? Integer.parseInt(m_STF.getText()) : 0;

        int charge = ((Integer) m_chargeCB.getSelectedItem()).intValue();
        double monoMz = Double.parseDouble(m_mozTF.getText()); //massToMz(monoMass, charge);

        IsotopicDistribution dist = new IsotopicDistribution(cAb, nAb, hAb, oAb, sAb);
        dist.calculate();
        Double[] values = dist.getPercTot();
        Double max = Arrays.stream(values).max(Double::compareTo).get();

        List<Tuple2> l = Arrays.stream(values).map(v -> new Tuple2(scala.Double.box(0.0), scala.Float.box((float) (100 * v / max)))).collect(Collectors.toList());
        Tuple2<Object, Object>[] ab = new Tuple2[l.size()];
        for (int k = 0; k < l.size(); k++) {
          ab[k] = new Tuple2(monoMz + k * IsotopePatternEstimator$.MODULE$.avgIsoMassDiff() / charge, l.get(k)._2);
        }

        TheoreticalIsotopePattern pattern = TheoreticalIsotopePattern.apply(ab, ((Integer) m_chargeCB.getSelectedItem()).intValue());
        m_isotopicTableModel.setPattern(pattern);
      }
      updatePatternViewer();
    }
  }

  protected JToolBar initToolbar() {
    JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
    toolbar.setFloatable(false);

    JButton displayPatternBtn = new JButton();
    displayPatternBtn.setIcon(IconManager.getIcon(IconManager.IconType.CENTROID_SPECTRA));
    displayPatternBtn.setToolTipText("Display pattern in current spectrum");
    displayPatternBtn.addActionListener(e -> displayIsotopicPattern());

    toolbar.add(displayPatternBtn);

    JButton aaSeqBtn = new JButton();
    aaSeqBtn.setIcon(IconManager.getIcon(IconManager.IconType.QUESTION));
    aaSeqBtn.setToolTipText("Enter Amino Acid sequence");
    aaSeqBtn.addActionListener(e -> enterAASequence());

    toolbar.add(aaSeqBtn);

    return toolbar;
  }

  private void enterAASequence() {
    String sequence = (String)JOptionPane.showInputDialog(
            null,
            "AA sequence",
            "Amino Acid Sequence",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "MENHIR"
    );
    if(sequence != null && sequence.length() > 0){
      double mass = massComputer.computeMass(sequence);
      int charge = ((Integer)m_chargeCB.getSelectedItem()).intValue();
      m_mozTF.setText(Double.toString(massToMz(mass, charge)));

      AminoAcidComposition aaCompo = new AminoAcidComposition(sequence, aaTable);
      displayAtomComposition(aaCompo.getAtomComposition(atomTable));
      computeIsotopicDist();

    }

  }

  private void displayIsotopicPattern() {
    IRawFileViewer viewer = appController.getCurrentRawFileViewer();

    final Spectrum currentSpectrum = viewer.getCurrentSpectrum();
    int maxTheoreticalPeakIndex = m_isotopicTableModel.getIsotopicPattern().theoreticalMaxPeakelIndex();
    int index = SpectrumUtils.getNearestPeakIndex(currentSpectrum.getMasses(), m_isotopicTableModel.getMasses()[maxTheoreticalPeakIndex]);
    double maxCurrentIntensity = currentSpectrum.getIntensities()[index];

    Spectrum s = SpectrumUtils.buildSpectrum(m_isotopicTableModel.getMasses(), m_isotopicTableModel.getAbundances(), m_fwhm, DataMode.FITTED);
    viewer.setReferenceSpectrum(s, (float) (maxCurrentIntensity/m_isotopicTableModel.getAbundances()[maxTheoreticalPeakIndex]));
  }


  private void updatePatternViewer() {
      m_graphPlot.clearPlots();
      float maxY = 0.0f;
      int index = 0;
      Spectrum spectrum = SpectrumUtils.buildSpectrum(m_isotopicTableModel.getMasses(), m_isotopicTableModel.getAbundances(), m_fwhm, DataMode.FITTED);
      ScanTableModel scanModel = new ScanTableModel(spectrum);
      PlotLinear plot = new PlotLinear(m_graphPlot, scanModel, null, ScanTableModel.COLTYPE_SCAN_MASS, ScanTableModel.COLTYPE_SCAN_INTENSITIES);
      ((PlotLinear) plot).setStrokeFixed(true);
      ((PlotLinear) plot).setPlotInformation(scanModel.getPlotInformation());
//        PeakelWrapper wrapper = new PeakelWrapper(p, index++);
//        PlotLinear plot = new PlotLinear(m_graphPlot, wrapper, null, 0, 1);
//        plot.setPlotInformation(wrapper.getPlotInformation());
        m_graphPlot.setPlot(plot);

      m_graphPlot.getPlots().get(0).clearMarkers();


      m_graphPlot.getYAxis().lockMinValue(0.0);
      m_graphPlot.getYAxis().setRange(0, 110.0);
      m_graphPlot.repaint();
  }

}

