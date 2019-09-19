package com.transfer;

import static com.transfer.config.Property.APP_CONTEXT_PATH;
import static com.transfer.config.Property.APP_PORT;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.transfer.service.transformer.GsonTransformer;

public abstract class AbstractIntegrationTest extends AbstractTest {

    private static ServerStarter serverStarter;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        serverStarter = new ServerStarter(propertyManager, new GsonTransformer());
        serverStarter.start();
    }

    @AfterSuite(alwaysRun = true)
    public void destroySuite() {
        if (serverStarter != null) {
            serverStarter.stop();
        }
    }

    protected String getBaseUrl() {
        return "http://localhost:" + propertyManager.getString(APP_PORT) + propertyManager.getString(APP_CONTEXT_PATH);
    }

    protected String getAccountUrl() {
        return getBaseUrl() + "/account";
    }

    protected String getTransactionUrl() {
        return getBaseUrl() + "/transaction";
    }

}