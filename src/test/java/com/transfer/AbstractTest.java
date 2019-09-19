package com.transfer;

import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;

import com.transfer.config.PropertyManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTest {

    protected static PropertyManager propertyManager;

    @BeforeSuite(alwaysRun = true)
    public void initSuite() {
        Properties properties = new Properties();
        try (InputStream fis = Main.class.getClassLoader().getResourceAsStream("testConfig.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            fail("Unable to parse test properties file");
        }
        propertyManager = new PropertyManager(properties);
    }
}
