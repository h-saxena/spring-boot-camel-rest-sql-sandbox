package io.fabric8.quickstarts.camel.db.repo.siebel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.fabric8.quickstarts.camel.db.custom.CustomNamedParameterJDBCTemplate;
import io.fabric8.quickstarts.camel.db.custom.CustomPreparedStatementCreatorWrapper;
import io.fabric8.quickstarts.camel.db.repo.CustomRowMapper;

@Repository
public class SiebelViewRepo {
  @Autowired
  @Qualifier(value = "siebelDataSource") 
  DataSource dataSource;

  @Autowired
  @Qualifier(value = "siebelJdbcTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  @Qualifier(value = "siebelJdbcTemplateCore")
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private CustomNamedParameterJDBCTemplate customNamedParameterJDBCTemplate;
  
  
  //@Transactional(value="siebelTransactionManager",readOnly = true, timeout=15)
  public List<Map<String, String>> query(String sql, Map<String, Object> parameters) {
    return namedParameterJdbcTemplate.query(sql, parameters, new CustomRowMapper());
  }

  //@Transactional(value="siebelTransactionManager",readOnly = true, timeout=10)
  public List<Map<String, String>> queryCore(String sql, Object[] parameters) {
	//return jdbcTemplateCore.query(sql, parameters, new CustomRowMapper());
	  final PreparedStatement[] stmt = new PreparedStatement[1];

	  ExecutorService executor = Executors.newSingleThreadExecutor();
	  executor.submit(() -> {
	      String threadName = Thread.currentThread().getName();
	      System.out.println("WatchDog thread started: " + threadName);
		  try {
			  	Thread.sleep(20000);
			  	if(!stmt[0].isClosed())
				stmt[0].cancel();
		  }
		  catch (InterruptedException e) {
			  System.out.println("WatchDog thread Interrupted ~~~~~: " + threadName);		  }
		  catch (Exception e) {
				e.printStackTrace();
		  }
		  System.out.println("WatchDog thread Ended ++++++++++++++: " + threadName);
	  });
	  
	  List<Map<String, String>> results = jdbcTemplate.query(new PreparedStatementCreator() {
		
		@Override
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			stmt[0] = con.prepareStatement(sql);;
			stmt[0].setString(1, "1-2J-4367");
			//stmt[0].setQueryTimeout(5);
			return stmt[0];
		}
	  }, new CustomRowMapper());  
	  
	  executor.shutdownNow();
	  return results;
  }

  public List<Map<String, String>> queryCoreCustomTemplate(String sql, Map<String, Object> parameters) {
	  CustomPreparedStatementCreatorWrapper creatorWrapper = customNamedParameterJDBCTemplate.getCustomPreparedStatementCreator(sql, parameters);
	  PreparedStatement[] stmt = creatorWrapper.getRegisteredPreparedStatementList();
	  PreparedStatementCreator preparedStatementCreator= creatorWrapper.getPreparedStatementCreator();
	  
	  ExecutorService executor = Executors.newSingleThreadExecutor();
	  executor.submit(() -> {
	      String threadName = Thread.currentThread().getName();
	      System.out.println("WatchDog thread started: " + threadName);
		  try {
			  	Thread.sleep(80000);
			  	if(!stmt[0].isClosed())
				stmt[0].cancel();
		  }
		  catch (InterruptedException e) {
			  System.out.println("WatchDog thread Interrupted ~~~~~: " + threadName);		  }
		  catch (Exception e) {
				e.printStackTrace();
		  }
		  System.out.println("WatchDog thread Ended ++++++++++++++: " + threadName);
	  });
	  
	  List<Map<String, String>> results = jdbcTemplate.query(preparedStatementCreator, new CustomRowMapper());  
	  
	  executor.shutdownNow();
	  return results;
  }

}
