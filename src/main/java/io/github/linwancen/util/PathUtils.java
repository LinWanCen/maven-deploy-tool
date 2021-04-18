package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PathUtils {

    @SuppressWarnings("ConstantConditions")
    public static final String CLASS_PATH = ClassLoader.getSystemClassLoader().getResource("").getPath();
    private static final Logger LOG = LoggerFactory.getLogger(PathUtils.class);

    private PathUtils() {}

    public static String canonicalPath(File file) {
        try {
            return file.getCanonicalPath().replace('\\', '/');
        } catch (IOException e) {
            String path = file.getAbsolutePath().replace('\\', '/');
            LOG.warn("getCanonicalPath IOException {}\n  use AbsolutePath file:///{}",
                    e.getLocalizedMessage(), path, e);
            return path;
        }
    }

    /**
     * 避免没有文件夹报错
     */
    public static void mkdirParent(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            if (!parentFile.exists() && parentFile.mkdirs()) {
                LOG.debug("mkdirs fail, file:///{}", PathUtils.canonicalPath(parentFile));
            }
        }
    }

    public static String dirSpaceName(String dirFile) {
        int nameIndex = dirFile.lastIndexOf('/') + 1;
        String dir = dirFile.substring(0, nameIndex);
        String name = dirFile.substring(nameIndex);
        return dir + " " + name;
    }
}
