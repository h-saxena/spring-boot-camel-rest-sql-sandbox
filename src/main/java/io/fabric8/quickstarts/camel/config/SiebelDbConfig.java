package io.fabric8.quickstarts.camel.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import io.fabric8.quickstarts.camel.db.custom.CustomNamedParameterJDBCTemplate;

@Configuration
@EnableTransactionManagement
//@EnableJpaRepositories(
//		  transactionManagerRef = "siebelTransactionManager",
//				  basePackages = "io.fabric8.quickstarts.camel.db.repo.crmp"
//		)
public class SiebelDbConfig  {
			
	@Value("${siebeldb.datasource.username}")
	private String username;

	@Value("${siebeldb.datasource.password}")
	private String password;

	@Value("${siebeldb.datasource.url}")
	private String url;

	@Value("${siebeldb.datasource.driver-class-name}")
	private String driverClassName;

	@Bean(name = "siebelDataSource")
	  public DataSource siebelDataSource() {	  
	    org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
	    dataSource.setDriverClassName(driverClassName);
	    dataSource.setUrl(url);
	    dataSource.setUsername(username);
	    dataSource.setPassword( password);
	    dataSource.setTestOnBorrow(true);
	    dataSource.setValidationQuery("select 1 from dual");
	    int validationTimeout = -1;
		if(validationTimeout > 0){
			dataSource.setValidationQueryTimeout(validationTimeout);
		}	    
	    boolean isEvictEnabled = false;
	    if(isEvictEnabled){		    
		    dataSource.setTestWhileIdle(isEvictEnabled); //Enable idle connection evictor.
		    int minEvictIdleTime = 60000;
		    dataSource.setMinEvictableIdleTimeMillis(minEvictIdleTime); // 105*60*1000 - Idle time can be 1 hour 45 mins since firewall setting is at 2 hours.
		    int timeBetweenEvictRuns = 5000;
		    dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictRuns);// 10*60*1000 - Eviction in every 10 mins.
	    }
	    //dataSource.setConnectionProperties("JtaManaged=true;javax.persistence.query.timeout=10;oracle.jdbc.ReadTimeout=10");
	    dataSource.setConnectionProperties("JtaManaged=true;javax.persistence.query.timeout=10");
	    return dataSource;
	  }
	
	@Bean
	//@ConditionalOnProperty(prefix = "siebeldb.datasource", name = "jmx-enabled", havingValue="true")
	public Object siebelDataSourceMBean(@Qualifier("siebelDataSource") DataSource dataSource) {
    Map<String,Object> properties = new HashMap<>();
    properties.put("javax.persistence.query.timeout", 10000);
	  
	    if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
	        try {
	            return ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).createPool().getJmxPool();
	        }
	        catch (SQLException ex) {	            
	        }
	    }
	    return null;
	}
	
	@Bean(name = "siebelTransactionManager")
	public PlatformTransactionManager siebelTransactionManager(
			@Qualifier(value = "siebelDataSource") DataSource siebelDataSource) {
	  DataSourceTransactionManager tm =  new DataSourceTransactionManager(siebelDataSource);
	  tm.setDefaultTimeout(20);
	  return tm;
	}

	@Bean(name = "siebelJdbcTemplate")
	public NamedParameterJdbcTemplate siebelJdbcTemplate(@Qualifier(value = "siebelDataSource") DataSource dataSource) {
	  JdbcTemplate template = new JdbcTemplate(dataSource);
	  template.setQueryTimeout(10);
		return new NamedParameterJdbcTemplate(template);
	}	

	@Bean(name = "siebelJdbcTemplateCore")
	public JdbcTemplate siebelJdbcTemplate2(@Qualifier(value = "siebelDataSource") DataSource dataSource) {
	  JdbcTemplate template = new JdbcTemplate(dataSource);
	  template.setQueryTimeout(10);
		return template;
	}	

	@Bean
	public CustomNamedParameterJDBCTemplate CustomNamedParameterJDBCTemplate(@Qualifier(value = "siebelDataSource") DataSource dataSource) {
	  CustomNamedParameterJDBCTemplate template = new CustomNamedParameterJDBCTemplate(dataSource);
		return template;
	}	

}
