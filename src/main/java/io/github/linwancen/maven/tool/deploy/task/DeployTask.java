package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.maven.tool.deploy.task.repo.RepoUtils;
import io.github.linwancen.util.CmdUtils;
import io.github.linwancen.util.FlagFileUtils;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class DeployTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DeployTask.class);

    public CountDownLatch count;
    public Map.Entry<String, StringBuilder> entry;
    public int deployDirLen;
    public int cmdTimeout;
    public String tip;
    public String cmdPrefix;
    public boolean getGavFromPath;
    public boolean notUpdatingSuccess;
    public boolean skipRepoHave;

    @Override
    public void run() {
        try {
            String pathPrefix = entry.getKey() + Suffix.DEPLOY;
            if (deploy()) {
                FlagFileUtils.update(pathPrefix, FlagFileUtils.SUCC_SUFFIX, FlagFileUtils.FAIL_SUFFIX);
            } else {
                FlagFileUtils.update(pathPrefix, FlagFileUtils.FAIL_SUFFIX, FlagFileUtils.SUCC_SUFFIX);
            }
        } finally {
            // 在 finally 中 countDown 避免异常等情况导致没有执行而卡住主线程
            count.countDown();
        }
    }

    private boolean deploy() {
        String k = entry.getKey();
        StringBuilder v = entry.getValue();
        v.insert(0, cmdPrefix);
        String cmd = v.toString();
        // 没有 pom 文件就在 jar 解压出来
        if (!cmd.contains(MvnKey.POM_FILE)) {
            // 如果没有 jar 文件直接报错跳过
            if (!cmd.contains(MvnKey.JAR_PACKAGING_FILE)) {
                String dirSpaceName = PathUtils.dirSpaceName(k);
                LOG.error("have no pom and jar\t{}\tfile:///{}", tip, dirSpaceName);
                return false;
            }
            PomFromJar.unzip(tip, k, v);
            cmd = v.toString();
        }
        // 没有 jar 文件就在路径中获取 groupId artifactId version 并生成 pom
        if (!cmd.contains(MvnKey.POM_FILE)) {
            if (!getGavFromPath) {
                String dirSpaceName = PathUtils.dirSpaceName(k);
                LOG.error("have no pom and getPomFromJar fail\t{}\tfile:///{}", tip, dirSpaceName);
                return false;
            }
            if (!GavFromPath.split(tip, k, v, deployDirLen,
                    skipRepoHave, cmdTimeout)) {
                // 在上面的方法里打了日志了，所以这里不再打一次
                return false;
            }
        } else {
            if (skipRepoHave && RepoUtils.have(k, cmdTimeout)) {
                LOG.info("skipRepoHave \t{}\tfile:///{}{}", tip, k, Suffix.GET_LOG);
                return true;
            }
            // 一般情况下不需要生成 pom 文件
            v.append(MvnKey.NO_GEN_POM);
        }
        cmd = v.toString();
        // 没有 jar 则判断是不是 pom 文件推送 pom
        if (!cmd.contains(MvnKey.JAR_PACKAGING_FILE)) {
            if (!PackagingFromPom.read(tip, k, v)) {
                // 在上面的方法里打了日志了，所以这里不再打一次
                return false;
            }
            cmd = v.toString();
        }
        // 执行命令
        File logFile = new File(k + Suffix.DEPLOY_LOG);
        int exitCode = CmdUtils.exec(cmd, cmdTimeout, logFile);
        // 这里的日志前缀是唯一的，便于查找筛选所有上传日志
        if (exitCode != 0) {
            String failLog = MvnLog.failLog(logFile);
            if (notUpdatingSuccess && failLog != null && MvnLog.isNotUpdating(failLog)) {
                // 可能有不规范的项目 RELEASE 包更新了内容，这里没更新所以为 warn
                LOG.warn("end-deploy not updating success\t{}\tfile:///{}{}", tip, k, Suffix.DEPLOY_LOG);
                return true;
            } else {
                if (failLog != null) {
                    failLog = failLog.replace(": ", ":\n  ");
                }
                LOG.error("end-deploy error code: {}\t{}\tfile:///{}{}\n  {}",
                        exitCode, tip, k, Suffix.DEPLOY_LOG, failLog);
                return false;
            }
        } else {
            // 虽然没看的必要，但可以看进度，不会产生长期卡住的错觉
            LOG.info("end-deploy success\t{}\tfile:///{}{}", tip, k, Suffix.DEPLOY_LOG);
            return true;
        }
    }
}
