package io.github.linwancen.maven.tool.deploy.task;

import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class MvnLog {

    private static final Logger LOG = LoggerFactory.getLogger(MvnLog.class);
    private static final String FAIL_LOG_PREFIX = "[ERROR] Failed to execute";
    private static final String NOT_UPDATING_LOG = "Repository does not allow updating assets";

    private MvnLog() {}

    static boolean isNotUpdating(String failLog) {
        return failLog.contains(NOT_UPDATING_LOG);
    }

    static String failLog(File logFile) {
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(FAIL_LOG_PREFIX)) {
                    break;
                }
            }
            return line;
        } catch (IOException e) {
            String path = PathUtils.canonicalPath(logFile);
            LOG.warn("read failLog IOException\tfile:///{}", path, e);
            return null;
        }
    }
}
