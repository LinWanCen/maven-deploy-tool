package io.github.linwancen.maven.tool;

import io.github.linwancen.util.ConfUtils;

import java.util.Properties;

public class Conf {

    public static final String CONF_PATH = "conf.properties";
    public static Properties prop = ConfUtils.loadUtf8(CONF_PATH);

    private Conf() {}

}
