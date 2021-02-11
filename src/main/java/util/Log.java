package util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/*** Класс для работы с логированием*/
public class Log {
    private static Logger log;
    public static String logHome;

    public static void configLogger() {
        logHome = Paths.get(System.getProperty("user.dir"), "log.txt").toString();
        try {
            LogManager.getLogManager().readConfiguration(Logger.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        log = Logger.getLogger(Log.class.getName());

    }

    public static void severe(Class clazz, Exception error) {
        int size = error.getStackTrace().length;
        log.severe(clazz.getName() + ": " + error + ", line: " + error.getStackTrace()[size -1].getLineNumber());
    }

    public static void info(Class clazz, String info) {
        log.info(clazz.getName() + ": " + info);
    }
}
