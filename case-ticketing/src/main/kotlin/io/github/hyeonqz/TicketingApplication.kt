package io.github.hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Ticketing Application
 * 코레일 유통 기차 선착순 예매 - 동시성 제어 및 대기열 관리 설계
 *
 * 인프라:
 * - MySQL Master-Replica (3306, 3307)
 * - Redis Master-Slave + Sentinel (6379, 6380, 26379)
 * - Kafka 3-node cluster (9092, 9093, 9094)
 *
 * Redis 구조:
 * - Master: 읽기/쓰기
 * - Slave: 읽기 전용 (REPLICA_PREFERRED)
 * - Sentinel: 자동 Failover
 */
@SpringBootApplication
class TicketingApplication

fun main(args: Array<String>) {
    runApplication<TicketingApplication>(*args)
}
