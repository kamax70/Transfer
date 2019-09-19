package com.transfer.dao;

import com.transfer.service.model.AccountInfo;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDaoImpl extends AbstractDao<AccountInfo> implements AccountDao {

    private static final String SELECT = "SELECT ID, ACCOUNT_NUMBER, BALANCE FROM ACCOUNT ";
    private static final String SELECT_BY_ACC_ID = SELECT + " WHERE ACCOUNT_NUMBER = ? ";
    private static final String UPDATE_ON_TRANSACTION = "UPDATE ACCOUNT a " +
            " SET a.BALANCE = " +
            "   CASE a.ACCOUNT_NUMBER " +
            "       WHEN ? THEN a.BALANCE - ?" +
            "       WHEN ? THEN a.BALANCE + ?" +
            "   END" +
            " WHERE (a.ACCOUNT_NUMBER = ? or ACCOUNT_NUMBER = ?) " +
            "   and exists(SELECT 1 FROM ACCOUNT WHERE ACCOUNT_NUMBER = ? AND BALANCE >= ?)" +
            "   and exists(SELECT 1 FROM ACCOUNT WHERE ACCOUNT_NUMBER = ?)";


    private static final String CREATE = "INSERT INTO ACCOUNT(ID, ACCOUNT_NUMBER, BALANCE) " +
            "VALUES(nextval('ACCOUNT_SEQ'), ?, ?)";

    private final RowMapper<AccountInfo> rowMapper;

    public AccountDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.rowMapper = new AccountInfoRowMapper();
    }

    @Override
    public void create(AccountInfo accountInfo) {
        update(CREATE, new AccountInfoCreateSetter(accountInfo));
    }

    @Override
    public int updateByTransaction(long accountNumberFrom, long accountNumberTo, long shift) {
        return update(UPDATE_ON_TRANSACTION, new OnTransactionSetter(accountNumberFrom, accountNumberTo, shift));
    }

    @Override
    public AccountInfo getByAccNumber(long accountNumber) {
        return queryForSingleResult(SELECT_BY_ACC_ID, rowMapper, accountNumber);
    }

    private static final class AccountInfoRowMapper implements RowMapper<AccountInfo> {

        @Override
        public AccountInfo rowMap(ResultSet rs) throws SQLException {
            return com.transfer.service.model.AccountInfo.builder()
                    .id(rs.getLong(1))
                    .accountNumber(rs.getLong(2))
                    .balance(rs.getLong(3))
                    .build();
        }
    }

    @AllArgsConstructor
    private static final class AccountInfoCreateSetter implements PreparedStatementSetter {

        private final AccountInfo accountInfo;

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            ps.setLong(1, accountInfo.getAccountNumber());
            ps.setLong(2, accountInfo.getBalance());
        }
    }

    @AllArgsConstructor
    private static final class OnTransactionSetter implements PreparedStatementSetter {

        private final long accountFrom;
        private final long accountTo;
        private final long shift;

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            ps.setLong(1, accountFrom);
            ps.setLong(2, shift);
            ps.setLong(3, accountTo);
            ps.setLong(4, shift);
            ps.setLong(5, accountFrom);
            ps.setLong(6, accountTo);
            ps.setLong(7, accountFrom);
            ps.setLong(8, shift);
            ps.setLong(9, accountTo);
        }
    }
}
