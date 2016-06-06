package ru.nsu.ccfit.zharkova.Factory.Storage;

import ru.nsu.ccfit.zharkova.Factory.Handlers.WorkerPool;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

/**
 * Created by Yana on 25.04.15.
 */

public class Controller implements Runnable {

    private ControlledStorage<?> carStorage;
    private WorkerPool workerPool;
    private static Logger logger = Logger.getLogger(Controller.class.getSimpleName());


    public Controller(ControlledStorage carStorage, WorkerPool workerPool){
        this.carStorage = carStorage;
        this.workerPool = workerPool;
    }

    @Override
    public void run() {
        try {
            giveTasks();
            while (!Thread.currentThread().isInterrupted()) {
                carStorage.listenUntilChange();
                giveTasks();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void giveTasks() {
        if (carStorage.currentSize() < carStorage.maxCapacity() && workerPool.getTaskQuantity() == 0) {
            int difference = carStorage.maxCapacity() - carStorage.currentSize();
            logger.debug("Controller asks for products " + difference);
            workerPool.addWorker(difference);
        }
    }
}

