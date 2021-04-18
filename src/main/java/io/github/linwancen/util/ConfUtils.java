package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ConfUtils.class);

    private ConfUtils() {}

    public static Properties loadUtf8(String path) {
        return loadUtf8(new File(PathUtils.CLASS_PATH, path));
    }

    public static Properties loadUtf8(File file) {
        Properties prop = new Properties();
        if (!file.exists()) {
            LOG.warn("not found prop file:///{}", PathUtils.canonicalPath(file));
            return prop;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            prop.load(br);
        } catch (Exception e) {
            LOG.warn("load prop Exception, file:///{}", PathUtils.canonicalPath(file), e);
        }
        return prop;
    }

}