package io.github.linwancen.maven.tool.deploy.task.repo;

import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.CmdUtils;

import java.io.File;

public class RepoUtils {

    /**
     * <br/>groupId:artifactId:version:packaging:classifier
     */
    private static final String GET = "mvn dependency:get -D artifact=";

    private RepoUtils() {}

    public static boolean have(String k, int cmdTimeout) {
        File pomFile = new File(k + Suffix.POM);
        Pom pom = PomParser.parse(pomFile);
        if (pom == null) {
            return false;
        }
        return haveGav(k, cmdTimeout,
                pom.groupId, pom.artifactId, pom.version,
                pom.packaging, pom.classifier);
    }

    public static boolean haveGav(String k, int cmdTimeout,
                                  String groupId, String artifactId, String version) {
        return haveGav(k, cmdTimeout, groupId, artifactId, version, null, null);
    }

    public static boolean haveGav(String k, int cmdTimeout,
                                  String groupId, String artifactId, String version,
                                  String packaging, String classifier) {
        StringBuilder cmd = new StringBuilder(GET);
        cmd.append(groupId).append(":");
        cmd.append(artifactId).append(":");
        cmd.append(version);
        if (packaging != null) {
            cmd.append(":").append(packaging);
        }
        if (classifier != null) {
            cmd.append(":").append(classifier);
        }
        File logFile = new File(k + Suffix.GET_LOG);
        return 0 == CmdUtils.exec(cmd.toString(), cmdTimeout, logFile);
    }
}
