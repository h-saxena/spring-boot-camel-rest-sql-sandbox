package io.fabric8.quickstarts.camel.db.custom;

import java.sql.PreparedStatement;

import org.springframework.jdbc.core.PreparedStatementCreator;

public class CustomPreparedStatementCreatorWrapper {
	private PreparedStatementCreator preparedStatementCreator;
	private PreparedStatement[] registeredPreparedStatementList;
	
	public CustomPreparedStatementCreatorWrapper() {
		
	}

	public PreparedStatementCreator getPreparedStatementCreator() {
		return preparedStatementCreator;
	}

	public void setPreparedStatementCreator(PreparedStatementCreator preparedStatementCreator) {
		this.preparedStatementCreator = preparedStatementCreator;
	}

	public PreparedStatement[] getRegisteredPreparedStatementList() {
		return registeredPreparedStatementList;
	}

	public void setRegisteredPreparedStatementList(PreparedStatement[] registeredPreparedStatementList) {
		this.registeredPreparedStatementList = registeredPreparedStatementList;
	}
	
	
}
