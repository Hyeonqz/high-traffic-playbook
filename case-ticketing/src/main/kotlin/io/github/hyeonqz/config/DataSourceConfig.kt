package io.github.hyeonqz.config

import com.zaxxer.hikari.HikariDataSource
import io.github.hyeonqz.config.properties.MasterDataSourceProperties
import io.github.hyeonqz.config.properties.ReplicaDataSourceProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

/**
 * MySQL Master-Replica DataSource 설정
 * - Master: Write 전용 (port 3306)
 * - Replica: Read 전용 (port 3307)
 * - @Transactional(readOnly) 기반 자동 라우팅
 * - Properties 클래스를 통한 순환 참조 방지
 */
@Configuration
@EnableConfigurationProperties(
    MasterDataSourceProperties::class,
    ReplicaDataSourceProperties::class
)
class DataSourceConfig(
    private val masterProperties: MasterDataSourceProperties,
    private val replicaProperties: ReplicaDataSourceProperties
) {

    /**
     * Master DataSource (Write)
     * - Properties 클래스를 통해 설정 주입 (순환 참조 방지)
     */
    @Bean(name = ["masterDataSource"])
    fun createMasterDataSource(): DataSource {
        val hikariConfig = masterProperties.hikari.toHikariConfig().apply {
            driverClassName = masterProperties.driverClassName
            jdbcUrl = masterProperties.jdbcUrl
            username = masterProperties.username
            password = masterProperties.password
        }
        return HikariDataSource(hikariConfig)
    }

    /**
     * Replica DataSource (Read)
     * - Properties 클래스를 통해 설정 주입 (순환 참조 방지)
     */
    @Bean(name = ["replicaDataSource"])
    fun createReplicaDataSource(): DataSource {
        val hikariConfig = replicaProperties.hikari.toHikariConfig().apply {
            driverClassName = replicaProperties.driverClassName
            jdbcUrl = replicaProperties.jdbcUrl
            username = replicaProperties.username
            password = replicaProperties.password
        }
        return HikariDataSource(hikariConfig)
    }

    /**
     * Routing DataSource
     * - @Transactional(readOnly=false) → Master
     * - @Transactional(readOnly=true) → Replica
     *
     * 빈 직접 호출 대신 @Qualifier 파라미터 주입 사용
     * - Spring 빈 싱글톤 보장
     * - AbstractRoutingDataSource.afterPropertiesSet() Spring 생명주기 정상 적용
     */
    @Bean(name = ["routingDataSource"])
    fun createRoutingDataSource(
        @Qualifier("masterDataSource") masterDataSource: DataSource,
        @Qualifier("replicaDataSource") replicaDataSource: DataSource
    ): DataSource {
        val routingDataSource = ReplicationRoutingDataSource()

        val dataSources = mapOf(
            DataSourceType.MASTER to masterDataSource,
            DataSourceType.REPLICA to replicaDataSource
        )

        routingDataSource.setTargetDataSources(dataSources as Map<Any, Any>)
        routingDataSource.setDefaultTargetDataSource(masterDataSource)

        return routingDataSource
    }

    /**
     * Primary DataSource (LazyConnectionDataSourceProxy)
     * - 트랜잭션 시작 전까지 실제 커넥션 획득을 지연
     * - @Transactional 어노테이션보다 먼저 실행되는 AOP 문제 해결
     */
    @Primary
    @Bean
    fun dataSource(@Qualifier("routingDataSource") routingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}

/**
 * DataSource 타입 Enum
 */
enum class DataSourceType {
    MASTER,  // Write 전용
    REPLICA  // Read 전용
}

/**
 * 트랜잭션 readOnly 속성 기반 DataSource 라우팅
 */
class ReplicationRoutingDataSource : AbstractRoutingDataSource() {

    override fun determineCurrentLookupKey(): Any {
        // @Transactional(readOnly=true)면 REPLICA, 아니면 MASTER
        val isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        return if (isReadOnly) DataSourceType.REPLICA else DataSourceType.MASTER
    }
}
