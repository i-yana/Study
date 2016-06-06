package ru.nsu.ccfit.zharkova.Factory;

import ru.nsu.ccfit.zharkova.Factory.Detail.Accessory;
import ru.nsu.ccfit.zharkova.Factory.Detail.Body;
import ru.nsu.ccfit.zharkova.Factory.Detail.Car;
import ru.nsu.ccfit.zharkova.Factory.Detail.Motor;
import ru.nsu.ccfit.zharkova.Factory.Handlers.Dealer;
import ru.nsu.ccfit.zharkova.Factory.Handlers.Supplier;
import ru.nsu.ccfit.zharkova.Factory.Handlers.WorkerPool;
import ru.nsu.ccfit.zharkova.Factory.Storage.ControlledStorage;
import ru.nsu.ccfit.zharkova.Factory.Storage.Controller;
import ru.nsu.ccfit.zharkova.Factory.Storage.Storage;
import ru.nsu.ccfit.zharkova.Factory.System.IDManager;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;
import ru.nsu.ccfit.zharkova.Factory.System.Parser;
import ru.nsu.ccfit.zharkova.ThreadPool.ThreadPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yana on 25.04.15.
 */
public class API implements Runnable {

    private static Map<String, API> factories = new HashMap<String, API>();
    private boolean isWork = false;

    public static Map<String, API> getFactories(){
        return factories;
    }
    public static int getWorkersDefaultDelay(){
        return WORKERS_DEFAULT_DELAY;
    }
    public static int getDealersDefaultDelay(){
        return DEALERS_DEFAULT_DELAY;
    }
    public static int getAccessorySuppliersDefaultDelay(){
        return ACCESSORY_SUPPLIERS_DEFAULT_DELAY;
    }
    public static int getMotorSuppliersDefaultDelay(){
        return MOTOR_SUPPLIERS_DEFAULT_DELAY;
    }
    public static int getBodySuppliersDefaultDelay(){
        return BODY_SUPPLIERS_DEFAULT_DELAY;
    }

    private static Logger logger;
    private static final int WORKERS_DEFAULT_DELAY = 0;
    private static final int DEALERS_DEFAULT_DELAY = 0;
    private static final int ACCESSORY_SUPPLIERS_DEFAULT_DELAY = 0;
    private static final int MOTOR_SUPPLIERS_DEFAULT_DELAY = 0;
    private static final int BODY_SUPPLIERS_DEFAULT_DELAY = 0;
    private final static String STORAGE_CONTROLLER_THREAD_ID = "StorageController";

    private final Storage<Accessory> accessoryStorage;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final ControlledStorage<Car> carStorage;
    private final IDManager idManager;

    private final WorkerPool workerPool;
    private final Dealer dealers;
    private final Supplier<Accessory> accessorySuppliers;
    private final Supplier<Body> bodySuppliers;
    private final Supplier<Motor> motorSuppliers;

    private final Thread storageControllerThread;
    private final Thread bodySupplierThread;
    private final Thread motorSupplierThread;
    private final List<Thread> accessorySuppliersThreads;
    private final List<Thread> dealersThreads;
    private final Parser p;
    private final String factoryName;

    public static API Instance(String factoryName) {
        return factories.get(factoryName);
    }

    public API(String factoryName, String config) throws IOException {
        p = new Parser(config);
        this.factoryName = factoryName;

        logger = Logger.getLogger(factoryName);
        accessoryStorage = new Storage<Accessory>(p.getAccessoryStorageCapacity());
        bodyStorage = new Storage<Body>(p.getBodyStorageCapacity());
        motorStorage = new Storage<Motor>(p.getMotorStorageCapacity());

        idManager = new IDManager();

        accessorySuppliers = new Supplier<Accessory>(factoryName, accessoryStorage, Accessory.class, ACCESSORY_SUPPLIERS_DEFAULT_DELAY, p.getAccessorySupplierNumber());
        motorSuppliers = new Supplier<Motor>(factoryName, motorStorage, Motor.class, MOTOR_SUPPLIERS_DEFAULT_DELAY, p.getMotorSupplierNumber());
        bodySuppliers = new Supplier<Body>(factoryName, bodyStorage, Body.class, BODY_SUPPLIERS_DEFAULT_DELAY, p.getBodySupplierNumber());
        workerPool = new WorkerPool(factoryName, p.getWorkerNumber(), WORKERS_DEFAULT_DELAY);
        carStorage = new ControlledStorage<Car>(p.getAutoStorageCapacity(), workerPool);
        dealers = new Dealer(factoryName,p.getDealerNumber(), DEALERS_DEFAULT_DELAY);

        Controller controller = new Controller(carStorage, workerPool);
        carStorage.setStorageController(controller);


        logger.debug("initialize");
        storageControllerThread = new Thread(controller, STORAGE_CONTROLLER_THREAD_ID);
        bodySupplierThread = new Thread(bodySuppliers, String.valueOf(p.getBodySupplierNumber()));
        motorSupplierThread = new Thread(motorSuppliers, String.valueOf(p.getMotorSupplierNumber()));
        accessorySuppliersThreads = new ArrayList<Thread>();
        dealersThreads = new ArrayList<Thread>();

        factories.put(factoryName,this);
    }

    public void start() {
        if(isWork){
            logger.debug("Faсtory already works");
            return;
        }
        storageControllerThread.start();
        bodySupplierThread.start();
        motorSupplierThread.start();
        workerPool.start();
        for (int i = 0; i < accessorySuppliers.getQuantity(); ++i) {
            Thread thread = new Thread(accessorySuppliers, String.valueOf(i+1));
            accessorySuppliersThreads.add(thread);
            thread.start();
        }
        for (int i = 0; i < dealers.getQuantity(); ++i) {
            Thread thread = new Thread(dealers, String.valueOf(i+1));
            thread.start();
        }
        isWork = true;
    }

    public void stop() {
        if(!isWork){
            logger.debug("Factory didn't begin its work");
            return;
        }
        storageControllerThread.interrupt();
        logger.debug("Controller was interrupted");
        workerPool.stop();
        logger.debug("WorkerPool threads was interrupted");
        for (Thread accessorySuppliersThread : accessorySuppliersThreads){
            accessorySuppliersThread.interrupt();
            logger.debug("Accessory Supplier thread #" + accessorySuppliersThread.getName() + " was interrupted");
        }
        motorSupplierThread.interrupt();
        logger.debug("Motor Supplier thread #" + motorSupplierThread.getName() + " was interrupted");
        bodySupplierThread.interrupt();
        logger.debug("Body Supplier thread #" + bodySupplierThread.getName() + " was interrupted");

        for (Thread dealersThread : dealersThreads){
            dealersThread.interrupt();
            logger.debug("Dealers thread #" + dealersThread.getName() + " was interrupted");
        }
        factories.remove(factoryName);
        isWork = false;
        Logger.closeLogFiles();
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

    public Dealer getDealers(){
        return dealers;
    }

    public Supplier<Accessory> getAccessorySupplier() {
        return accessorySuppliers;
    }

    public Supplier<Body> getBodySupplier() {
        return bodySuppliers;
    }

    public Supplier<Motor> getMotorSupplier() {
        return motorSuppliers;
    }

    public Storage<Accessory> getAccessoryStorage() {
        return accessoryStorage;
    }

    public Storage<Body> getBodyStorage() {
        return bodyStorage;
    }

    public Storage<Motor> getMotorStorage() {
        return motorStorage;
    }

    public ControlledStorage<Car> getCarStorage() {
        return carStorage;
    }

    public IDManager getIdManager(){
        return idManager;
    }

    public Parser getParser(){
        return p;
    }
    @Override
    public void run() {
        start();
    }
}
