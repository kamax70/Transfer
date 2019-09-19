package com.transfer;

import com.transfer.dao.DataSourceManager;
import com.transfer.dao.DataSourceManagerImpl;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.sql.DataSource;

public class AbstractDaoTest extends AbstractTest {

    private static DbManager dbManager;
    protected static DataSource dataSource;

    @BeforeSuite(alwaysRun = true)
    public void initDaoSuite() {
        DataSourceManager dataSourceManager = new DataSourceManagerImpl(propertyManager);
        dataSource = dataSourceManager.getDataSource();
        dbManager = new DbManager(dataSource);
        dbManager.prepareDb();
    }

    @AfterSuite(alwaysRun = true)
    public void destroyDaoSuite() {
            if (dbManager != null) {
            dbManager.clearDb();
        }
    }
}
