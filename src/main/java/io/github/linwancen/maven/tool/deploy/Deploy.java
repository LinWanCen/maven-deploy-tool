package io.github.linwancen.maven.tool.deploy;

import io.github.linwancen.maven.tool.Conf;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.maven.tool.deploy.task.DeployTask;
import io.github.linwancen.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * <br/>info 打印成功进度避免觉得卡死
 */
public class Deploy {

    private static final Logger LOG = LoggerFactory.getLogger(Deploy.class);

    private Deploy() {}

    public static void run(Map<String, StringBuilder> paramMap, File deployDir) {
        int size = paramMap.size();
        String deployPath = PathUtils.canonicalPath(deployDir);
        LOG.info("begin deploy:{} file:///{}", size, deployPath);

        final CountDownLatch count = new CountDownLatch(size);
        final int deployDirLen = deployPath.length();
        // 在方法中获取而不是成员变量中获取以便支持重设
        final boolean getGavFromPath = "true".equals(EnvUtils.get("getGavFromPath", Conf.prop));
        final boolean skipSuccess = "true".equals(EnvUtils.get("skipSuccess", Conf.prop));
        final boolean skipRepoHave = "true".equals(EnvUtils.get("skipRepoHave", Conf.prop));
        final boolean notUpdatingSuccess = "true".equals(EnvUtils.get("notUpdatingSuccess", Conf.prop));

        final int threadMultiplier = Integer.parseInt(EnvUtils.get("threadMultiplier", Conf.prop));
        final int cmdTimeout;
        Double calculate = JsUtils.calculate(EnvUtils.get("cmdTimeout", Conf.prop).replace("_", ""));
        if (calculate == null) {
            cmdTimeout = 1000 * 60 * 60 * 60;
            LOG.warn("set cmdTimeout 1000 * 60 * 60 * 60 = {}", cmdTimeout);
        } else {
            cmdTimeout = calculate.intValue();
        }

        String cmdDeploy = CmdPrefix.deploy();
        String cmdGet = CmdPrefix.get();

        int index = 0;
        for (final Map.Entry<String, StringBuilder> entry : paramMap.entrySet()) {
            index++;
            final String tip = index + "/" + size;
            if (skipSuccess) {
                String k = entry.getKey();
                File successFile = new File(k + Suffix.DEPLOY + FlagFileUtils.SUCC_SUFFIX);
                if (successFile.exists()) {
                    String dirSpaceName = PathUtils.dirSpaceName(k);
                    LOG.info("end-deploy skip success\t{}\tfile:///{}", tip, dirSpaceName);
                    count.countDown();
                    continue;
                }
            }
            // 不在构方法中避免添加字段时需要频繁修改构造方法
            DeployTask task = new DeployTask();
            task.count = count;
            task.entry = entry;
            // int
            task.deployDirLen = deployDirLen;
            task.cmdTimeout = cmdTimeout;
            // String
            task.tip = tip;
            task.cmdDeploy = cmdDeploy;
            task.cmdGet = cmdGet;
            // boolean
            task.getGavFromPath = getGavFromPath;
            task.skipRepoHave = skipRepoHave;
            task.notUpdatingSuccess = notUpdatingSuccess;
            ThreadsPools.get("run-cmd-%d", threadMultiplier).execute(task);
        }
        try {
            count.await();
            // 这里的日志前缀没有横线跟 DeployTask 区分开来
            LOG.info("end deploy:{} file:///{}", size, deployPath);
        } catch (InterruptedException e) {
            LOG.error("count.await exception.", e);
            Thread.currentThread().interrupt();
        }
    }

}
