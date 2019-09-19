package com.transfer;

import com.transfer.config.PropertyManager;
import com.transfer.service.transformer.GsonTransformer;
import com.transfer.service.transformer.Transformer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Main {
    public static void main(String[] args) {

        Properties properties = new Properties();
        try (InputStream fis = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            log.error("Unable to load properties file", e);
            System.exit(0);
        }

        Transformer transformer = new GsonTransformer();

        new ServerStarter(
                new PropertyManager(properties),
                transformer
        ).start();
    }
}
