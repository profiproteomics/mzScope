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
package fr.profi.mzscope.threading.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Thread which executes a task
 *
 * @author JPM235353
 */
public class WorkerThread extends Thread {

    private final Logger m_logger = LoggerFactory.getLogger(getClass().toString());

    private AbstractTask m_action = null;

    private static int m_threadCounter = 0;

    private WorkerPool m_workerPool;

    public WorkerThread(WorkerPool workerPool) {
        super("WorkerThread"+m_threadCounter); // for debug, set specific name
        m_threadCounter++;

        m_workerPool = workerPool;

    }

    public synchronized boolean isAvailable() {
        return (m_action == null);
    }

    public synchronized void setAction(AbstractTask action) {

        m_action = action;
        notifyAll();

    }

    @Override
    public void run() {
        try {
            while (true) {
                m_logger.debug(" WT : Step 1");
                AbstractTask action;

                synchronized (this) {
                    m_logger.debug(" WT : Step 2");
                    while (true) {
                        m_logger.debug(" WT : Step 2.1");
                        if (m_action != null) {
                            action = m_action;
                            m_logger.debug(" WT : Step 2.2");
                            break;
                        }
                        m_logger.debug(" WT : Step 2.3");
                        wait();
                    }
                    notifyAll();
                    m_logger.debug(" WT : Step 3");
                }

//                action.getTaskInfo().setRunning(true);


                // execute action
                try {
                    m_logger.debug(" WT : Step 4");
                    boolean success = action.runTask();
                    m_logger.debug(" WT : Step 5");
                    // call callback code (if there is not a consecutive task)
                    action.callback(success);
                    m_logger.debug(" WT : Step 6");


                } finally {
                    m_logger.debug(" WT : Step 7");
                    TaskManagerThread.getTaskManagerThread().actionDone(action);
                }

                synchronized(this) {
                    m_logger.debug(" WT : Step 8");
                    m_action = null;
                }

                m_logger.debug(" WT : Step 9");
                m_workerPool.threadFinished();
            }

        } catch (Throwable t) {
            m_logger.debug(" WT : Step 10");
            m_logger.debug("Unexpected exception in main loop of WorkerThread", t);
        }

    }

}
