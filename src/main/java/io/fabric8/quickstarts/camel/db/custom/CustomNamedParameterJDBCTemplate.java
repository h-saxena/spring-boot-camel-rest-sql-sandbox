package io.fabric8.quickstarts.camel.db.custom;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

//@Component
public class CustomNamedParameterJDBCTemplate extends NamedParameterJdbcTemplate {
	
	public CustomNamedParameterJDBCTemplate(DataSource ds) {
		super(ds);
	}
	
	public CustomPreparedStatementCreatorWrapper getCustomPreparedStatementCreator(String sql, Map<String, ?> paramMap) {
		SqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
		ParsedSql parsedSql = getParsedSql(sql);
		String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
		Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
		List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
		CustomPreparedStatementCreatorFactory pscf = new CustomPreparedStatementCreatorFactory(sqlToUse, declaredParameters);
		return pscf.newPreparedStatementCreator(params);
	}
	


}
