package pl.edu.mimuw.kd209238.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigConstants {
    public static final Path INDEX_PATH =
            Paths.get(System.getProperty("user.home"), ".index");
}
