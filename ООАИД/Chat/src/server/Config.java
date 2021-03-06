package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Yana on 03.07.15.
 */
public class Config {

    private static final String PROPERTIES_FILE = "server.properties";
    private static final String PORT_PROPERTY = "PORT";
    public static int PORT;
    public static int HISTORY_LENGTH;

    private static final Logger logger = LogManager.getLogger(Config.class);

    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(propertiesFile);

            PORT = Integer.parseInt(properties.getProperty(PORT_PROPERTY));
        } catch (FileNotFoundException ex) {
            logger.info("Properties config file not found");
        } catch (IOException ex) {
            logger.info("Error while reading file");
        } finally {
            try {
                assert propertiesFile != null;
                propertiesFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
