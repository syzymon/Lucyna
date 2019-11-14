package common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigConstants {
    public static final Path INDEX_PATH =
            Paths.get(System.getProperty("user.home"), ".indexd");
    public static final String TIKA_CONFIG = "tika-config.xml";
}
