/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.mzscope.ui;

import fr.profi.mzscope.model.IRawFile;
import fr.profi.mzscope.mzdb.MzdbRawFile;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author VD225637
 */
public class RawFileListModel extends  AbstractListModel<Object> {
    private List<IRawFile> rawFiles = new ArrayList<IRawFile>();
    
    public RawFileListModel() {
    }

    public IRawFile[] getFiles() {
        return rawFiles.toArray(new IRawFile[0]);
    }

    @Override
    public Object getElementAt(int index) {
        return rawFiles.get(index);
    }

    @Override
    public int getSize() {
        return rawFiles.size();
    }
    
    public boolean add(IRawFile f) {
        if (!rawFiles.contains(f)) {
            rawFiles.add(f);
            fireContentsChanged(this, rawFiles.size() - 2, rawFiles.size() - 1);
            return true;
        }
        return false;
    }
}
