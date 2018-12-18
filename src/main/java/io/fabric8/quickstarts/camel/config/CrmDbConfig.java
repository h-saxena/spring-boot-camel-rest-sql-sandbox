package io.fabric8.quickstarts.camel.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.fabric8.quickstarts.camel.config.model.DbConfig;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", basePackages = "io.fabric8.quickstarts.camel.db.repo.crmp")
public class CrmDbConfig {

  @Autowired
  DbConfig dbConfig;

  	@Primary
	@Bean
	public DataSource dataSource() {
		
		// no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
			.setType(EmbeddedDatabaseType.HSQL) //.H2 or .DERBY
			//.addScript("db/sql/schema.sql")
			.addScript("schema-hsqldb.sql")
			.build();
		return db;
	}
	
  //@Primary
  @Bean(name = "crmpDataSource")
  @ConfigurationProperties(prefix = "crmp.datasource")
  public DataSource dataSourceCrmp() {
    org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
    dataSource.setDriverClassName(dbConfig.getDriverClassName());
    dataSource.setUrl(dbConfig.getUrl());
    dataSource.setUsername(dbConfig.getUsername());
    dataSource.setPassword(dbConfig.getPassword());
    dataSource.setTestOnBorrow(true);
    dataSource.setValidationQuery("select 1 from dual");
    return dataSource;
  }

  //@Primary
  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    EntityManagerFactoryBuilder builder,
    @Qualifier("crmpDataSource") DataSource dataSource) {
    //Map<String,Object> properties = new HashMap<>();
    //properties.put("javax.persistence.query.timeout", 10000);
    return builder
      .dataSource(dataSource)
      .packages("io.fabric8.quickstarts.camel.db.model.crmp")
      .persistenceUnit("crmp")
      //.properties(properties)
      .build();
  }

  //@Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(
    @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
