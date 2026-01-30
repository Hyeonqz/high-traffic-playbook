package io.github.hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Realtime Ranking Application
 * 실시간 랭킹 시스템 - 자료구조 최적화 및 성능 최적화
 */
@SpringBootApplication
class RealtimeRankingApplication

fun main(args: Array<String>) {
    runApplication<RealtimeRankingApplication>(*args)
}
