package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.*;

public class JsUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(JsUtilsTest.class);

    @Test
    public void testCalculate() {
        String express = "1_024 * 1024 * 50";
        Double calculate = JsUtils.calculate(express);
        assertNotNull(calculate);
        assertEquals(calculate, Double.valueOf(1024 * 1024 * 50));
        String s = BigDecimal.valueOf(calculate).toPlainString();
        LOG.info("{} = {}", express, s);
    }
}