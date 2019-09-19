package com.transfer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@AllArgsConstructor
@Slf4j
public class DbManager {

    private final DataSource dataSource;

    public void prepareDb() {
        try (Connection con = dataSource.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("SET DATABASE SQL SYNTAX PGS TRUE");

                //----

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ACCOUNT(" +
                        " ID BIGINT NOT NULL, " +
                        " ACCOUNT_NUMBER BIGINT NOT NULL UNIQUE, " +
                        " BALANCE BIGINT NOT NULL, " +
                        " CONSTRAINT ACCOUNT_PK PRIMARY KEY (ID))");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS ACCOUNT_NUM_IDX ON ACCOUNT (ACCOUNT_NUMBER)");
                stmt.executeUpdate("CREATE SEQUENCE IF NOT EXISTS ACCOUNT_SEQ   " +
                        "START WITH 1 " +
                        "INCREMENT BY 1");

                //---

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACTION(" +
                        " ID BIGINT NOT NULL, " +
                        " ACCOUNT_NUMBER BIGINT NOT NULL, " +
                        " TIMESTAMP TIMESTAMP NOT NULL, " +
                        " TYPE INT NOT NULL, " +
                        " SHIFT BIGINT NOT NULL, " +
                        " CONSTRAINT TRANSACTION_PK PRIMARY KEY (ID), " +
                        " FOREIGN KEY (ACCOUNT_NUMBER) REFERENCES ACCOUNT(ACCOUNT_NUMBER))");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS ACCOUNT_NUM_IDX ON ACCOUNT (ACCOUNT_NUMBER)");
                stmt.executeUpdate("CREATE SEQUENCE IF NOT EXISTS TRANSACTION_SEQ   " +
                        "START WITH 1 " +
                        "INCREMENT BY 1");
            }
        } catch (Throwable t) {
            log.error("Unable to init DB", t);
            throw new RuntimeException(t);
        }
    }

    public void clearDb() {
        try (Connection con = dataSource.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("DROP SEQUENCE IF EXISTS TRANSACTION_SEQ");
                stmt.executeUpdate("DROP TABLE IF EXISTS TRANSACTION");

                //----

                stmt.executeUpdate("DROP SEQUENCE IF EXISTS ACCOUNT_SEQ");
                stmt.executeUpdate("DROP TABLE IF EXISTS ACCOUNT");
            }
        } catch (Throwable t) {
            log.error("Unable to clear DB", t);
            throw new RuntimeException(t);
        }
    }
}
