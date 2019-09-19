package com.transfer.dao;

import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public final class IdSetter implements PreparedStatementSetter {

    private final long id;

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setLong(1, id);
    }
}