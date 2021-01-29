package logger;

import java.nio.file.Paths;

public interface Log {

    static void configLogger(){
        System.setProperty("java.util.logging.config.file",
                Paths.get(System.getProperty("user.dir"), "target", "classes", "logging.properties").toString());
    }
}
