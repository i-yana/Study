package ru.nsu.ccfit.zharkova.ThreadPool;


import ru.nsu.ccfit.zharkova.Factory.System.Logger;

import java.util.List;

/**
 * Created by Yana on 25.04.15.
 */

public class PooledThread extends Thread {

    private final List taskQueue;
    private static final Logger logger = Logger.getLogger(PooledThread.class.getSimpleName());

    public PooledThread(int ID, List taskQueue) {
        super(String.valueOf(ID));
        this.taskQueue = taskQueue;
    }

    private void performTask(ThreadPoolTask t)
    {
        try {
            t.go();
        }
        catch (InterruptedException ex) {
            logger.debug("Thread was inetrrupted:"+getName());
        }
    }

    public void run()
    {
        ThreadPoolTask toExecute = null;
        while (!Thread.currentThread().isInterrupted())
        {
            synchronized (taskQueue) {
                if (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException ex) {
                        logger.debug("Thread was inetrrupted:" + getName());
                    }
                    continue;
                } else {
                    toExecute = (ThreadPoolTask) taskQueue.remove(0);
                }
            }
                logger.debug(getName() + " got the job: ");
                performTask(toExecute);

        }
    }
}
