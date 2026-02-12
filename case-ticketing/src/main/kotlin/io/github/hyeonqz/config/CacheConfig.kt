package io.github.hyeonqz.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * Spring Cache 설정
 *
 * Key Prefix:
 * - cache:* (자동으로 추가됨)
 *
 * 예시:
 * - @Cacheable(value = ["tickets"], key = "#id") → cache:tickets::1
 * - @Cacheable(value = ["users"], key = "#userId") → cache:users::123
 */
@EnableCaching
@Configuration
class CacheConfig {

    /**
     * RedisCacheManager 설정
     * - Key Prefix: cache:*
     * - TTL: 1시간 (기본값)
     */
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            // Cache Key Prefix 설정
            .prefixCacheNameWith("cache:")

            // 기본 TTL 설정
            .entryTtl(Duration.ofHours(1))

            // Null 값 캐싱 비활성화
            .disableCachingNullValues()

            // Key Serializer
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    StringRedisSerializer()
                )
            )

            // Value Serializer
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer()
                )
            )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .transactionAware()
            .build()
    }
}
