package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class UnZipUtilsTest {

    @SuppressWarnings("ConstantConditions")
    public static final String TEST_CLASS_PATH = ClassLoader.getSystemClassLoader().getResource("").getPath();

    private static final Logger LOG = LoggerFactory.getLogger(UnZipUtilsTest.class);

    @Test
    public void testUnZip() {
        List<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                "\\\\/META-INF\\maven/io.github.linwancen\\demo\\pom.xml"
        );
        assertEquals(list.size(), 1);
        for (File file : list) {
            String path = PathUtils.canonicalPath(file);
            LOG.info("testUnZip\tfile:///{}", path);
        }
    }

    @Test
    public void testUnZipPattern() {
        List<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                Pattern.compile("pom.xml"),
                null
        );
        assertEquals(list.size(), 2);
        for (File file : list) {
            String path = PathUtils.canonicalPath(file);
            LOG.info("testUnZipPattern\tfile:///{}", path);
        }
    }

    @Test
    public void testUnZipInclusionExclusion() {
        List<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                Pattern.compile("pom.xml"),
                Pattern.compile("other")
        );
        assertEquals(list.size(), 1);
        for (File file : list) {
            String path = PathUtils.canonicalPath(file);
            LOG.info("testUnZipInclusionExclusion\tfile:///{}", path);
        }
    }
}