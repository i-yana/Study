package ru.nsu.ccfit.zharkova.Factory.Storage;

import ru.nsu.ccfit.zharkova.Factory.Detail.Detail;
import ru.nsu.ccfit.zharkova.Factory.Handlers.WorkerPool;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Yana on 25.04.15.
 */
public class ControlledStorage <T extends Detail> extends Storage<T> {

    private Controller controller;
    private final WorkerPool workerPool;
    private static Logger logger = Logger.getLogger(ControlledStorage.class.getSimpleName());
    private final DetailCounter detailCounter;

    public ControlledStorage(int capacity, WorkerPool workerPool) {
        super(capacity);
        this.workerPool = workerPool;
        detailCounter = new DetailCounter();
    }

    public AtomicLong getAllQuantity(){
        return detailCounter.getCount();
    }

    public void add(T detail) throws InterruptedException {
        detailCounter.increment();
        synchronized (storageLock) {
            while (details.size() == maxCapacity()) {
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
            notifyListeners();
            storageLock.notifyAll();
            return part;
        }
    }

    public int maxCapacity(){
        return super.maxCapacity();
    }

    public int currentSize() {
        return super.currentSize();
    }


    void notifyListeners() {
        if (controller != null) {
            synchronized (storageLock) {
                storageLock.notifyAll();
            }
        }
    }

    public void setStorageController(Controller controller) {
        this.controller = controller;
        notifyListeners();
    }

    void listenUntilChange() throws InterruptedException {
        synchronized (storageLock) {
            if (currentSize() == 0 && workerPool.getTaskQuantity() == 0) {
                return;
            }
            storageLock.wait();
        }
    }
}
