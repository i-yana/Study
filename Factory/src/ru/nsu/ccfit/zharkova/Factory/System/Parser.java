package ru.nsu.ccfit.zharkova.Factory.System;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Yana on 25.04.15.
 */
public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getSimpleName());

    private final String BODY_STORAGE_CAPACITY = "StorageBodySize";
    private final String MOTOR_STORAGE_CAPACITY = "StorageMotorSize";
    private final String ACCESSORY_STORAGE_CAPACITY = "StorageAccessorySize";
    private final String AUTO_STORAGE_CAPACITY = "StorageAutoSize";
    private final String ACCESSORY_SUPPLIER_NUMBER = "AccessorySuppliers";
    private final String BODY_SUPPLIER_NUMBER = "AccessorySuppliers";
    private final String MOTOR_SUPPLIER_NUMBER = "AccessorySuppliers";
    private final String WORKER_NUMBER = "Workers";
    private final String DEALER_NUMBER = "Dealers";
    private final String SALE_LOG = "LogSale";

    private final String STORAGE_BODY_DEFAULT_SIZE = "100";
    private final String STORAGE_MOTOR_DEFAULT_SIZE = "100";
    private final String STORAGE_ACCESSORY_DEFAULT_SIZE = "100";
    private final String STORAGE_AUTO_DEFAULT_SIZE = "100";
    private final String ACCESSORY_SUPPLIERS_DEFAULT_NUMBER = "5";
    private final String BODY_SUPPLIERS_DEFAULT_NUMBER = "1";
    private final String MOTOR_SUPPLIERS_DEFAULT_NUMBER = "1";
    private final String WORKER_DEFAULT_NUMBER = "10";
    private final String DEALER_DEFAULT_NUMBER = "20";
    private final String DEFAULT_LOG_SALE = "true";

    private int bodyStorageCapacity;
    private int motorStorageCapacity;
    private int accessoryStorageCapacity;
    private int autoStorageCapacity;
    private int accessorySupplierNumber;
    private int bodySupplierNumber;
    private int motorSupplierNumber;
    private int workerNumber;
    private int dealerNumber;
    private boolean logSale;

    private String configFile;

    public Parser(String config) throws IOException {
        this.configFile = config;
        Properties properties = new Properties();
        InputStream fin = null;
        try{
            fin = new FileInputStream(config);
            properties.load(fin);


            bodyStorageCapacity = Integer.parseInt(getProperty(properties, BODY_STORAGE_CAPACITY, STORAGE_BODY_DEFAULT_SIZE));
            motorStorageCapacity = Integer.parseInt(getProperty(properties, MOTOR_STORAGE_CAPACITY, STORAGE_MOTOR_DEFAULT_SIZE));
            accessoryStorageCapacity = Integer.parseInt(getProperty(properties, ACCESSORY_STORAGE_CAPACITY, STORAGE_ACCESSORY_DEFAULT_SIZE));
            autoStorageCapacity = Integer.parseInt(getProperty(properties, AUTO_STORAGE_CAPACITY, STORAGE_AUTO_DEFAULT_SIZE));
            accessorySupplierNumber = Integer.parseInt(getProperty(properties, ACCESSORY_SUPPLIER_NUMBER, ACCESSORY_SUPPLIERS_DEFAULT_NUMBER));
            bodySupplierNumber = Integer.parseInt(getProperty(properties, BODY_SUPPLIER_NUMBER, BODY_SUPPLIERS_DEFAULT_NUMBER));
            motorSupplierNumber = Integer.parseInt(getProperty(properties, MOTOR_SUPPLIER_NUMBER, MOTOR_SUPPLIERS_DEFAULT_NUMBER));
            workerNumber = Integer.parseInt(getProperty(properties, WORKER_NUMBER, WORKER_DEFAULT_NUMBER));
            dealerNumber = Integer.parseInt(getProperty(properties, DEALER_NUMBER, DEALER_DEFAULT_NUMBER));
            logSale = Boolean.parseBoolean(getProperty(properties, SALE_LOG, DEFAULT_LOG_SALE));

            logger.debug("Config is loaded successfully");
        }
        catch (IOException e) {
            throw new IOException(e);
        }
        finally {
            try{
                if (fin != null)
                    fin.close();
            }
            catch (Exception e){
                logger.error("Problem with close stream of config file");
            }
        }
    }

    private String getProperty(Properties p, String key, String value){
        String prop = p.getProperty(key);
        if (prop == null){
            logger.debug(configFile + "does not have a value for " + key +". Use default value "+ value);
            return value;
        }
        return prop;
    }


    public int getBodyStorageCapacity() {
        return bodyStorageCapacity;
    }

    public int getMotorStorageCapacity() {
        return motorStorageCapacity;
    }

    public int getAccessoryStorageCapacity() {
        return accessoryStorageCapacity;
    }

    public int getAutoStorageCapacity() {
        return autoStorageCapacity;
    }

    public int getAccessorySupplierNumber() {
        return accessorySupplierNumber;
    }

    public int getBodySupplierNumber() {
        return bodySupplierNumber;
    }

    public int getMotorSupplierNumber() {
        return motorSupplierNumber;
    }

    public int getWorkerNumber() {
        return workerNumber;
    }

    public int getDealerNumber() {
        return dealerNumber;
    }

    public boolean isLogSale() {
        return logSale;
    }
}

