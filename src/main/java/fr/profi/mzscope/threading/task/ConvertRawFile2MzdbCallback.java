/*
 * Copyright (C) 2019
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the CeCILL FREE SOFTWARE LICENSE AGREEMENT
 * ; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * CeCILL License V2.1 for more details.
 *
 * You should have received a copy of the CeCILL License
 * along with this program; If not, see <http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.html>.
 */
package fr.profi.mzscope.threading.task;

import fr.profi.mzscope.ConverterListener;
import fr.profi.mzscope.threading.queue.AbstractCallback;
import fr.proline.studio.WindowManager;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;

/**
 *
 * Callback called when the conversion to mzdb task has finished
 *
 * @author JPM235353
 */
public class ConvertRawFile2MzdbCallback extends AbstractCallback {

    private ConverterListener m_converterListener;

    public ConvertRawFile2MzdbCallback() {
    }

    private File m_mzdbFile = null;
    private File m_rawFile = null;

    public void setListener(ConverterListener listener) {
        m_converterListener = listener;
    }

    @Override
    public boolean mustBeCalledInAWT() {
        return true;
    }

    @Override
    public void run(boolean success, long taskId) {
        m_logger.debug( " --- Finish raw2mzdb. run next convert if exist ");
        if(!success) {
            String message = (getTaskError() !=null && StringUtils.isNotEmpty(getTaskError().getErrorText())) ? getTaskError().getErrorTitle()+" : "+getTaskError().getErrorText() : getTaskError().getErrorTitle();
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), message, "Conversion Error", JOptionPane.ERROR_MESSAGE);

        }
        // run next convert if exist
        if(m_converterListener !=null)
            m_converterListener.convertEnd(m_rawFile, m_mzdbFile, success);

    }

    public void setRawFile(File rfile) {
        m_rawFile = rfile;
    }

    public void setMzdbFile(File file) {
        m_mzdbFile = file;
    }
}
