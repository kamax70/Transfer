package com.transfer.dao;

import com.transfer.service.model.TransactionInfo;
import com.transfer.service.model.TransactionType;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TransactionDaoImpl extends AbstractDao<TransactionInfo> implements TransactionDao {

    private static final String SELECT = "SELECT ID, ACCOUNT_NUMBER, TIMESTAMP, TYPE, SHIFT FROM TRANSACTION ";
    private static final String SELECT_BY_ACC_ID = SELECT + " WHERE ACCOUNT_NUMBER = ? ";
    private static final String SELECT_BY_ACC_ID_WITH_LIMIT = SELECT_BY_ACC_ID + " order by timestamp DESC limit ? ";

    private static final String CREATE = "INSERT INTO TRANSACTION(ID, ACCOUNT_NUMBER, TIMESTAMP, TYPE, SHIFT) " +
            "VALUES(nextval('TRANSACTION_SEQ'), ?, ?, ?, ?)";

    private final RowMapper<TransactionInfo> rowMapper;

    public TransactionDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.rowMapper = new TransactionInfoRowMapper();
    }

    @Override
    public void create(TransactionInfo transactionInfo) {
        update(CREATE, new TransactionInfoCreateSetter(transactionInfo));
    }

    @Override
    public List<TransactionInfo> getLast(long accountNumber, int limit) {
        return query(SELECT_BY_ACC_ID_WITH_LIMIT, rowMapper, new GetLastSetter(accountNumber, limit));
    }

    private static final class TransactionInfoRowMapper implements RowMapper<TransactionInfo> {

        @Override
        public TransactionInfo rowMap(ResultSet rs) throws SQLException {
            return TransactionInfo.builder()
                    .id(rs.getLong(1))
                    .accountNumber(rs.getLong(2))
                    .timestamp(rs.getTimestamp(3).getTime())
                    .transactionType(TransactionType.of(rs.getInt(4)))
                    .shift(rs.getLong(5))
                    .build();
        }
    }

    @AllArgsConstructor
    private static final class TransactionInfoCreateSetter implements PreparedStatementSetter {

        private final TransactionInfo transactionInfo;

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            ps.setLong(1, transactionInfo.getAccountNumber());
            ps.setTimestamp(2, new Timestamp(transactionInfo.getTimestamp()));
            ps.setLong(3, transactionInfo.getTransactionType().getType());
            ps.setLong(4, transactionInfo.getShift());
        }
    }

    @AllArgsConstructor
    private static final class GetLastSetter implements PreparedStatementSetter {

        private final long accountNumber;
        private final long limit;

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            ps.setLong(1, accountNumber);
            ps.setLong(2, limit);
        }
    }
}
