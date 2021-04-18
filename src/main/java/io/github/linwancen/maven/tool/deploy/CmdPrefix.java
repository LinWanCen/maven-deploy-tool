package io.github.linwancen.maven.tool.deploy;

import io.github.linwancen.maven.tool.Conf;
import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.util.EnvUtils;

public class CmdPrefix {

    @SuppressWarnings("ConstantConditions")
    private static final String CLASS_PATH = ClassLoader.getSystemClassLoader().getResource("").getPath();

    private CmdPrefix() {}

    static String get() {
        StringBuilder cmdBuilder = new StringBuilder(EnvUtils.get("cmdPrefix", Conf.prop));
        String userSettings = EnvUtils.get("userSettings", Conf.prop);
        if (userSettings != null) {
            cmdBuilder.append(MvnKey.SETTINGS).append(userSettings);
        }
        String url = EnvUtils.get("url", Conf.prop);
        if (url != null) {
            cmdBuilder.append(MvnKey.URL).append(url);
        }
        String repositoryId = EnvUtils.get("repositoryId", Conf.prop);
        if (url != null) {
            cmdBuilder.append(MvnKey.REPOSITORY_ID).append(repositoryId);
        }
        return cmdBuilder.toString().replace("classpath:", CLASS_PATH);
    }
}
