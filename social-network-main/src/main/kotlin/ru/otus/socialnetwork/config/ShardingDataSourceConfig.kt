package ru.otus.socialnetwork.config;

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * ShardingDataSourceConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class ShardingDataSourceConfig {

  @Profile("sharding")
  @Bean("shardingDataSourceProperties")
  @ConfigurationProperties("spring.datasource.sharding")
  fun shardingDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Profile("sharding")
  @Bean("shardingDataSource")
  fun shardingDataSource(@Qualifier("shardingDataSourceProperties") properties: DataSourceProperties): DataSource {
    return properties
        .initializeDataSourceBuilder()
        .build()
  }

  @Profile("sharding")
  @Bean("shardingJdbcTemplate")
  fun shardingJdbcTemplate(@Qualifier("shardingDataSource") dataSource: DataSource?): JdbcTemplate {
    return JdbcTemplate(dataSource!!)
  }

  @Profile("sharding")
  @Bean("shardingJdbcTemplate")
  fun shardingJdbcTemplateLocal(@Qualifier("masterDataSource") dataSource: DataSource?): JdbcTemplate {
    return JdbcTemplate(dataSource!!)
  }
}
