package io.github.linwancen.util;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class CmdUtilsTest {

    @Test
    public void testExec() {
        int exitCode = CmdUtils.exec("echo hello", 600_000, new File("target/CmdUtilsTest.log"));
        assertEquals(exitCode, 0);
    }
}