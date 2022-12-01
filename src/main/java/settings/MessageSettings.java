package settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MessageSettings {

    private static Properties properties;

    static {
        try (FileInputStream file = new FileInputStream("src/main/resources/messages.properties");) {
            properties = new Properties();
            properties.load(file);
        } catch (IOException e) {
            System.out.println("Cannot get access to properties file");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
