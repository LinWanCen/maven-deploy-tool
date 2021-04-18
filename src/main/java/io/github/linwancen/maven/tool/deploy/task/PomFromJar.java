package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.UnZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PomFromJar {

    private static final Logger LOG = LoggerFactory.getLogger(PomFromJar.class);

    private PomFromJar() {}

    static void unzip(String tip, String k, StringBuilder v) {
        LOG.debug("not found pom, get pom in jar\t{}\tfile:///{}.jar", tip, k);
        File file = new File(k + Suffix.JAR);
        // 解压 jar 里的所有 pom 文件
        ArrayList<File> list = UnZipUtils.unZip(file, file.getParentFile(),
                Pattern.compile("pom.xml"), null);
        if (list == null || list.isEmpty()) {
            LOG.debug("not found pom in jar\t{}\tfile:///{}.jar", tip, k);
            return;
        }
        File pomFile = null;
        // 多个 pom 文件时根据路径包含 artifactId 选择
        if (list.size() > 1) {
            String name = null;
            for (File f : list) {
                // META-INF/maven/groupId/artifactId/pom.xml
                name = f.getParentFile().getName();
                if (k.contains(name)) {
                    if (pomFile != null) {
                        LOG.warn("get more pom contains({}}) in jar\t{}\tfile:///{}",
                                name, tip, PathUtils.dirSpaceName(k));
                    }
                    pomFile = f;
                }
            }
            if (pomFile == null) {
                // 虽然没确定用哪个 pom 文件需要人工介入需要用 error，不过后面还有 getGavFromPath 兜底，如果没开启则会出现一个 error
                LOG.warn("found more pom in and can not chose\t{}\tfile:///{}", tip,
                        PathUtils.dirSpaceName(k));
                return;
            } else {
                // 在多个中找到多个包含 artifactId 的风险情况在前面已经打印 warn 日志，所以这里不再 warn
                // 不过找到多个本身就可能有问题，可能是个 fat jar，不过比起没确定用那个问题不大，这里用 info日志
                LOG.info("found more pom in jar chose {}\t{}\tfile:///{}", name, tip,
                        PathUtils.dirSpaceName(k));
            }
            return;
        } else {
            pomFile = list.get(0);
        }
        String pomPath = k + Suffix.POM;
        if (!pomFile.renameTo(new File(pomPath))) {
            LOG.warn("move pom fail\t{}\tfile:///{}", tip, PathUtils.dirSpaceName(k));
        }
        v.append(MvnKey.POM_FILE).append(pomPath);
    }
}
