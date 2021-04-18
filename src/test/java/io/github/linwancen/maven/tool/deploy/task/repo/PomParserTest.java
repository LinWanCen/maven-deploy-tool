package io.github.linwancen.maven.tool.deploy.task.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class PomParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(PomParserTest.class);

    @Test
    public void testParse() {
        Pom pom = PomParser.parse(new File("pom.xml"));
        assertNotNull(pom);
        LOG.info("{}", pom);
    }
}