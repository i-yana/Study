package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Yana on 04.12.15.
 */
public class Config {
    private static final String PROPERTIES_FILE = "server.properties";

    public static int PORT;
    public static String HOST;

    public static final String PROPERTIES_CONFIG_FILE_NOT_FOUND = "Properties config file not found";
    public static final String ERROR_WHILE_READING_FILE = "Error while reading file";
    public static final String NAME_PORT_FIELD = "PORT";
    public static final String NAME_HOST_FIELD = "HOST";

    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(propertiesFile);

            PORT = Integer.parseInt(properties.getProperty(NAME_PORT_FIELD));
            HOST = properties.getProperty(NAME_HOST_FIELD);;
        } catch (FileNotFoundException ex) {
            System.err.println(PROPERTIES_CONFIG_FILE_NOT_FOUND);
        } catch (IOException ex) {
            System.err.println(ERROR_WHILE_READING_FILE);
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
