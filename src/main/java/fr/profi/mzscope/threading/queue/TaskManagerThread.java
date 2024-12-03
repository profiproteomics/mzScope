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

import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * Manager of tasks which dispatch their execution in worker threads from the thread pool.
 *
 * @author JPM235353
 *
 */
public class TaskManagerThread extends Thread {

    private static TaskManagerThread m_instance;
    private Queue<AbstractTask> m_actions;
    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass().toString());
    private HashMap<Class, HashSet<String>> m_registeredActions = new HashMap<>();

    private WorkerPool m_workerPool;

    private TaskManagerThread() {
        super("TaskManagerThread"); // useful for debugging

        m_actions = new ArrayDeque<>();
        m_workerPool = WorkerPool.getWorkerPool();
    }

    public static TaskManagerThread getTaskManagerThread() {
        if (m_instance == null) {
            m_instance = new TaskManagerThread();
            m_instance.start();
        }
        return m_instance;
    }

    private boolean register(AbstractTask action) {
        HashSet<String> registerSet = m_registeredActions.get(action.getClass());
        if (registerSet == null) {
            // we register
            registerSet = new HashSet<>();
            registerSet.add(action.getUniqueKey());
            m_registeredActions.put(action.getClass(), registerSet);
            return true;
        }

        if (registerSet.contains(action.getUniqueKey())) {
            // we can not register
            return false;
        }

        // we register
        registerSet.add(action.getUniqueKey());
        return true;
    }

    private void unregister(AbstractTask action) {
        HashSet<String> registerSet = m_registeredActions.get(action.getClass());
        registerSet.remove(action.getUniqueKey());

    }

    /**
     * Main loop of the thread
     */
    @Override
    public void run() {
        try {
            logger.debug("Start  Tasks Manager Thread");
            Object workerPoolMutex = m_workerPool.getMutex();
            while (true) {
                AbstractTask action = null;
                synchronized (this) {
                    actionSearch:
                    while (true) {
                        // look for a task to be done
                        if (!m_actions.isEmpty()) {

                            // try to get the first action with thread available
                            synchronized (workerPoolMutex) {
                                for (AbstractTask actionCur : m_actions) {
                                    if (m_workerPool.getWorkerThread(actionCur.getType()) != null) {
                                        // thread is available for this action
                                        m_actions.remove(actionCur);
                                        action = actionCur;
                                        break actionSearch;
                                    }
                                }
                            }
                            // take the first action, even if thread is not available for this action
                            action = m_actions.poll();
                            break;
                        }
                        wait();
                    }
                    notifyAll();
                }
                synchronized (workerPoolMutex) {

                    WorkerThread workerThread = null;
                    while (true) {
                        workerThread = m_workerPool.getWorkerThread(action.getType());
                        if (workerThread != null) {
                            break;
                        }
                        workerPoolMutex.wait();
                    }
                    workerThread.setAction(action);

                    workerPoolMutex.notifyAll();
                }

            }


        } catch (Throwable t) {
            LoggerFactory.getLogger("mzDB-Task").error("Unexpected exception in main loop of AccessDatabaseThread", t);
            m_instance = null; // reset thread
        }

    }

    public void actionDone(AbstractTask task) {
        synchronized (this) {

            unregister(task);

//            TaskError taskError = task.getTaskError();
//            task.getTaskInfo().setFinished((taskError==null), taskError, true);

            notifyAll();
        }

    }

    /**
     * Add a task to be done later according to its priority
     *
     */
    public final void addTask(AbstractTask task) {

        synchronized (this) {
            if (!register(task)) {
                return; // this task already exists, do nothing
            }
        }


//        TaskInfoManager.getTaskInfoManager().add(task.getTaskInfo());

        // action is queued
        synchronized (this) {
            m_actions.add(task);
            notifyAll();
        }
    }

}
