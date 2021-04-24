package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.maven.tool.deploy.task.repo.RepoUtils;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GavFromPath {

    private static final Logger LOG = LoggerFactory.getLogger(GavFromPath.class);

    private GavFromPath() {}

    /**
     * 在路径中获取 groupId artifactId version 并生成 pom
     * <br/>用 IDE 打包后本地安装上去的 jar 可能没有 pom 文件时使用
     * <br/>需要保证文件路径的准确，一般用在本地仓库直接推送，复制过来的文件不行
     * <br/>这种情况下如果 jar 有依赖会丢失
     */
    static boolean split(String tip, String k, StringBuilder v,
                         int deployDirLen,
                         boolean skipRepoHave, String cmdGet, int cmdTimeout) {
        String dirSpaceName = PathUtils.dirSpaceName(k);
        LOG.warn("not found pom in jar, get pom from path, would lost dependencies\t{}\tfile:///{}", tip, dirSpaceName);
        String version;
        String artifactId;
        String groupId;
        try {
            int dirFileIndex = k.lastIndexOf("/");
            if (dirFileIndex <= deployDirLen) {
                LOG.error("k.lastIndexOf(\"/\") = {} <= deployDirLen: {}, k={}",
                        dirFileIndex, deployDirLen, k);
                return false;
            }
            String gavPath = k.substring(deployDirLen, dirFileIndex);
            int avIndex = gavPath.lastIndexOf("/");
            if (avIndex <= 0) {
                LOG.error("gavPath.lastIndexOf(\"/\") = {}, gavPath={}", avIndex, gavPath);
                return false;
            }

            version = gavPath.substring(avIndex + 1);

            String gaPath = gavPath.substring(0, avIndex);
            int gaIndex = gaPath.lastIndexOf("/");
            if (gaIndex <= 0) {
                LOG.error("gaPath.lastIndexOf(\"/\") = {}, gaPath={}", gaIndex, gaPath);
                return false;
            }

            artifactId = gaPath.substring(gaIndex + 1);

            groupId = gaPath.substring(1, gaIndex).replace('/', '.');
        } catch (Exception e) {
            LOG.info("getGavFromPath Exception \t{}\tfile:///{}.pom", tip, dirSpaceName, e);
            return false;
        }
        LOG.warn("getGavFromPath: {}:{}:{}", groupId, artifactId, version);
        if (skipRepoHave && RepoUtils.haveGav(k, cmdGet, cmdTimeout,
                groupId, artifactId, version)) {
            LOG.info("skipRepoHave \t{}\tfile:///{}{}", tip, k, Suffix.GET_LOG);
            return false;
        }
        v.append(MvnKey.GROUP_ID).append(groupId);
        v.append(MvnKey.ARTIFACT_ID).append(artifactId);
        v.append(MvnKey.VERSION).append(version);
        return true;
    }
}
