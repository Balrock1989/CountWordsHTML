package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/*** Класс для работы с логированием*/
public class Log {
    private static Logger log;
    public static String logHome;

    public static void configLogger() throws IOException {
        logHome = Paths.get(System.getProperty("user.dir"), "log.txt").toString();
        File file = new File(logHome);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            LogManager.getLogManager().readConfiguration(Logger.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("SEVERE Could not setup logger configuration: " + e.toString());
        }
        log = Logger.getLogger(Log.class.getName());

    }

    public static void severe(Class clazz, Exception error) {
        int lineNumber = 0;
        for (int i = error.getStackTrace().length -1; i >= 0; i--) {
            if (error.getStackTrace()[i].getClassName().equals(clazz.getName())){
                lineNumber = error.getStackTrace()[i].getLineNumber();
                break;
            }
        }
        log.severe(clazz.getName() + ": " + error + ", line: " + lineNumber);
    }

    public static void info(Class clazz, String info) {
        log.info(clazz.getName() + ": " + info);
    }
}
