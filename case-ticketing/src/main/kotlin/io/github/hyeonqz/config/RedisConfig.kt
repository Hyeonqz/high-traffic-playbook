package io.github.hyeonqz.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Redis 설정
 *
 * 아키텍처:
 * - Master (localhost:6379): 읽기/쓰기
 * - Slave (localhost:6380): 읽기 전용 복제본 (부하 분산)
 * - Sentinel (localhost:26379): 자동 Failover
 *
 * Key 구분:
 * - Queue: queue:*
 * - Cache: cache:*
 *
 * [Bug Fix] LettuceClientConfigurationBuilderCustomizer 제거 이유:
 *   ReadFrom 을 설정하면 Standalone 모드에서도 Spring Data Redis가
 *   MasterReplica 모드를 강제 활성화하여 'INFO replication' 으로 replica를 자동 탐색한다.
 *   macOS + Docker Desktop 환경에서는 master가 replica 주소를 Docker 내부 IP(172.18.0.x)로 반환하여
 *   호스트에서 접근 불가 → MasterReplicaTopologyRefresh 연결 실패 발생.
 *
 *   ReadFrom 전략이 필요한 경우:
 *   - Docker 내부 네트워크에서 실행 시 (앱과 Redis가 동일 네트워크)
 *   - redis-slave에 --replica-announce-ip 127.0.0.1 --replica-announce-port 6380 설정 후
 */
@Configuration
class RedisConfig {

    /**
     * RedisTemplate 설정
     * - Spring Boot Auto-configuration이 생성한 RedisConnectionFactory 사용
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = connectionFactory

            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()

            afterPropertiesSet()
        }
    }
}
