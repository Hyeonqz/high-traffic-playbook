package io.github.hyeonqz.exception

import org.slf4j.MDC
import java.time.LocalDateTime

/**
 * 공통 에러 응답 DTO.
 *
 * - traceId : CorrelationIdFilter 가 MDC 에 세팅한 X-Correlation-Id 값.
 *             클라이언트가 오류 제보 시 서버 로그와 1:1 추적 가능.
 * - timestamp : 오류 발생 시각 (ISO-8601)
 */
data class ErrorResponse(
    val code: String,
    val message: String,
    val traceId: String = MDC.get("correlationId") ?: "",
    val timestamp: String = LocalDateTime.now().toString(),
)
