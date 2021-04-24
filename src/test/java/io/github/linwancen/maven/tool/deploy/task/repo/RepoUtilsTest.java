package io.github.linwancen.maven.tool.deploy.task.repo;

import io.github.linwancen.maven.tool.Conf;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.maven.tool.deploy.CmdPrefix;
import io.github.linwancen.util.ConfUtils;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class RepoUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepoUtilsTest.class);

    @BeforeClass
    public void before() {
        Conf.prop = ConfUtils.loadUtf8(Conf.CONF_PATH);
    }

    @Test
    public void testHave() {

    }

    @Test
    public void testHaveGav() {
        String k = "target/testHaveGav";
        boolean b = RepoUtils.haveGav(k, CmdPrefix.get(), 0,
                "commons-cli", "commons-cli", "1.4");
        File file = new File(k);
        String path = PathUtils.canonicalPath(file);
        LOG.info("\tfile:///{}{}", path, Suffix.GET_LOG);
        assertTrue(b);
    }

    @Test
    public void testNotHaveGav() {
        String k = "target/testNotHaveGav";
        boolean b = RepoUtils.haveGav(k, CmdPrefix.get(), 0,
                "commons-cli", "NotCommons-cli", "1.4");
        File file = new File(k);
        String path = PathUtils.canonicalPath(file);
        LOG.info("\tfile:///{}{}", path, Suffix.GET_LOG);
        assertFalse(b);
    }
}