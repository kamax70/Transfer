package com.transfer.dao;

import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.ExecutionException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
abstract class AbstractDao<T> {

    private final DataSource dataSource;

    AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    int update(String sql, PreparedStatementSetter setter) {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                setter.setValues(ps);
                int updated = ps.executeUpdate();
                con.commit();
                return updated;
            } catch (Throwable t) {
                log.error("Sql exception on update", t);
                con.rollback();
                throw new RuntimeException(t);
            }
        } catch (Throwable t) {
            log.error("Unexpected exception on SQL update", t);
            throw new ExecutionException(ErrorCode.INTERNAL_ERROR, "Unexpected server error");
        }
    }

    final T queryForSingleResult(String sql, RowMapper<T> rowMapper, PreparedStatementSetter setter) {
        List<T> list = query(sql, rowMapper, setter);
        return list.isEmpty() ? null : list.get(0);
    }

    final T queryForSingleResult(String sql, RowMapper<T> rowMapper, long id) {
        List<T> list = query(sql, rowMapper, new IdSetter(id));
        return list.isEmpty() ? null : list.get(0);
    }

    final List<T> query(String sql, RowMapper<T> rowMapper, PreparedStatementSetter setter) {
        try (Connection con = dataSource.getConnection()) {
            List<T> resultList = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                setter.setValues(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        T row = rowMapper.rowMap(rs);
                        resultList.add(row);
                    }
                }
            }
            return resultList;
        } catch (SQLException e) {
            log.error("Sql exception on select", e);
            throw new ExecutionException(ErrorCode.INTERNAL_ERROR, "Unexpected server error");
        }
    }
}