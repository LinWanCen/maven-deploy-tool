package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class PackagingFromPom {

    private static final Logger LOG = LoggerFactory.getLogger(PackagingFromPom.class);

    private PackagingFromPom() {}

    /**
     * 不支持其他类型文件推送，没有 jar 默认 pom 文件
     */
    static boolean read(String tip, String k, StringBuilder v) {
        String line;
        boolean havePackaging = false;
        File pomFile = new File(k + Suffix.POM);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pomFile)))) {
            while ((line = br.readLine()) != null) {
                if (line.trim().contains("<packaging>pom</packaging>")) {
                    havePackaging = true;
                    break;
                }
            }
        } catch (IOException e) {
            LOG.warn("getPackagingFromPom IOException \t{}\tfile:///{}", tip, PathUtils.canonicalPath(pomFile), e);
            return false;
        }
        if (!havePackaging) {
            LOG.error("not found jar, packaging is not pom\t{}\tfile:///{}", tip, PathUtils.dirSpaceName(k));
            return false;
        }
        LOG.info("not found jar, set packaging=pom\t{}\tfile:///{}.pom", tip, PathUtils.dirSpaceName(k));
        v.append(MvnKey.POM_PACKAGING_FILE).append(k).append(Suffix.POM);
        return true;
    }

}
