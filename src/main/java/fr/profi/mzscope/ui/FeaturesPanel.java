package fr.profi.mzscope.ui;

import fr.profi.mzdb.model.Feature;
import fr.profi.mzscope.util.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * panel that contains the features/peaks table
 *
 * @author CB205360
 */
public class FeaturesPanel extends JPanel implements RowSorterListener {

    final private static Logger logger = LoggerFactory.getLogger(FeaturesPanel.class);

    private IRawFilePlot rawFilePanel;
    private List<Feature> features = new ArrayList<Feature>();
    private int modelSelectedIdxBeforeSort = -1;

    private JXTable featureTable;
    private FeaturesTableModel featureTableModel;
    private JScrollPane jScrollPane;

    public FeaturesPanel() {
        initComponents();
    }

    public FeaturesPanel(IRawFilePlot rawFilePanel) {
        this.rawFilePanel = rawFilePanel;
        initComponents();
        
        TableColumnModel columnModel = featureTable.getColumnModel();
        for (int k = 0; k < columnModel.getColumnCount(); k++) {
            switch (FeaturesTableModel.Columns.values()[k]) {
                case MZ_COL:
                    columnModel.getColumn(k).setCellRenderer(new DefaultTableRenderer(new NumberFormatter("#.0000"), JLabel.RIGHT));
                    break;
                case ET_COL:
                case DURATION_COL:
                    columnModel.getColumn(k).setCellRenderer(new DefaultTableRenderer(new NumberFormatter("#0.00"), JLabel.RIGHT));
                    break;
                case APEX_INT_COL:
                case AREA_COL:
                    columnModel.getColumn(k).setCellRenderer(new DefaultTableRenderer(new NumberFormatter("#,###,###"), JLabel.RIGHT));
                    break;
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        featureTableModel = new FeaturesTableModel();
        jScrollPane = new JScrollPane();
        featureTable = new JXTable();

        featureTable.setModel(featureTableModel);
        featureTable.setColumnControlVisible(true);
        featureTable.setEditable(false);
        featureTable.setShowGrid(true);
        featureTable.setShowHorizontalLines(false);
        featureTable.addHighlighter(HighlighterFactory.createSimpleStriping());
        featureTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                featureTableMouseClicked(evt);
            }
        });
        featureTable.getRowSorter().addRowSorterListener(this);
        
        jScrollPane.setViewportView(featureTable);
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    public void setFeatures(List<Feature> features) {
        featureTableModel.setFeatures(features);
        this.features = features;
    }

    private void featureTableMouseClicked(MouseEvent evt) {
        if ((features != null) && (!features.isEmpty()) && (rawFilePanel != null)
                && (evt.getClickCount() == 2) && (featureTable.getSelectedRow() != -1)) {
            Feature f = features.get(featureTable.convertRowIndexToModel(featureTable.getSelectedRow()));
            rawFilePanel.displayFeature(f);
        }
    }

    @Override
    public void sorterChanged(RowSorterEvent e) {
        if (featureTable.getSelectedRow() == -1) {
            return;
        }
        if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
            modelSelectedIdxBeforeSort = featureTable.convertRowIndexToModel(featureTable.getSelectedRow());
        } else if (modelSelectedIdxBeforeSort != -1) {
            int idx = featureTable.convertRowIndexToView(modelSelectedIdxBeforeSort);
            featureTable.scrollRectToVisible(new Rectangle(featureTable.getCellRect(idx, 0, true)));
        }
    }
}
