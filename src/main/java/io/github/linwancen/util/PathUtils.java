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
            LOG.warn("getCanonicalPath IOException, use AbsolutePath file:///{}", path, e);
            return path;
        }
    }

    /**
     * 避免没有文件夹报错
     */
    public static void mkdir(File dir) {
        if (dir != null && !dir.exists() && dir.mkdirs()) {
            String path = PathUtils.canonicalPath(dir);
            LOG.debug("mkdir fail, file:///{}", path);
        }
    }

    public static String dirSpaceName(String dirFile) {
        int nameIndex = dirFile.lastIndexOf('/') + 1;
        String dir = dirFile.substring(0, nameIndex);
        String name = dirFile.substring(nameIndex);
        return dir + " " + name;
    }
}
