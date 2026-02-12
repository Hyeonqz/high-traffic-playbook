package io.github.hyeonqz.config

import io.lettuce.core.ReadFrom
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Redis Master-Slave 설정
 *
 * 아키텍처:
 * - Master (localhost:6379): 읽기/쓰기
 * - Slave (localhost:6380): 읽기 전용 (부하 분산)
 * - Sentinel (localhost:26379): 자동 Failover
 *
 * Key 구분:
 * - Queue: queue:*
 * - Cache: cache:*
 *
 * Spring Boot Redis Auto-configuration 활용:
 * - exclude 불필요
 * - Sentinel 설정 자동 적용
 */
@Configuration
class RedisConfig {

    /**
     * Lettuce 읽기 전략 설정
     * - REPLICA_PREFERRED: Slave에서 읽기 우선, Slave 없으면 Master
     *
     * 옵션:
     * - MASTER: Master에서만 읽기
     * - MASTER_PREFERRED: Master 우선, 실패 시 Slave
     * - REPLICA: Slave에서만 읽기
     * - REPLICA_PREFERRED: Slave 우선, 없으면 Master (권장)
     */
    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        return LettuceClientConfigurationBuilderCustomizer { builder ->
            builder.readFrom(ReadFrom.REPLICA_PREFERRED)
        }
    }

    /**
     * RedisTemplate 설정
     * - Spring Boot Auto-configuration이 생성한 RedisConnectionFactory 사용
     * - Sentinel 기반 Master-Slave 자동 관리
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = connectionFactory

            // Key/Value Serializer 설정
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()

            afterPropertiesSet()
        }
    }
}
