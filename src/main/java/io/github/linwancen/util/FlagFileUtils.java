package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FlagFileUtils {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String SUCC_SUFFIX = ".succ";
    public static final String LOCK_SUFFIX = ".lock";
    public static final String FAIL_SUFFIX = ".fail";
    public static final String[] SUFFIXES = {
            SUCC_SUFFIX,
            LOCK_SUFFIX,
            FAIL_SUFFIX,
    };
    private static final Logger LOG = LoggerFactory.getLogger(FlagFileUtils.class);

    private FlagFileUtils() {}

    public static void update(String pathPrefix, String suffix, String... deleteSuffixes) {
        for (String s : deleteSuffixes) {
            deleteFile(new File(pathPrefix + s));
        }
        File file = new File(pathPrefix + suffix);
        deleteFile(file);
        try {
            if (file.createNewFile()) {
                return;
            }
            String dirSpaceName = PathUtils.dirSpaceName(PathUtils.canonicalPath(file));
            LOG.warn("have not create file:///{}", dirSpaceName);
        } catch (IOException e) {
            String dirSpaceName = PathUtils.dirSpaceName(PathUtils.canonicalPath(file));
            LOG.warn("Exception for create file:///{}", dirSpaceName, e);
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (!file.delete()) {
                String dirSpaceName = PathUtils.dirSpaceName(PathUtils.canonicalPath(file));
                LOG.warn("have not delete file:///{}", dirSpaceName);
            }
        }
    }
}
