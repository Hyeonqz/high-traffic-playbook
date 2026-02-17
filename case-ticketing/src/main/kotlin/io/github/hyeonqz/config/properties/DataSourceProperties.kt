package io.github.hyeonqz.config.properties

import com.zaxxer.hikari.HikariConfig
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Master DataSource Properties
 * - spring.datasource.master.* 설정 바인딩
 */
@ConfigurationProperties(prefix = "spring.datasource.master")
data class MasterDataSourceProperties(
    var driverClassName: String = "",
    var jdbcUrl: String = "",
    var username: String = "",
    var password: String = "",
    var hikari: HikariProperties = HikariProperties()
)

/**
 * Replica DataSource Properties
 * - spring.datasource.replica.* 설정 바인딩
 */
@ConfigurationProperties(prefix = "spring.datasource.replica")
data class ReplicaDataSourceProperties(
    var driverClassName: String = "",
    var jdbcUrl: String = "",
    var username: String = "",
    var password: String = "",
    var hikari: HikariProperties = HikariProperties()
)

/**
 * HikariCP Properties
 */
data class HikariProperties(
    var poolName: String = "",
    var maximumPoolSize: Int = 10,
    var minimumIdle: Int = 5,
    var connectionTimeout: Long = 30000,
    var idleTimeout: Long = 600000,
    var maxLifetime: Long = 1800000,
    var connectionTestQuery: String = "SELECT 1",
    var readOnly: Boolean = false
) {
    fun toHikariConfig(): HikariConfig {
        return HikariConfig().apply {
            poolName = this@HikariProperties.poolName
            maximumPoolSize = this@HikariProperties.maximumPoolSize
            minimumIdle = this@HikariProperties.minimumIdle
            connectionTimeout = this@HikariProperties.connectionTimeout
            idleTimeout = this@HikariProperties.idleTimeout
            maxLifetime = this@HikariProperties.maxLifetime
            connectionTestQuery = this@HikariProperties.connectionTestQuery
            isReadOnly = this@HikariProperties.readOnly
        }
    }
}
