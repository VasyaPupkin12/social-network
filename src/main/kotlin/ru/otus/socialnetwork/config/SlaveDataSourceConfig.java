package ru.otus.socialnetwork.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SlaveDataSourceConfig {

  @Bean("masterDataSourceProperties")
  @ConfigurationProperties("spring.datasource.master")
  public DataSourceProperties masterDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean("masterDataSource")
  public DataSource masterDataSource(@Qualifier("masterDataSourceProperties") DataSourceProperties properties) {
    return properties
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean("masterJdbcTemplate")
  public JdbcTemplate masterJdbcTemplate(@Qualifier("masterDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean("slaveDataSourceProperties")
  @ConfigurationProperties("spring.datasource.slave")
  public DataSourceProperties slaveDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean("slaveDataSource")
  public DataSource slaveDataSource(@Qualifier("slaveDataSourceProperties") DataSourceProperties properties) {
    return properties
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean("slaveJdbcTemplate")
  public JdbcTemplate slaveJdbcTemplate(@Qualifier("slaveDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

}
