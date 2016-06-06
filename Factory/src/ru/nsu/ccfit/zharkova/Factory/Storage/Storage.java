package ru.nsu.ccfit.zharkova.Factory.Storage;

import ru.nsu.ccfit.zharkova.Factory.Detail.Detail;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Yana on 25.04.15.
 */

public class Storage<T extends Detail> {

    protected final Object storageLock = new Object();
    protected final int maxCapacity;
    protected final ArrayBlockingQueue<T> details;
    protected static Logger logger = Logger.getLogger("Storage");
    private final DetailCounter detailCounter;

    public Storage(int capacity){
        this.maxCapacity = capacity;
        details = new ArrayBlockingQueue<T>(capacity);
        this.detailCounter = new DetailCounter();
    }

    public AtomicLong getAllQuantity(){
        return detailCounter.getCount();
    }

    public void add(T detail) throws InterruptedException {
        synchronized (storageLock) {
            detailCounter.increment();
            while (details.size() == maxCapacity) {
                storageLock.wait();
            }
            details.add(detail);
            logger.debug(detail.getID() + " was added to the storage. In all: " + currentSize());
            storageLock.notifyAll();
        }
    }

    public T get() throws InterruptedException {
        synchronized (storageLock) {
            while (details.isEmpty()) {
                storageLock.wait();
            }
            T part = details.take();
            logger.debug(part.getID() + " was removed from the storage. In all:" + currentSize());
            storageLock.notifyAll();
            return part;
        }
    }

    public int maxCapacity(){
        return maxCapacity;
    }

    public int currentSize() {
        synchronized (storageLock) {
            return details.size();
        }
    }
}
