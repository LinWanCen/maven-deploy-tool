package io.github.linwancen.util;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * <br/>info 打印命令
 * <br/>debug 打印工作目录
 * <br/>trace 打印命令执行日志
 */
public class CmdUtils {

    /** 默认超时时间 10 分钟 */
    public static final int DEFAULT_TIMEOUT = 1000 * 60 * 10;
    public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
    private static final Logger LOG = LoggerFactory.getLogger(CmdUtils.class);

    private CmdUtils() {}

    /**
     * @return exitCode
     */
    public static int exec(String cmd, int timeout, File logFile) {
        return exec(cmd, null, timeout, logFile);
    }

    /**
     * @return exitCode
     */
    public static int exec(String cmd, File workingDirectory, int timeout, File logFile) {
        LOG.info("executing command\t{}", cmd);

        Executor executor = new DefaultExecutor();

        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }

        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
        }
        if (LOG.isDebugEnabled()) {
            File file = executor.getWorkingDirectory();
            String path = PathUtils.canonicalPath(file);
            LOG.debug("workdir\tfile:///{}", path);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(out, err));
        executor.setExitValue(0);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        try {
            if (IS_WINDOWS) {
                // 必须用 parse 而不是 new 否则会被合在一起
                executor.execute(CommandLine.parse("cmd.exe /C " + cmd), resultHandler);
            } else {
                executor.execute(CommandLine.parse(cmd), resultHandler);
            }
        } catch (IOException e) {
            LOG.error("execute IOException, cmd:\t{} ", cmd, e);
            return 1;
        }

        try {
            resultHandler.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("waitFor InterruptedException, cmd:\t{}", cmd, e);
            return 1;
        }

        final int exitCode = resultHandler.getExitValue();
        byte[] commandLineBytes = (cmd + "\n").getBytes(StandardCharsets.UTF_8);
        try {
            LOG.trace("exec err log:\n{}", err);
            LOG.trace("exec out out:\n{}", out);
            if (logFile != null) {
                Path path = logFile.toPath();
                PathUtils.mkdir(logFile.getParentFile());
                Files.write(path, commandLineBytes);
                Files.write(path, err.toByteArray(), StandardOpenOption.APPEND);
                Files.write(path, "\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                Files.write(path, out.toByteArray(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            String path = PathUtils.canonicalPath(logFile);
            LOG.error("write logFile IOException\tfile:///{}", path, e);
        }

        // 异常的情况下才检查 超时进程被看门狗杀死
        if (exitCode != 0 && executor.getWatchdog().killedProcess()) {
            LOG.error("execute timeout: {}, cmd:\t{}", timeout, cmd);
        }
        return exitCode;
    }
}
