package com.transfer.dao;

import com.transfer.config.PropertyManager;
import lombok.extern.slf4j.Slf4j;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;

import static com.transfer.config.Property.*;

@Slf4j
public class DataSourceManagerImpl implements DataSourceManager {

    private final DataSource dataSource;

    public DataSourceManagerImpl(PropertyManager propertyManager) {
        try {
            Class.forName(propertyManager.getString(DB_DRIVER));
        } catch (ClassNotFoundException e) {
            log.error("Unable to initialize DataSource", e);
            throw new RuntimeException(e);
        }

        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl(propertyManager.getString(DB_URL));
        dataSource.setUser(propertyManager.getString(DB_SCHEMA));
        dataSource.setPassword(propertyManager.getString(DB_PASSWORD));

        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
