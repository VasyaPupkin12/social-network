package ru.otus.socialnetwork.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * DataSourceConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class DataSourceConfig {

  @Bean("dataSourceProperties")
  @ConfigurationProperties("spring.datasource")
  fun dataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean("dataSource")
  fun dataSource(@Qualifier("dataSourceProperties") properties: DataSourceProperties): DataSource {
    return properties
      .initializeDataSourceBuilder()
      .build()
  }

  @Bean("jdbcTemplate")
  fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
    return JdbcTemplate(dataSource)
  }
}