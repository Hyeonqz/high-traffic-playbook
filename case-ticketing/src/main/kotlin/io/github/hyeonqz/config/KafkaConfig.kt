package io.github.hyeonqz.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

/**
 * Kafka 설정
 * - application-local.yml의 spring.kafka.* 설정 자동 적용
 * - Spring Boot Auto-configuration 활용
 * - 추가 설정: Manual ACK, Concurrency
 */
@EnableKafka
@Configuration
class KafkaConfig {

    /**
     * Kafka Listener Container Factory
     * - Spring Boot가 생성한 ConsumerFactory 활용
     * - Manual ACK 모드로 변경
     * - 3개의 Consumer Thread
     */
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        return ConcurrentKafkaListenerContainerFactory<String, Any>().apply {
            this.consumerFactory = consumerFactory
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
            setConcurrency(3)
        }
    }
}
