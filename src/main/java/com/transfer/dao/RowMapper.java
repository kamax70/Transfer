package com.transfer.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

        T rowMap(ResultSet rs) throws SQLException;
}
