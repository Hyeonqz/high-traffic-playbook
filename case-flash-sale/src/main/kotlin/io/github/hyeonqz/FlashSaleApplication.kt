package io.github.hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Flash Sale Application
 * 타임 세일 시스템 - 대량 트래픽 분산 중심 설계
 */
@SpringBootApplication
class FlashSaleApplication

fun main(args: Array<String>) {
    runApplication<FlashSaleApplication>(*args)
}
