package io.github.linwancen.maven.tool.deploy.task.repo;

import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.CmdUtils;

import java.io.File;

public class RepoUtils {

    private RepoUtils() {}

    public static boolean have(String k, String cmdGet, int cmdTimeout) {
        File pomFile = new File(k + Suffix.POM);
        Pom pom = PomParser.parse(pomFile);
        if (pom == null) {
            return false;
        }
        return haveGav(k, cmdGet, cmdTimeout,
                pom.groupId, pom.artifactId, pom.version,
                pom.packaging, pom.classifier);
    }

    public static boolean haveGav(String k, String cmdGet, int cmdTimeout,
                                  String groupId, String artifactId, String version) {
        return haveGav(k, cmdGet, cmdTimeout, groupId, artifactId, version, null, null);
    }

    public static boolean haveGav(String k, String cmdGet, int cmdTimeout,
                                  String groupId, String artifactId, String version,
                                  String packaging, String classifier) {
        StringBuilder cmd = new StringBuilder();
        // groupId:artifactId:version:packaging:classifier
        cmd.append(groupId).append(":");
        cmd.append(artifactId).append(":");
        cmd.append(version);
        if (packaging != null) {
            cmd.append(":").append(packaging);
        }
        if (classifier != null) {
            cmd.append(":").append(classifier);
        }
        cmd.insert(0, cmdGet);
        File logFile = new File(k + Suffix.GET_LOG);
        return 0 == CmdUtils.exec(cmd.toString(), cmdTimeout, logFile);
    }
}
