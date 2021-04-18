package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class UnZipUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(UnZipUtilsTest.class);

    @SuppressWarnings("ConstantConditions")
    public static final String TEST_CLASS_PATH = ClassLoader.getSystemClassLoader().getResource("").getPath();

    @Test
    public void testUnZip() {
        ArrayList<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                "\\\\/META-INF\\maven/io.github.linwancen\\demo\\pom.xml"
        );
        assertEquals(list.size(), 1);
        for (File file : list) {
            LOG.info("testUnZip file:///{}", PathUtils.canonicalPath(file));
        }
    }

    @Test
    public void testUnZipPattern() {
        ArrayList<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                Pattern.compile("pom.xml"),
                null
        );
        assertEquals(list.size(), 2);
        for (File file : list) {
            LOG.info("testUnZipPattern file:///{}", PathUtils.canonicalPath(file));
        }
    }

    @Test
    public void testUnZipIE() {
        ArrayList<File> list = UnZipUtils.unZip(
                new File(TEST_CLASS_PATH, "test.jar"),
                new File(TEST_CLASS_PATH),
                Pattern.compile("pom.xml"),
                Pattern.compile("other")
        );
        assertEquals(list.size(), 1);
        for (File file : list) {
            LOG.info("testUnZipIE file:///{}", PathUtils.canonicalPath(file));
        }
    }
}