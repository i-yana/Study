package ru.nsu.ccfit.zharkova.Factory.Handlers;

import ru.nsu.ccfit.zharkova.Factory.API;
import ru.nsu.ccfit.zharkova.Factory.Detail.Detail;
import ru.nsu.ccfit.zharkova.Factory.Storage.Storage;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

/**
 * Created by Yana on 25.04.15.
 */

public class Supplier<T extends Detail> implements Runnable{

    private static final String CLASS_NAME_TAG = " Supplier";
    private final String factoryName;
    private final Storage<T> storage;
    private final Class<T> classType;
    private int count;
    private int delay;
    private static Logger logger;

    public Supplier(String factoryName, Storage<T> storage, Class<T> classType, int delay, int count){
        this.factoryName = factoryName;
        this.storage = storage;
        this.classType = classType;
        this.delay = delay;
        this.count = count;
        logger = Logger.getLogger(factoryName + CLASS_NAME_TAG);
    }

    synchronized public void setDelay(int newDelay){
        this.delay = newDelay;
    }

    public int getQuantity(){
        return count;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Thread.sleep(delay);
                logger.debug("<" + classType.getSimpleName() + "> #" + Thread.currentThread().getName() + " put detail");
                T detail = (T) classType.newInstance();
                detail.setID(API.Instance(factoryName).getIdManager().getProductId());
                storage.add(detail);
            }
        } catch (InterruptedException ignored) {
        } catch (NullPointerException ignored) {
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
            logger.debug("Supplier thread #" + Thread.currentThread().getName() + " was interrupted");
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
            logger.debug("Supplier thread #" + Thread.currentThread().getName()+ " was interrupted");
        }
    }
}

