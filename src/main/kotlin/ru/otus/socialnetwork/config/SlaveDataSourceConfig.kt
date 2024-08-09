package ru.otus.socialnetwork.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * SlaveDataSourceConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class SlaveDataSourceConfig {

  @Bean("masterDataSourceProperties")
  @ConfigurationProperties("spring.datasource.master")
  fun masterDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean("masterDataSource")
  fun masterDataSource(@Qualifier("masterDataSourceProperties") properties: DataSourceProperties): DataSource {
    return properties
      .initializeDataSourceBuilder()
      .build()
  }

  @Bean("masterJdbcTemplate")
  fun masterJdbcTemplate(@Qualifier("masterDataSource") dataSource: DataSource?): JdbcTemplate {
    return JdbcTemplate(dataSource!!)
  }

  @Bean("slaveDataSourceProperties")
  @ConfigurationProperties("spring.datasource.slave")
  fun slaveDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean("slaveDataSource")
  fun slaveDataSource(@Qualifier("slaveDataSourceProperties") properties: DataSourceProperties): DataSource {
    return properties
      .initializeDataSourceBuilder()
      .build()
  }

  @Bean("slaveJdbcTemplate")
  fun slaveJdbcTemplate(@Qualifier("slaveDataSource") dataSource: DataSource?): JdbcTemplate {
    return JdbcTemplate(dataSource!!)
  }

  @Bean("shardingDataSourceProperties")
  @ConfigurationProperties("spring.datasource.sharding")
  fun shardingDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean("shardingDataSource")
  fun shardingDataSource(@Qualifier("shardingDataSourceProperties") properties: DataSourceProperties): DataSource {
    return properties
      .initializeDataSourceBuilder()
      .build()
  }

  @Bean("shardingJdbcTemplate")
  fun shardingJdbcTemplate(@Qualifier("shardingDataSource") dataSource: DataSource?): JdbcTemplate {
    return JdbcTemplate(dataSource!!)
  }
}