package com.hl;

import com.hl.java.util.CommUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class CommUtilsTest {

    @Test
    public void loadProperties() {
        String fileName = "datasource.properties";
        Properties properties = CommUtils.loadProperties(fileName);
        Assert.assertNotNull(properties);

    }
}