package ru.nsu.ccfit.zharkova.Factory;

import ru.nsu.ccfit.zharkova.Factory.System.Logger;
import ru.nsu.ccfit.zharkova.GUI.GUIClient;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Yana on 25.04.15.
 */
public class Factory {

    private String factoryName;
    private String config;
    private boolean isWork = false;
    Logger logger;
    public Factory(String factoryName, String config)  {

        this.factoryName = factoryName;
        this.config = config;
        logger = Logger.getLogger("Factory " + factoryName);
    }

    public void start(){
        if(isWork){
            logger.debug("Faсtory already works");
            Logger.closeLogFiles();
            return;
        }
        try {
            new API(factoryName,config);
        } catch (IOException e) {
            logger.error("cannot load config " + e.getMessage());
            Logger.closeLogFiles();
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIClient(factoryName);
            }
        });
        isWork = true;
    }
}