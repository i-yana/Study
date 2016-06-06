package ru.nsu.ccfit.zharkova.ThreadPool;

import ru.nsu.ccfit.zharkova.Factory.System.IDManager;

import java.util.*;

/**
 * Created by Yana on 25.04.15.
 */
public class ThreadPool {
    private final List<ThreadPoolTask> taskQueue = new LinkedList<ThreadPoolTask>();
    private Set<PooledThread> availableThreads = new HashSet<PooledThread>();


    public void addTask(Runnable t)
    {
        synchronized (taskQueue)
        {
            taskQueue.add(new ThreadPoolTask(t));
            taskQueue.notifyAll();
        }
    }

    public ThreadPool(int threadCount) {
        for (int i=0;i<threadCount;i++)
        {
            availableThreads.add(new PooledThread(i,taskQueue));
        }
    }

    public int getTaskQuantity(){
        synchronized (taskQueue) {
            return taskQueue.size();
        }
    }

    public void start(){
        for (PooledThread availableThread : availableThreads) {
            (availableThread).start();
        }
    }

    public void stop() {
        for (PooledThread availableThread : availableThreads) {
            availableThread.interrupt();
        }
    }
}
