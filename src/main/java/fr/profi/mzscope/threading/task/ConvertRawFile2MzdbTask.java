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
import fr.profi.mzscope.ConverterManager;
import fr.profi.mzscope.threading.queue.AbstractCallback;
import fr.profi.mzscope.threading.queue.AbstractTask;
import fr.profi.mzscope.threading.queue.WorkerPool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Task to convert a raw file to a mzdb file
 *
 * @author JPM235353
 */
public class ConvertRawFile2MzdbTask extends AbstractTask {

    private File m_file;
    private Process m_conversionProcess = null;

    private String m_outputTempFilePath = null;
    private String m_outputMzdbFilePath = null;

    private ConverterListener m_converterListener;

    public ConvertRawFile2MzdbTask(AbstractCallback callback, File f) {
        super(callback);
        m_file = f;
    }

    @Override
    public int getType() {
        return WorkerPool.CONVERTER_THREAD;
    }

    @Override
    public String getUniqueKey() {
        return m_file.getName().toLowerCase();
    }

    public File getFile(){
        return m_file;
    }

    public void setListener(ConverterListener listener) {
        m_converterListener = listener;
    }

    @Override
    public boolean precheck() {
        logger.debug("file conversion precheck "+getFile().getAbsolutePath());
        if(!ConverterManager.getConverterExeFile().exists()){
            logger.warn("Converter executable doesn't exists.");
            return false;
        }

        // check that corresponding converted file does not exist
        String path = getFile().getAbsolutePath();
        int index = path.lastIndexOf(".");
        String mzdbFilePath = path.substring(0, index + 1) + "mzdb";
        File mzdbFile = new File(mzdbFilePath);
        if (mzdbFile.exists()) {
            m_taskError = new TaskError("mzDB file corresponding to " + getFile().getAbsolutePath() + " already exists.");
            logger.warn("mzDB file corresponding to "+getFile().getAbsolutePath()+ " already exists.");
            return false;
        }

        m_outputMzdbFilePath = mzdbFilePath;

        // delete mzdb tmp file if necessary
        String mzdbTmpFilePath = path.substring(0, index + 1) + "mzdb.tmp";
        File tempFile = new File(mzdbTmpFilePath);
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if(!deleted){
                m_taskError = new TaskError("Can't delete temp file " + tempFile.getAbsolutePath() );
                logger.warn("Can't delete file "+tempFile.getAbsolutePath());
                return false;
            }
        }
        m_outputTempFilePath = mzdbTmpFilePath;

        return true;
    }

    @Override
    public boolean runTask() {
        if(m_converterListener !=null)
            m_converterListener.convertStart(getFile());

        if (!precheck()) {
            if (m_callback instanceof ConvertRawFile2MzdbCallback) {
                ((ConvertRawFile2MzdbCallback) m_callback).setMzdbFile(null);
                ((ConvertRawFile2MzdbCallback) m_callback).setRawFile(getFile());
            }
            return false;
        }


        logger.info("File conversion start "+getFile().getAbsolutePath());
        // convert file
        if (!convertFile()) {
            if (m_callback instanceof ConvertRawFile2MzdbCallback) {
                ((ConvertRawFile2MzdbCallback) m_callback).setMzdbFile(null);
                ((ConvertRawFile2MzdbCallback) m_callback).setRawFile(getFile());
            }
            return false;
        }

        //
        try {
            logger.warn("Check m_conversionProcess");
            while (/*m_run &&*/ m_conversionProcess != null && m_conversionProcess.isAlive()) {
                Thread.sleep(2000);
            }
        } catch (InterruptedException ex) {
            logger.error("File Finalization Interrupted!");
        }

        if (m_conversionProcess != null && m_conversionProcess.exitValue() == 0) {
            File tmpFile = new File(m_outputTempFilePath);
            File mzdbFile = new File(m_outputMzdbFilePath);
            if (tmpFile.exists()) {
                String log = tmpFile.getAbsolutePath() + " size is " + tmpFile.length() + " bytes";
                logger.debug(log);
                if (!tmpFile.renameTo(mzdbFile)) {
                    m_taskError = new TaskError("Temp File Renaming Failure", "File " + tmpFile.getAbsolutePath() + " could not be renamed.");
                    String log2 = "File " + tmpFile.getAbsolutePath() + " could not be renamed.";
                    logger.debug(log2);
                    return false;
                }
            } else {
                //JPM.WART : raw2mzdb.exe automatically rename .mzdb.tmp file to .mzDB VDS TODO why rename ?
                mzdbFile.renameTo(mzdbFile);
            }

            if (m_callback instanceof ConvertRawFile2MzdbCallback) {
                ((ConvertRawFile2MzdbCallback) m_callback).setMzdbFile(mzdbFile);
                ((ConvertRawFile2MzdbCallback) m_callback).setRawFile(getFile());
            }

        } else {

            m_taskError = new TaskError("Converter Failure", getFile().getAbsolutePath()+" conversion: Non-zero exit value.");
            String log = "File converter for file " + getFile().getAbsolutePath() + " is not responding.";
            logger.info(log);

            if (m_callback instanceof ConvertRawFile2MzdbCallback) {
                ((ConvertRawFile2MzdbCallback) m_callback).setMzdbFile(null);
                ((ConvertRawFile2MzdbCallback) m_callback).setRawFile(getFile());
            }

            return false;
        }

        String log = "Converting for file: " + getFile().getAbsolutePath() + " has come to its end.";
        logger.info(log);

        return true;
    }

    private boolean convertFile() {

        try {
            logger.info("Convert File ");
            String architecture = ConvertRawFile2MzdbTask.getSystemArchitecture();
            if (architecture.contains("64")) {
                List<String> command = new ArrayList<>();
                logger.info("add "+ConverterManager.getConverterExeFile().getAbsolutePath());
                command.add(ConverterManager.getConverterExeFile().getAbsolutePath());
//                if(ConfigurationManager.getConverterOptions() != null && !ConfigurationManager.getConverterOptions().trim().isEmpty()) {
//                    String options = ConfigurationManager.getConverterOptions();
//                    String[] eachOptions  = options.split(" ");
//                    for (int i = 0; i<eachOptions.length; i++){
//                        if(!eachOptions[i].trim().isEmpty())
//                            command.add(eachOptions[i].trim());
//                    }
//                }
                command.add("-n");
                command.add("-i");
                command.add(getFile().getAbsolutePath());
                logger.info("add "+getFile().getAbsolutePath());
                command.add("-o");
                command.add(m_outputTempFilePath);

                m_conversionProcess = new ProcessBuilder().command(command).start();
            } else {
                m_taskError = new TaskError("This installation package is not supported by this processor type. Contact your administrator.");
                logger.warn("This installation package is not supported by this processor type. Contact your administrator.");
                return false;
            }

            logger.info("------------------------------------------------------------------------");
            logger.info("CONVERSION");
            logger.info("------------------------------------------------------------------------");
            logger.info("");

            String log = "Converter: " + ConverterManager.getConverterExeFile().getAbsolutePath();
            logger.info(log);

            InputStream standardOutputStream = m_conversionProcess.getInputStream();
            BufferedReader standardReader = new BufferedReader(new InputStreamReader(standardOutputStream));
            String line;
            while ((line = standardReader.readLine()) != null) {
                logger.info(line);
            }

            InputStream errorStream = m_conversionProcess.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String errorLine;

            while ((errorLine = errorReader.readLine()) != null) {
                logger.info("Warning:");
                logger.info(errorLine);
            }

        } catch (IOException ex) {
            m_taskError = new TaskError("Converter faced an IOException during conversion of " + getFile().getAbsolutePath() + ". Check input file's integrity.");
            logger.error("File convertion failed!");
            return false;
        }

        return true;
    }


    private static String getSystemArchitecture() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        String realArch = arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
        System.out.println("RealArch:" + realArch);
        return realArch;
    }

}
