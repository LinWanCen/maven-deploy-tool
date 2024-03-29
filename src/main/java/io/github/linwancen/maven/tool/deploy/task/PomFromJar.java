package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.UnZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class PomFromJar {

    private static final Logger LOG = LoggerFactory.getLogger(PomFromJar.class);

    private PomFromJar() {}

    static void unzip(String k, StringBuilder v) {
        LOG.debug("get pom in jar\tfile:///{}.jar\tbecause not found pom, ", k);
        File file = new File(k + Suffix.JAR);
        // 避免 jar 在同一个目录的情况下相互覆盖并导致其他的移动失败
        // 解压的时候会创建文件夹这里就不创建了
        File dir = new File(k);
        // 解压 jar 里的所有 pom 文件
        List<File> list = UnZipUtils.unZip(file, dir,
                Pattern.compile("pom.xml"), null);
        if (list == null || list.isEmpty()) {
            LOG.debug("not found pom in jar\tfile:///{}.jar", k);
            return;
        }
        String dirSpaceName = PathUtils.dirSpaceName(k);
        File pomFile = null;
        // 多个 pom 文件时根据路径包含 artifactId 选择
        if (list.size() > 1) {
            String name = null;
            for (File f : list) {
                // META-INF/maven/groupId/artifactId/pom.xml
                name = f.getParentFile().getName();
                if (k.contains(name)) {
                    // 重复找到符合的 pom
                    if (pomFile != null) {
                        LOG.warn("found more pom in jar\tfile:///{}\twhat contains({}}) ",
                                name, dirSpaceName);
                    }
                    pomFile = f;
                }
            }
            if (pomFile == null) {
                // 虽然没确定用哪个 pom 文件需要人工介入需要用 error，不过后面还有 getGavFromPath 兜底，如果没开启则会出现一个 error
                LOG.warn("can not chose\tfile:///{}\tfor found more pom in jar", dirSpaceName);
                return;
            }
            // 在多个中找到多个包含 artifactId 的风险情况在前面已经打印 warn 日志，所以这里不再 warn
            // 不过找到多个本身就可能有问题，可能是个 fat jar，不过比起没确定用那个问题不大，这里用 info日志
            LOG.info("chose {}\tfile:///{}for found more pom in jar", name, dirSpaceName);
        } else {
            pomFile = list.get(0);
        }
        String pomPath = k + Suffix.POM;
        if (!pomFile.renameTo(new File(pomPath))) {
            // 不把移动失败的路径写进去，避免移动失败后报找不到文件异常
            LOG.warn("move pom fail\tfile:///{}", dirSpaceName);
        } else {
            v.append(MvnKey.POM_FILE).append(pomPath);
        }
        // 删除多余的文件夹
    }
}
