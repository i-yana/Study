package ru.nsu.ccfit.zharkova.ThreadPool;

/**
 * Created by Yana on 25.04.15.
 */
class ThreadPoolTask {

    private Runnable task;

    public ThreadPoolTask(Runnable t) {
        task = t;
    }

    void go() throws InterruptedException
    {
        task.run();
    }
}
