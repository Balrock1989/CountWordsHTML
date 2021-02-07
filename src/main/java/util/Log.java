package util;

import java.nio.file.Paths;
import java.util.logging.Logger;

/*** Класс для работы с логированием*/
public class Log {
    public static Logger log;
    public static void configLogger(){

        System.setProperty("java.util.logging.config.file",
                Paths.get(System.getProperty("user.dir"), "target", "classes", "logging.properties").toString());
        log = Logger.getLogger(Log.class.getName());
    }
    public static void severe(Object clazz, String error){
        log.severe(clazz.getClass().getName() + ": " + error);
    }
    public static void info(Object clazz, String info){
        log.info(clazz.getClass().getName() + ": " + info);
    }
}
