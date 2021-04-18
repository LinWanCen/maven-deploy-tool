package io.github.linwancen.maven.tool;

import io.github.linwancen.util.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    public static void main(String[] args) {
        String deployDir = EnvUtils.get("deployDir", Conf.prop);
        if (deployDir != null) {
            File file = new File(deployDir);
            Service.run(file);
            return;
        }
        LOG.info("have not -D deployDir=[deployDir] in args or {}, and GUI no support yet.", Conf.CONF_PATH);
        // GUI 图形界面
    }

}
