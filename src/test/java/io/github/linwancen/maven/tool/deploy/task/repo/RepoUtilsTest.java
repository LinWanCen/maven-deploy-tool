package io.github.linwancen.maven.tool.deploy.task.repo;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RepoUtilsTest {

    @Test
    public void testHave() {

    }

    @Test
    public void testHaveGav() {
        boolean b = RepoUtils.haveGav("target/testHaveGav", 0,
                "commons-cli", "commons-cli", "1.4");
        assertTrue(b);
    }

    @Test
    public void testNotHaveGav() {
        boolean b = RepoUtils.haveGav("target/testNotHaveGav", 0,
                "commons-cli", "Notcommons-cli", "1.4");
        assertFalse(b);
    }
}