package ru.nsu.ccfit.zharkova.chat.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Yana on 07.07.15.
 */
public class ClientConfig {

    private static final String PROPERTIES_FILE = "server.properties";
    private static final String HOST_PROPERTY = "HOST";
    private static final String PORT_PROPERTY = "PORT";
    private static final String XML_MODE_PROPERTY = "XML";

    private static final Logger logger = LogManager.getLogger(ClientConfig.class);

    public static int PORT;
    public static boolean XML_MODE;
    public static String HOST;


    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(propertiesFile);

            PORT = Integer.parseInt(properties.getProperty(PORT_PROPERTY));
            HOST = properties.getProperty(HOST_PROPERTY);
            XML_MODE = Boolean.parseBoolean(properties.getProperty(XML_MODE_PROPERTY));
        } catch (FileNotFoundException ex) {
            logger.info("Properties config file not found " + PROPERTIES_FILE);
        } catch (IOException ex) {
            logger.info("Error while reading file");
        } finally {
            try {
                assert propertiesFile != null;
                propertiesFile.close();
            } catch (IOException ex) {
                logger.info(ex.getMessage());
            }
        }
    }
}
