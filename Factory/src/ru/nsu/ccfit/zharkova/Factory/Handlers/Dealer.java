package ru.nsu.ccfit.zharkova.Factory.Handlers;

import ru.nsu.ccfit.zharkova.Factory.API;
import ru.nsu.ccfit.zharkova.Factory.Detail.Car;
import ru.nsu.ccfit.zharkova.Factory.Storage.DetailCounter;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

/**
 * Created by Yana on 25.04.15.
 */
public class Dealer implements Runnable {

    private static final String CLASS_NAME_TAG = " Dealer";
    private int delay;
    private int count;
    private String factoryName;
    private Logger logger;
    private final DetailCounter statistic = new DetailCounter();

    public Dealer(String factoryName, int count, int delay){
        this.delay = delay;
        this.count = count;
        this.factoryName = factoryName;
        this.logger = Logger.getLogger(factoryName + CLASS_NAME_TAG);
    }

    public int getQuantity(){
        return count;
    }

    public DetailCounter getStatistic() {
        return statistic;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(delay);
                Car auto = API.Instance(factoryName).getCarStorage().get();
                statistic.increment();
                if(API.Instance(factoryName).getParser().isLogSale()) {
                    logger.info("#" + Thread.currentThread().getName() + ": " + auto.getFullCarID());
                }
            }
        } catch (InterruptedException ignored) {}
        catch (NullPointerException ignored){}
    }

    public void setDelay(int newDelay) {
        delay = newDelay;
    }
}
