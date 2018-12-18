package io.fabric8.quickstarts.camel.db.repo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;

public class CustomRowMapper implements RowMapper<Map<String, String>> {

    @Override
    public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        Map<String, String> row = new HashMap<>();

        for (int i = 1; i <= md.getColumnCount(); i++) {
            String colName = md.getColumnName(i);
            colName = underscoreToCamelCase(colName);
            String colValue = rs.getString(i);
            row.put(colName, colValue);
        }
        return row;
    }

    private String underscoreToCamelCase(String source) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, source);
    }
}
