package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FlagFileUtils {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String SUCC_SUFFIX = ".succ";
    public static final String FAIL_SUFFIX = ".fail";
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
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                String dirSpaceName = PathUtils.dirSpaceName(PathUtils.canonicalPath(file));
                LOG.warn("Files.delete IOException file:///{}", dirSpaceName, e);
            }
        }
    }
}
