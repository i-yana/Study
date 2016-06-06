package ru.nsu.ccfit.zharkova.Factory.Handlers;

import ru.nsu.ccfit.zharkova.Factory.API;
import ru.nsu.ccfit.zharkova.Factory.Detail.Accessory;
import ru.nsu.ccfit.zharkova.Factory.Detail.Body;
import ru.nsu.ccfit.zharkova.Factory.Detail.Car;
import ru.nsu.ccfit.zharkova.Factory.Detail.Motor;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

/**
 * Created by Yana on 25.04.15.
 */
public class Worker implements Runnable {

    private static final String CLASS_NAME_TAG = " Worker";
    private int ID;
    private int delay;
    private String factoryName;
    private Logger logger;

    public Worker(String factoryName, int ID, int delay) {
        this.delay = delay;
        this.ID = ID;
        this.factoryName = factoryName;
        this.logger = Logger.getLogger(factoryName + CLASS_NAME_TAG);
    }


    @Override
    public void run() {
        try {
            Body b = API.Instance(factoryName).getBodyStorage().get();
            Motor m = API.Instance(factoryName).getMotorStorage().get();
            Accessory a = API.Instance(factoryName).getAccessoryStorage().get();
            Thread.sleep(delay);
            Car auto = new Car(a, b, m);
            auto.setID(API.Instance(factoryName).getIdManager().getProductId());
            API.Instance(factoryName).getCarStorage().add(auto);
            API.Instance(factoryName).getWorkerPool().removeWorker(getID());
            logger.debug("#" + Thread.currentThread().getName() + " assembled " + auto.getID());
        }catch (InterruptedException ignored) {}
        catch (NullPointerException ignored){}
    }

    public int getID() {
        return ID;
    }

    public void setDelay(int newDelay) {
        this.delay = newDelay;
    }
}
