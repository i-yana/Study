package ru.nsu.ccfit.zharkova.Factory.Handlers;

import ru.nsu.ccfit.zharkova.ThreadPool.ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 25.04.15.
 */

public class WorkerPool {
    private final Object workerLock = new Object();
    private int delay;
    private List<Worker> workers;
    private ThreadPool pool;
    private String factoryName;

    public WorkerPool(String factoryName, int workerCount, int delay) {

        this.factoryName = factoryName;
        this.delay = delay;
        this.workers = new ArrayList<Worker>();
        pool = new ThreadPool(workerCount);
        addWorker(workerCount);
    }

    public void start(){
        pool.start();
    }

    public ThreadPool getThreadPool(){
        synchronized (workerLock) {
            return pool;
        }
    }

    public void addWorker(int quantity){
        synchronized (workerLock) {
            for (int i = 0; i < quantity; ++i) {
                workers.add(new Worker(factoryName, workers.size(), delay));
                pool.addTask(workers.get(workers.size() - 1));
                workerLock.notifyAll();
            }
        }
    }

    void removeWorker(int ID){
        synchronized (workerLock) {
            for (Worker w : workers) {
                if (w.getID() == ID) {
                    workers.remove(w);
                    workerLock.notifyAll();
                    break;
                }
            }
        }
    }
    public long getTaskQuantity(){
        synchronized (workerLock) {
            return pool.getTaskQuantity();
        }
    }
    public void stop(){
        pool.stop();
    }

    public void setDelay(int value) {
        synchronized (workerLock) {
            delay = value;
            for (Worker w : workers) {
                w.setDelay(value);
            }
        }
    }

    public void sleepUntilNoTasks() throws InterruptedException {
        while (getTaskQuantity() != 0 && !Thread.currentThread().isInterrupted())
            synchronized (workerLock) {
                workerLock.wait();
            }
    }
}

